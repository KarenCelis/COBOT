#!/usr/bin/env python
# -*- coding: utf-8 -*-
import socket
import json
import GestorDeObra
import threading

print_lock = threading.Lock()


def printstring(string):
    print_lock.acquire()
    print string
    print_lock.release()


class ThreadedServer(object):
    def __init__(self, host, port):
        self.gestores = []
        self.direcciones = []
        self.timers = []
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind((self.host, self.port))
        print "Enviar datos al servidor de IP {} y puerto {}".format(socket.gethostbyname(host), port)

    def listen(self):
        self.sock.listen(20)
        while True:
            printstring("-----Servidor escuchando-----\n")
            client, address = self.sock.accept()
            host, port = address
            printstring("------Conexión recibida------")
            if host in self.direcciones:
                indice = self.direcciones.index(host)
                printstring("Accediendo al gestor de obra del usuario con IP: {}\n".format(host))
                threading.Thread(target=self.listenToClient, args=(client, indice)).start()
            else:
                self.direcciones.append(host)
                self.gestores.append(GestorDeObra.Gestor(host))
                self.timers.append(threading.Timer(18, self.ejecutarsignosdevida, [len(self.gestores)-1]))
                printstring("Iniciando un nuevo gestor de obra para el usuario con IP {}\n".format(host))
                threading.Thread(target=self.listenToClient, args=(client, len(self.gestores)-1)).start()

            client.settimeout(180)

    def listenToClient(self, client, indice):

        try:

            data = client.recv(4096).decode('utf-8')

            if not data:
                printstring("El contenido del mensaje está vacío, verifica la conexión")
            else:

                jdata = json.loads(data)
                option = 0
                printstring(jdata)

                if "code" in jdata:
                    option = int(json.dumps(jdata["code"]))

                if option != 0:
                    if "data" in jdata:
                        data = json.dumps(jdata["data"])

                        if self.timers[indice].is_alive() and (option == 4 or option == 5):
                            canceltimer(self.timers[indice])
                            printstring("Cancelando timer para el gestor de {}".format(self.gestores[indice].userip))
                            if self.gestores[indice].momento == GestorDeObra.EJECUTANDO_SIGNOS_DE_VIDA:
                                print "Signos detenidos"
                                self.gestores[indice].stopTask()

                        if self.gestores[indice].momento == GestorDeObra.EJECUTANDO_ACCIONES_SIMPLES:
                            printstring("Ya se están ejecutando acciones, por favor espere a que finalicen")
                            client.send("acciones bloqueadas, intente de nuevo")
                        elif self.gestores[indice].momento == GestorDeObra.EJECUTANDO_ACCION_EMERGENTE:
                            printstring("Ya se están ejecutando acciones, por favor espere a que finalicen")
                            client.send("acciones bloqueadas, intente de nuevo")
                        else:
                            result = self.gestores[indice].loadcontent(json.loads(data), option)

                            if self.gestores[indice].momento == GestorDeObra.INACTIVO or \
                                    self.gestores[indice].momento == GestorDeObra.EJECUTANDO_SIGNOS_DE_VIDA:
                                if not self.timers[indice].is_alive():
                                    self.timers[indice] = threading.Timer(18, self.ejecutarsignosdevida, [indice])
                                    starttimer(self.timers[indice])
                                    printstring("Iniciando timer para el gestor de {}".format(self.gestores[indice].userip))

                            if result == GestorDeObra.ACCIONES_ENTRANTES_EJECUTADAS:
                                client.send("acciones ejecutadas exitosamente")
                            elif result == GestorDeObra.ACCIONES_ENTRANTES_BLOQUEADAS:
                                client.send("acciones bloqueadas, intente de nuevo")

                    client.send("información enviada")
                else:
                    client.send("Servidor conectado")
            client.close()

        except ValueError as err:
            printstring("Ocurrió un error inesperado con el gestor de {}".format(self.gestores[indice].connection.ip))
            printstring("error: \n{}".format(err))

    def ejecutarsignosdevida(self, indice):
        self.gestores[indice].sendsingsoflife()
        self.timers[indice] = threading.Timer(18, self.ejecutarsignosdevida, [indice])
        if self.gestores[indice].momento == GestorDeObra.INACTIVO or \
                self.gestores[indice].momento == GestorDeObra.EJECUTANDO_SIGNOS_DE_VIDA:
            starttimer(self.timers[indice])


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
    timer.join()


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
                            if not servidor.timers[indice].is_alive():
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
    # main()
    ThreadedServer(socket.gethostname(), 1235).listen()
