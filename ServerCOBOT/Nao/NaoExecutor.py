# coding=utf-8
from naoqi import ALProxy
import math
import time


def rotatefront(initpos):
    if initpos != 0.0:
        return -initpos
    return 0.0


def rotatehead(initpos):
    if abs(initpos) > 119.5 and initpos != 0.0:
        return 0.0
    return -initpos


def rotate(initpos, theta_result):
    if initpos + theta_result > 180 or initpos + theta_result < -180:
        return -abs(initpos - theta_result)

    return initpos + theta_result


def gettheta(initpos, a, b):
    x = (b.xaxis - a.xaxis) * 1.0
    y = (b.yaxis - a.yaxis) * 1.0

    if x + y == 0:
        return False

    if y == 0:
        if x > 0:
            if abs(initpos) < 90:
                return (90 + initpos) * -1
            elif initpos < -90:
                return (90 - initpos) * -1
            elif initpos == -90:
                return 0
            else:
                return initpos - 270
        else:
            if abs(initpos) < 90:
                return 90 - initpos
            elif initpos < -90:
                return 270 + initpos
            elif initpos == -90:
                return initpos + 270
            else:
                return 0

    elif x == 0:
        if y > 0:
            return -initpos
        elif initpos < 0:
            return (180 + initpos) * -1
        else:
            return 180 - initpos
    else:
        # tanto x como y tienen valores
        angle = math.degrees(math.atan(y / x))
        dstn = 0
        if x > 0 and y > 0:
            dstn = angle - 90
            if dstn - initpos < -180:
                return -360 - dstn + initpos
        elif x < 0 < y:
            dstn = angle + 90
            if dstn - initpos > 180:
                return -360 + dstn - initpos
        elif x < 0 and y < 0:
            dstn = angle + 90
            if dstn - initpos > 180:
                return - 360 + dstn - initpos
        elif x > 0 > y:
            dstn = angle - 90
            if dstn - initpos < -180:
                return - 360 - dstn + initpos
            else:
                return initpos - dstn

        return dstn - initpos


def distance(first, second):
    return round(math.sqrt(pow(second.xaxis - first.xaxis, 2) + pow(second.yaxis - first.yaxis, 2)), 2)


def setconnection(connection, altype):
    try:
        # return ALProxy(altype, connection.ip, connection.port)
        return ALProxy(altype, connection.ip.encode(), int(connection.port))
    except Exception, e:
        print "Could not create proxy to {} ".format(altype)
        print "Error was: ", e
        return None


