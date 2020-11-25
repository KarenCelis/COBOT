# coding=utf-8
from naoqi import ALProxy
import math
import time
import os


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
        print "La posición inicial del robot es {} grados".format(self.connection.theta)

    def stopTask(self, identifiers):
        print 'deteniendo acciones de Nao'
        motion = setconnection(self.connection, "ALMotion")
        for identifier in identifiers:
            if ALProxy.isRunning(motion, identifier):
                ALProxy.stop(motion, identifier)
        self.applyPosture()

    def applyPosture(self):
        motionproxy = setconnection(self.connection, "ALRobotPosture")
        motionproxy.goToPosture('stand', 0.1)

    def setleds(self, color, fadetime):
        # ledproxy.fadeRGB("AllLeds", '#%02x%02x%02x' % (leds[0], leds[1], leds[-1]), 10)
        ledproxy = setconnection(self.connection, "ALLeds")
        processid = ledproxy.post.fadeRGB("AllLeds", color, fadetime)
        return processid

    def setsound(self, audiofile, volume, folder):
        soundproxy = setconnection(self.connection, "ALAudioPlayer")

        print os.path.abspath(os.getcwd())
        head, tail = os.path.split(os.path.abspath(os.getcwd()))
        processid = soundproxy.post.playFile("{}/Sounds/{}/{}".format(head, folder, audiofile), volume, 0.0)
        return processid

    def avanzar(self, command, path):

        try:

            motionproxy = setconnection(self.connection, "ALMotion")
            posture_service = setconnection(self.connection, "ALRobotPosture")

            if motionproxy is None or posture_service is None:
                return False, self.connection.theta
            else:

                posture_service.goToPosture("Stand", 0.01)

                speed = 1
                ispitch = False
                sound = None
                pitch_angle = math.radians(10.0)

                for parameter in command.parameters:
                    if parameter.parametername == "speed":
                        speed = parameter.values
                    elif parameter.parametername == "ledcolor":
                        leds = parameter.values
                        self.setleds('red', 5)
                        print "El color del led para el robot es {}".format(leds)
                    elif parameter.parametername == "HeadPitch":
                        ispitch = True
                        pitch_angle = math.radians(parameter.values)
                    elif parameter.parametername == "sound":
                        self.setsound(parameter.values, speed, "Sounds")
                        sound = parameter.values

                if ispitch:
                    motionproxy.setAngles(["HeadPitch"], [pitch_angle], speed)

                for i in range(len(path) - 1):

                    theta_result = gettheta(self.connection.theta, path[i], path[i + 1])
                    print "¡nao va a girar {} grados".format(theta_result)

                    processid = motionproxy.post.moveTo(0, 0, math.radians(theta_result))
                    motionproxy.wait(processid, 0)
                    if sound is not None:
                        self.setsound(sound, speed, "Sounds")

                    processid = motionproxy.moveTo(distance(path[i], path[i + 1]), 0, 0)
                    motionproxy.wait(processid, 0)
                    if sound is not None:
                        self.setsound(sound, speed, "Sounds")

                    self.connection.theta = self.connection.theta + theta_result

                return True, self.connection.theta

        except RuntimeError, e:
            print "Hubo un error al ejecutar el comando avanzar, por favor revisar los valores ingresados como " \
                  "parametros "
            print e.message
            return False, self.connection.theta

    def reproducir_tts(self, command):

        try:

            speakproxy = setconnection(self.connection, "ALTextToSpeech")
            posture_service = setconnection(self.connection, "ALRobotPosture")

            if speakproxy is None or posture_service is None:
                return False
            else:
                posture_service.goToPosture("Stand", 0.01)
                speed = 100
                pitch = 0
                dialog = "Hola soy un robot actor!"

                for parameter in command.parameters:
                    if parameter.parametername == "speed":
                        speed = parameter.values
                    elif parameter.parametername == "pitch":
                        pitch = parameter.values
                    elif parameter.parametername == "reproducir":
                        dialog = parameter.values
                    elif parameter.parametername == "ledcolor":
                        self.setleds('red', 5)
                        leds = parameter.values
                        print "El color del led para el robot es {}".format(leds)

                speakproxy.setLanguage("Spanish")
                speakproxy.setParameter("speed", speed)
                speakproxy.setParameter("pitchShift", pitch)

                speakproxy.post.say(dialog)
                posture_service.post.goToPosture("Stand", 0.01)

                return True

        except RuntimeError, e:
            print "Hubo un error al ejecutar el comando reproducir, por favor revisar los valores ingresados como " \
                  "parametros "
            print e.message
            return False

    def reproducir(self, command):
        try:

            soundproxy = setconnection(self.connection, "ALAudioPlayer")
            motionproxy = setconnection(self.connection, "ALMotion")
            posture_service = setconnection(self.connection, "ALRobotPosture")

            if soundproxy is None or posture_service is None:
                return False
            else:
                posture_service.goToPosture("Stand", 0.01)
                # Valor por defecto de la velocidad al hablar
                speed = 1
                ispitch = False
                pitch_angle = math.radians(10.0)

                for parameter in command.parameters:
                    if parameter.parametername == "speed":
                        speed = parameter.values
                    elif parameter.parametername == "sound":
                        self.setsound(parameter.values, speed, "Dialogs")
                    elif parameter.parametername == "ledcolor":
                        self.setleds('red', 5)
                        leds = parameter.values
                        print "El color del led para el robot es {}".format(leds)
                    elif parameter.parametername == "HeadPitch":
                        ispitch = True
                        pitch_angle = math.radians(parameter.values)

                if ispitch:
                    motionproxy.setAngles(["HeadPitch"], [pitch_angle], speed)

                posture_service.post.goToPosture("Stand", 0.01)
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

            if motionproxy is None or posture_service is None:
                return False, self.connection.theta
            else:

                posture_service.goToPosture("Stand", 0.01)
                theta_result = 90
                speed = 1

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
                        leds = parameter.values
                        self.setleds('red', 5)
                        print "El color del led para el robot es {}".format(leds)
                    elif parameter.parametername == "HeadPitch":
                        ispitch = True
                        pitch_angle = math.radians(parameter.values)
                    elif parameter.parametername == "sound":
                        self.setsound(parameter.values, speed, "Sounds")

                if ispitch:
                    motionproxy.setAngles(["HeadPitch"], [pitch_angle], speed)

                print "nao va a girar {} grados" .format(theta_result)

                motionproxy.moveInit()
                motionproxy.post.moveTo(0, 0, math.radians(theta_result))

                self.connection.theta = rotate(self.connection.theta, theta_result)
                posture_service.post.goToPosture("Stand", 0.01)

            return True, self.connection.theta

        except RuntimeError, e:
            print "Hubo un error al ejecutar el comando rotar, por favor revisar los valores ingresados como parametros"
            print e.message
            return False, self.connection.theta

    def mirar(self, command):
        try:
            motionproxy = setconnection(self.connection, "ALMotion")
            posture_service = setconnection(self.connection, "ALRobotPosture")

            if motionproxy is None or posture_service is None:
                return False
            else:

                names = []
                angle_lists = []
                times = []
                isabsolute = True

                posture_service.goToPosture("Stand", 0.01)

                for parameter in command.parameters:

                    if parameter.parametername == "sound":
                        self.setsound(parameter.values, 1, "Sounds")

                    elif str.endswith(parameter.parametername.encode(), "_time"):
                        times.append([parameter.values])

                    elif parameter.parametername == "HeadYaw":

                        if parameter.values == 0:
                            theta_result = rotatehead(self.connection.theta)
                        else:
                            theta_result = parameter.values

                        angle_lists.append([math.radians(theta_result)])
                        names.append(parameter.parametername.encode())

                    elif parameter.parametername == "ledcolor":
                        leds = parameter.values
                        self.setleds('red', 5)
                        print "El color del led para el robot es {}".format(leds)

                    else:

                        names.append(parameter.parametername.encode())
                        angle_lists.append([math.radians(parameter.values)])

                # print names
                # print angle_lists
                # print times

                motionproxy.post.angleInterpolation(names, angle_lists, times, isabsolute)
                posture_service.post.goToPosture("Stand", 0.01)

                return True

        except RuntimeError, e:
            print "Hubo un error al ejecutar el comando mirar, por favor revisar los valores ingresados como parametros"
            print e.message
            return False

    def signo_o_emergente(self, command):

        try:
            processid = []
            motion_service = setconnection(self.connection, "ALMotion")
            posture_service = setconnection(self.connection, "ALRobotPosture")

            if motion_service is None or posture_service is None:
                return False, []
            else:

                motion_service.wakeUp()
                posture_service.goToPosture("Stand", 0.01)

                names = []
                angle_lists = []
                times = []
                is_absolute = True
                dialog = "hola soy un robot actor"
                isdialog = False

                for parameter in command.parameters:
                    # Si el nombre del parámetro termina con time, entonces ubicar el tiempo en el arreglo paralelo
                    if str.endswith(parameter.parametername.encode(), "_time"):
                        times.append(parameter.values)
                    elif parameter.parametername == "ledcolor":
                        processid.append(self.setleds('red', 5))
                        leds = parameter.values
                        print "El color del led para el robot es {}".format(leds)
                    elif parameter.parametername == "reproducir":
                        isdialog = True
                        dialog = parameter.values
                    elif parameter.parametername == "sound":
                        processid.append(self.setsound(parameter.values, 1, "Sounds"))
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

                processid.append(motion_service.post.angleInterpolation(names, angle_lists, times, is_absolute))

                if isdialog:
                    speakproxy = setconnection(self.connection, "ALTextToSpeech")
                    speakproxy.setLanguage("Spanish")
                    speakproxy.post.say(dialog.encode())

                posture_service.post.goToPosture("Stand", 0.5)

                return True, processid

        except RuntimeError, e:
            print "Hubo un error al ejecutar el comando signo o emergente, " \
                  "por favor revisar los valores ingresados como parametros"
            print e.message
            return False, []


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
