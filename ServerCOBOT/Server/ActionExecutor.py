# coding=utf-8
import WorldModel


class Executor(object):
    def __init__(self, comandos, connection, posicion=None):
        self.comandos = comandos
        self.connection = connection
        self.posicion = posicion
        self.worldmodel = WorldModel.WorldModel.getinstance()

    def executeactions(self):

        if self.connection.robot == "nao":

            from Nao import NaoExecutor
            nao = NaoExecutor.Executor(self.comandos, self.connection)
            nao.execute()

        elif self.connection.robot == "quyca":

            # Enviar datos por socket (pendiente)
            x = 0
