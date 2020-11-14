# coding=utf-8
from __future__ import division

import WorldModel
import GestorDeObra


class Executor(object):
    def __init__(self, comandos, connection, posicion=None):
        self.comandos = comandos
        self.connection = connection
        self.posicion = posicion
        self.worldmodel = WorldModel.WorldModel.getinstance()

    def executeactions(self):

        if self.connection.robot == "nao":

            import sys
            sys.path.insert(0, "../")

            from Nao import Executor

            nao = Executor(self.comandos, self.connection)
            # Se inicializa el valor en caso de no haber desplazamiento, no cambia el ángulo

            return_message = GestorDeObra.ExecutorReturn(False, self.connection.theta, None)
            if self.posicion is not None:
                return_message.new_position = self.posicion.NodeId

            for i in range(len(self.comandos)):

                if self.comandos[i].commandname == "avanzar":

                    for parametro in self.comandos[i].parameters:

                        if parametro.parametername == "avanzar":

                            path = WorldModel.WorldModel.dijkstra(int(self.posicion.NodeId) - 1,
                                                                  int(parametro.values) - 1)

                            # print "el path es: {}".format(path)
                            returns, theta = nao.avanzar(self.comandos[i], path)
                            return_message.status = returns
                            return_message.new_theta = theta

                            if returns:
                                return_message.new_position = int(parametro.values)

                if self.comandos[i].commandname == "rotar":

                    returns, theta = nao.rotar(self.comandos[i])
                    return_message.status = returns

                    if returns:
                        return_message.new_theta = theta

                if self.comandos[i].commandname == "reproducir":

                    return_message.status = nao.reproducir(self.comandos[i])

                if self.comandos[i].commandname == "mirar":

                    return_message.status = nao.mirar(self.comandos[i])

                if self.comandos[i].commandname == "signo" or self.comandos[i].commandname == "emergent":

                    return_message.status, return_message.processid = nao.signo_o_emergente(self.comandos[i])

            return return_message

        elif self.connection.robot == "quyca":

            # Enviar datos por socket (pendiente)
            x = 0
            return True

    def stopTask(self, returned_message):

        if returned_message is not None:
            if self.connection.robot == "nao":
                import sys
                sys.path.insert(0, "../")

                from Nao import Executor

                nao = Executor(self.comandos, self.connection)
                nao.stopTask(returned_message.processid)
