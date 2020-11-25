# coding=utf-8
from __future__ import division

import WorldModel
import GestorDeObra
import socket
import json

import threading

print_lock = threading.Lock()


def printstring(string):
    print_lock.acquire()
    print string
    print_lock.release()


class Executor(object):
    def __init__(self, comandos, connection, posicion=None):
        self.comandos = comandos
        self.connection = connection
        self.posicion = posicion
        self.worldmodel = WorldModel.WorldModel.getinstance()

    def executeactions(self):

        return_message = GestorDeObra.ExecutorReturn(False, self.connection.theta, None)
        if self.posicion is not None:
            return_message.new_position = self.posicion.NodeId

        if self.connection.robot == "nao":

            import sys
            sys.path.insert(0, "../")

            from Nao import Executor

            nao = Executor(self.comandos, self.connection)
            # Se inicializa el valor en caso de no haber desplazamiento, no cambia el ángulo

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

        else:

            try:
                # Verificar que exista trayectoria para recorrer
                for i in range(len(self.comandos)):

                    if self.comandos[i].commandname == "avanzar":

                        for parametro in self.comandos[i].parameters:

                            if parametro.parametername == "avanzar":
                                # Generar trayectoria desde el modelo del mundo
                                path = WorldModel.WorldModel.dijkstra(int(self.posicion.NodeId) - 1,
                                                                      int(parametro.values) - 1)

                                # Ahora el valor del parametro son los nodos que componen la trayectoria
                                self.posicion = parametro.values
                                for j in range(len(path)):
                                    path[j] = path[j].nodeid
                                parametro.values = path
                name = self.connection.robot
                printstring("Construyendo archivo json para envío de comandos al sistema del robot {}".format(name))
                # comandos_json = json.dumps(data_wrap.__dict__, lambda o: o.__dict__, indent=4)
                comandos_json = json.dumps([o.dump() for o in self.comandos], indent=4)
                printstring("Creando socket para el envío de comandos...")
                client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                client_socket.connect((self.connection.ip, self.connection.port))

                # Envío de los comandos al sistema del robot
                client_socket.send(comandos_json)
                printstring("Comandos enviados exitosamente")

                # Recepción del estado del robot
                message = client_socket.recv(1024)

                jdata = json.loads(message)

                if "estado" in jdata:
                    return_message.status = json.dumps(jdata["estado"])

                if "theta" in jdata:
                    return_message.new_theta = json.dumps(jdata["theta"])

                return return_message

            except socket.error as err:
                printstring("Ocurrio un error al enviar el mensaje al robot, por favor verificar la conexion")
                printstring("Por favor verificar el estado de la conexión en el robot para recepción de comandos")
                printstring("Error: \n{}".format(err))
                return return_message
            except ValueError as err:
                printstring("Ocurrio un error inesperado, por favor verificar la conexion")
                printstring("Error: \n{}".format(err))
                return return_message

    def stopTask(self, returned_message):

        if returned_message is not None:
            if self.connection.robot == "nao":
                import sys
                sys.path.insert(0, "../")

                from Nao import Executor

                nao = Executor(self.comandos, self.connection)
                nao.stopTask(returned_message.processid)
