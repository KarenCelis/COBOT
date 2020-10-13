# coding=utf-8
from naoqi import ALProxy
import math
import time


def gettheta(init, a, b):
    x = (b.xaxis - a.xaxis) * 1.0
    y = (b.yaxis - a.yaxis) * 1.0

    if x + y == 0:
        return False

    if y == 0:
        if x > 0:
            if abs(init) < 90:
                return (90 + init) * -1
            elif init < -90:
                return (90 - init) * -1
            else:
                return 270 - init
        else:
            if abs(init) < 90:
                return 90 - init
            elif init < -90:
                return 270 + init
            else:
                return 90 - init
    elif x == 0:
        if y > 0:
            return -init
        elif init < 0:
            return (180 + init) * -1
        else:
            return 180 - init
    else:
        # tanto x como y tienen valores
        angle = math.degrees(math.atan(y / x))
        print "angulo es %s" % angle
        dstn = 0
        if x > 0 and y > 0:
            dstn = angle - 90
            print "angulo destino es %s" % dstn
            if dstn - init < -180:
                return -360 - dstn + init
        elif x < 0 < y:
            dstn = angle + 90
            print "angulo destino es %s" % dstn
            if dstn - init > 180:
                return -360 + dstn - init
        elif x < 0 and y < 0:
            dstn = angle + 90
            print "angulo destino es %s" % dstn
            if dstn - init > 180:
                return - 360 + dstn - init
        elif x > 0 > y:
            dstn = angle - 90
            print "angulo destino es %s" % dstn
            if dstn - init < -180:
                return - 360 - dstn + init
            else:
                return init - dstn

        return dstn - init


def distance(first, second):
    return round(math.sqrt(pow(second.xaxis - first.xaxis, 2) + pow(second.yaxis - first.yaxis, 2)), 2)


def setconnection(connection, altype):
    try:
        # return ALProxy(altype, connection.ip, connection.port)
        motionproxy = ALProxy(altype, "127.0.0.1", 9559)
        return motionproxy
    except Exception, e:
        print "Could not create proxy to {} ", format(altype)
        print "Error was: ", e
        return None


class Executor(object):

    def __init__(self, commands, connection):
        self.commands = commands
        self.connection = connection

    def avanzar(self, comand, path):
        motionproxy = setconnection(self.connection, "ALMotion")

        if motionproxy is None:
            return False, self.connection.theta
        else:
            frequency = 1
            for parameter in comand.parameters:
                if parameter.parametername == "speed":
                    frequency = parameter.values

            for i in range(len(path) - 1):
                theta = gettheta(self.connection.theta, path[i], path[i + 1])
                # primero el robot se gira para quedar mirando frente al nodo destino
                print "¡nao va a girar %s " % theta,
                print " que son %s radianes!" % math.radians(theta)
                processid = motionproxy.moveTo(0, 0, math.radians(theta))
                motionproxy.wait(processid, 0)
                # luego el robot se mueve en línea recta hacia el nodo destino, al frente es en el eje y del plano
                # cartesiano
                processid = motionproxy.moveTo(distance(path[i], path[i + 1]), 0, 0)
                motionproxy.wait(processid, 0)
                self.connection.theta = self.connection.theta + theta

            return True, self.connection.theta


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

    theta = gettheta(init, v1, v2)
    print theta
    init = init + theta
    theta = gettheta(init, v2, v3)
    print theta
    init = init + theta
    theta = gettheta(init, v3, v4)
    print theta
    init = init + theta
    print("--- %s seconds ---" % (time.time() - start_time))
