from naoqi import ALProxy


def setconnection(connection, altype):
    try:
        # return ALProxy(altype, connection.ip, connection.port)
        return ALProxy(altype, "127.0.0.1", 9559)
    except Exception, e:
        print "Could not create proxy to {} ", format(altype)
        print "Error was: ", e


def avanzar(comand, connection):
    motionproxy = setconnection(connection, "ALMotion")

    x = 1
    y = 1
    theta = 0
    frequency = 1

    for parameter in comand.parameters:
        if parameter.parametername == "speed":
            frequency = parameter.values
        if parameter.parametername == "x":
            x = parameter.values
        if parameter.parametername == "y":
            y = parameter.values

    motionproxy.moveTo(x, y, theta, frequency)
    return "--done--"


class Executor(object):

    def __init__(self, commands, connection):
        self.commands = commands
        self.connection = connection

    def execute(self):
        for command in self.commands:
            print(command.commandname(command, self.connection))
