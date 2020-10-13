# coding=utf-8
from __future__ import division

import WorldModel


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
            theta = self.connection.theta
            position = int(self.posicion.NodeId)

            for i in range(len(self.comandos)):

                if self.comandos[i].commandname == "avanzar":
                    for parametro in self.comandos[i].parameters:
                        if parametro.parametername == "avanzar":
                            path = WorldModel.WorldModel.dijkstra(int(self.posicion.NodeId) - 1,
                                                                  int(parametro.values) - 1)
                            result, theta = nao.avanzar(self.comandos[i], path)
                            position = parametro.values

                if self.comandos[i].commandname == "reproducir":
                    print "código para reproducir..."
                if self.comandos[i].commandname == "mirar":
                    print "código para mirar..."

            return True, theta, position

        elif self.connection.robot == "quyca":

            # Enviar datos por socket (pendiente)
            x = 0
            return True