class Executor(object):

    def __init__(self, commands, connection):
        self.commands = commands
        self.connection = connection
        print "La posición inicial es {} grados".format(self.connection.theta)

    def avanzar(self, command, path):

        try:

            motionproxy = setconnection(self.connection, "ALMotion")
            posture_service = setconnection(self.connection, "ALRobotPosture")

            if motionproxy is None:
                return False, self.connection.theta
            else:

                posture_service.goToPosture("Stand", 0.01)

                speed = 1

                isleds = False
                leds = 'red'

                ispitch = False
                pitch_angle = math.radians(10.0)

                for parameter in command.parameters:
                    if parameter.parametername == "speed":
                        speed = parameter.values
                    elif parameter.parametername == "ledcolor":
                        isleds = True
                        leds = parameter.values
                    elif parameter.parametername == "HeadPitch":
                        ispitch = True
                        pitch_angle = math.radians(parameter.values)

                if ispitch:
                    motionproxy.setAngles(["HeadPitch"], [pitch_angle], speed)

                for i in range(len(path) - 1):
                    theta_result = gettheta(self.connection.theta, path[i], path[i + 1])
                    # primero el robot se gira para quedar mirando frente al nodo destino
                    print "¡nao va a girar {} grados".format(theta_result)
                    # print " que son %s radianes!" % math.radians(theta_result)

                    if isleds:
                        ledproxy = setconnection(self.connection, "ALLeds")
                        # ledproxy.fadeRGB("AllLeds", '#%02x%02x%02x' % (leds[0], leds[1], leds[-1]), 10)
                        ledproxy.post.fadeRGB("AllLeds", 'red', 5)

                    # motionproxy.moveInit()
                    processid = motionproxy.post.moveTo(0, 0, math.radians(theta_result))
                    motionproxy.wait(processid, 0)
                    # luego el robot se mueve en línea recta hacia el nodo destino, al frente es en el eje y del plano
                    # cartesiano
                    processid = motionproxy.moveTo(distance(path[i], path[i + 1]), 0, 0)
                    motionproxy.wait(processid, 0)
                    self.connection.theta = self.connection.theta + theta_result

                posture_service.goToPosture('Stand', 0.5)
                return True, self.connection.theta

        except RuntimeError, e:
            print "Hubo un error al ejecutar el comando avanzar, por favor revisar los valores ingresados como " \
                  "parametros "
            print e.message
            return False, self.connection.theta

    def reproducir(self, command):

        try:

            speakproxy = setconnection(self.connection, "ALTextToSpeech")
            posture_service = setconnection(self.connection, "ALRobotPosture")

            if speakproxy is None:
                return False
            else:
                posture_service.goToPosture("Stand", 0.01)
                # Valor por defecto de la velocidad al hablar
                speed = 100
                # Valor por defecto del tono de la voz
                pitch = 0
                # Diálogo por defecto del robot
                dialog = "Hola soy un robot actor!"
                isleds = False
                leds = 'red'

                for parameter in command.parameters:
                    if parameter.parametername == "speed":
                        speed = parameter.values
                    elif parameter.parametername == "pitch":
                        pitch = parameter.values
                    elif parameter.parametername == "reproducir":
                        dialog = parameter.values
                    elif parameter.parametername == "ledcolor":
                        isleds = True
                        leds = parameter.values

                if isleds:
                    ledproxy = setconnection(self.connection, "ALLeds")
                    # ledproxy.fadeRGB("AllLeds", '#%02x%02x%02x' % (leds[0], leds[1], leds[-1]), 10)
                    ledproxy.fadeRGB("AllLeds", 'red', 5)

                speakproxy.setLanguage("Spanish")
                speakproxy.setParameter("speed", speed)
                speakproxy.setParameter("pitchShift", pitch)

                speakproxy.post.say(dialog)

                return True

        except RuntimeError, e:
            print "Hubo un error al ejecutar el comando reproducir, por favor revisar los valores ingresados como " \
                  "parametros "
            print e.message
            return False

    def rotar(self, command):

        try:

            motionproxy = setconnection(self.connection, "ALMotion")
            posture_service = setconnection(self.connection, "ALRobotPosture")

            if motionproxy is None:
                return False, self.connection.theta
            else:

                posture_service.goToPosture("Stand", 0.01)
                # Ángulo de giro por defecto
                theta_result = 90
                speed = 1

                isleds = False
                leds = 'red'

                ispitch = False
                pitch_angle = math.radians(10.0)

                for parameter in command.parameters:
                    if parameter.parametername == "speed":
                        speed = parameter.values
                    elif parameter.parametername == "body_rotation":
                        if parameter.values[-1] == 0:
                            theta_result = rotatefront(self.connection.theta)
                        else:
                            theta_result = parameter.values[-1]
                    elif parameter.parametername == "ledcolor":
                        isleds = True
                        leds = parameter.values
                    elif parameter.parametername == "HeadPitch":
                        ispitch = True
                        pitch_angle = math.radians(parameter.values)

                if ispitch:
                    motionproxy.setAngles(["HeadPitch"], [pitch_angle], speed)

                # primero el robot se gira para quedar mirando frente al nodo destino
                # print "¡nao va a girar %s " % theta_result,
                # print " que son %s radianes!" % math.radians(theta_result)
                if isleds:
                    ledproxy = setconnection(self.connection, "ALLeds")
                    # ledproxy.fadeRGB("AllLeds", '#%02x%02x%02x' % (leds[0], leds[1], leds[-1]), 10)
                    ledproxy.post.fadeRGB("AllLeds", 'red', 5)

                motionproxy.moveInit()
                motionproxy.post.moveTo(0, 0, math.radians(theta_result))
                self.connection.theta = rotate(self.connection.theta, theta_result)

            return True, self.connection.theta

        except RuntimeError, e:
            print "Hubo un error al ejecutar el comando rotar, por favor revisar los valores ingresados como parametros"
            print e.message
            return False, self.connection.theta

    def mirar(self, command):
        try:
            motionproxy = setconnection(self.connection, "ALMotion")
            posture_service = setconnection(self.connection, "ALRobotPosture")

            if motionproxy is None:
                return False
            else:

                theta_result = 0.0

                names = []
                angle_lists = []
                times = []
                isabsolute = True

                isleds = False
                leds = 'red'

                posture_service.goToPosture("Stand", 0.01)
                for parameter in command.parameters:
                    if str.endswith(parameter.parametername.encode(), "_time"):
                        times.append([parameter.values])
                    elif parameter.parametername == "HeadYaw":

                        if parameter.values == 0:
                            theta_result = rotatehead(self.connection.theta)
                        else:
                            theta_result = parameter.values

                        angle_lists.append([math.radians(theta_result)])
                        names.append(parameter.parametername.encode())

                    elif parameter.parametername == "ledcolor":

                        isleds = True
                        leds = parameter.values

                    else:

                        names.append(parameter.parametername.encode())
                        angle_lists.append([math.radians(parameter.values)])

                # print "¡nao va a girar %s " % theta_result,
                # print " que son %s radianes!" % math.radians(theta_result)

                if isleds:
                    ledproxy = setconnection(self.connection, "ALLeds")
                    # ledproxy.fadeRGB("AllLeds", '#%02x%02x%02x' % (leds[0], leds[1], leds[-1]), 10)
                    ledproxy.post.fadeRGB("AllLeds", 'red', 5)

                print names
                print angle_lists
                print times

                motionproxy.post.angleInterpolation(names, angle_lists, times, isabsolute)
                posture_service.goToPosture("Stand", 0.01)

                return True

        except RuntimeError, e:
            print "Hubo un error al ejecutar el comando mirar, por favor revisar los valores ingresados como parametros"
            print e.message
            return False

    def signo_o_emergente(self, command):

        try:
            motion_service = setconnection(self.connection, "ALMotion")
            posture_service = setconnection(self.connection, "ALRobotPosture")

            if motion_service is None:
                return False
            else:

                motion_service.wakeUp()
                posture_service.goToPosture("Stand", 0.01)

                names = []
                angle_lists = []
                times = []
                is_absolute = True
                dialog = "hola soy un robot actor"
                isdialog = False
                isleds = False
                leds = []

                for parameter in command.parameters:
                    # Si el nombre del parámetro termina con time, entonces ubicar el tiempo en el arreglo paralelo
                    if str.endswith(parameter.parametername.encode(), "_time"):
                        times.append(parameter.values)
                    elif parameter.parametername == "ledcolor":
                        isleds = True
                        leds = parameter.values
                    elif parameter.parametername == "reproducir":
                        isdialog = True
                        dialog = parameter.values
                    else:
                        names.append(parameter.parametername.encode())
                        values_radians = []
                        if parameter.parametername == "RHand" or parameter.parametername == "LHand":
                            for i in range(0, len(parameter.values)):
                                values_radians.append(parameter.values[i])
                        else:
                            for i in range(0, len(parameter.values)):
                                values_radians.append(math.radians(parameter.values[i]))
                        angle_lists.append(values_radians)

                motion_service.post.angleInterpolation(names, angle_lists, times, is_absolute)

                if isdialog:
                    speakproxy = setconnection(self.connection, "ALTextToSpeech")
                    speakproxy.setLanguage("Spanish")
                    speakproxy.post.say(dialog.encode())

                if isleds:
                    ledproxy = setconnection(self.connection, "ALLeds")
                    # ledproxy.fadeRGB("AllLeds", '#%02x%02x%02x' % (leds[0], leds[1], leds[-1]), 10)
                    ledproxy.post.fadeRGB("AllLeds", 'red', 5)

                    # print '#%02x%02x%02x' % (leds[0], leds[1], leds[-1])

                posture_service.goToPosture("Stand", 0.01)

                return True

        except RuntimeError, e:
            print "Hubo un error al ejecutar el comando signo, por favor revisar los valores ingresados como parametros"
            print e.message
            return False


class Vertex:
    def __init__(self, nodeid, name, xaxis, yaxis):
        self.nodeid = nodeid
        self.name = name
        self.xaxis = xaxis
        self.yaxis = yaxis


if __name__ == "__main__":
    start_time = time.time()
    # x = 5 - 3 = 2, y = 5 - 4 = 1
    # atan (1/2) = 26.57
    # 26.57 - 90 = -63.43 = dstn
    # r = -63.43 - - 70 = 6.57
    # x = -2 - 3 = -5, y = 5 - 4 = 1
    # atan (1/-5) = -11.31
    # -11.31 + 90 = 78.69 = dstn
    # r = -63.43 - - 70 = 6.57
    v1 = Vertex(1, "cero", 1, 3)
    v2 = Vertex(2, "dos", 1, 2)
    v3 = Vertex(1, "cuatro", 1, 1)
    v4 = Vertex(2, "cinco", 1, 0)

    init = 0

    theta_test = gettheta(init, v1, v2)
    print theta_test
    init = init + theta_test
    theta = gettheta(init, v2, v3)
    print theta_test
    init = init + theta_test
    theta_test = gettheta(init, v3, v4)
    print theta_test
    init = init + theta_test
    print("--- %s seconds ---" % (time.time() - start_time))
