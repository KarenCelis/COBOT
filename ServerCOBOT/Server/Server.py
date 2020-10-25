#!/usr/bin/env python
# -*- coding: utf-8 -*-
import socket
import json
import GestorDeObra
import threading


class Server(object):
    def __init__(self):
        self.gestores = []
        self.direcciones = []
        self.timers = []

    def ejecutarsignosdevida(self, indice):
        self.gestores[indice].sendsingsoflife()
        self.timers[indice] = threading.Timer(10, self.ejecutarsignosdevida, [indice])
        if self.gestores[indice].momento == GestorDeObra.INACTIVO:
            starttimer(self.timers[indice])


def starttimer(timer):
    timer.start()


def canceltimer(timer):
    timer.cancel()


def main():
    host = None
    servidor = Server()
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    buffer_size = 4096

    serversocket.bind((socket.gethostname(), 1235))
    serversocket.listen(10)

    while True:
        try:
            print("Servidor escuchando!")
            clientsocket, address = serversocket.accept()
            print("Conexión recibida desde %s..." % str(address))
            host, port = clientsocket.getpeername()

            clientsocket.settimeout(5)
            data = clientsocket.recv(buffer_size).decode('utf-8')

            if not data:
                print("El contenido del mensaje está vacío, verifica la conexión")
            else:

                jdata = json.loads(data)
                option = 0

                if "code" in jdata:
                    option = int(json.dumps(jdata["code"]))

                if option != 0:
                    if "data" in jdata:
                        data = json.dumps(jdata["data"])

                    if host in servidor.direcciones:
                        print("accediendo al gestor de ", host)
                        indice = servidor.direcciones.index(host)

                        if servidor.timers[indice].is_alive() and (option == 4 or option == 5):
                            canceltimer(servidor.timers[indice])
                            print "cancelando timer para el gestor {}".format(servidor.direcciones[indice])

                        result = servidor.gestores[indice].loadcontent(json.loads(data), option)

                        if servidor.gestores[indice].momento == GestorDeObra.INACTIVO:
                            # if not servidor.timers[indice].is_alive():
                            servidor.timers[indice] = threading.Timer(10, servidor.ejecutarsignosdevida, [indice])
                            starttimer(servidor.timers[indice])
                            print "iniciando timer para el gestor {}".format(servidor.direcciones[indice])

                        if result == GestorDeObra.ACCIONES_ENTRANTES_EJECUTADAS:
                            clientsocket.send("acciones ejecutadas exitosamente")
                        elif result == GestorDeObra.ACCIONES_ENTRANTES_BLOQUEADAS:
                            clientsocket.send("acciones bloqueadas, intente de nuevo")
                    else:
                        print("creando un nuevo gestor con ip ", host)
                        servidor.direcciones.append(host)
                        servidor.gestores.append(GestorDeObra.Gestor(host))

                        indice = len(servidor.direcciones) - 1
                        servidor.timers.append(threading.Timer(10, servidor.ejecutarsignosdevida, [indice]))

                        servidor.gestores[-1].loadcontent(json.loads(data), option)

                    clientsocket.send("información enviada")
                else:
                    clientsocket.send("Servidor conectado")
            clientsocket.close()

        except socket.timeout as timeout:
            print("Se perdió comunicación con la aplicacion o tardó demasiado en responder para la ip: "),
            print(host)
            print("error: ", timeout)
        except ValueError as err:
            print("Ocurrió un error inesperado con el gestor de ", host)
            print("error: ", err)


if __name__ == "__main__":
    main()
