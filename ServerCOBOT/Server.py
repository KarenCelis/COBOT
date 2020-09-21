#!/usr/bin/env python
# -*- coding: utf-8 -*-
import socket
import json
import GestorDeObra


class Server(object):
    def __init__(self):
        self.gestores = []
        self.direcciones = []


def main():
    host = None
    servidor = Server()
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    buffer_size = 4096

    serversocket.bind((socket.gethostname(), 1235))
    serversocket.listen(10)

    while True:
        try:
            print("server listening!")
            clientsocket, address = serversocket.accept()
            print("Connection received from %s..." % str(address))
            host, port = clientsocket.getpeername()

            data = clientsocket.recv(buffer_size).decode('utf-8')
            print(data)

            jdata = json.loads(data)
            option = 0

            if "code" in jdata:
                option = int(json.dumps(jdata["code"]))
                print("option ", option)
            if "data" in jdata:
                data = json.dumps(jdata["data"])

            if host in servidor.direcciones:
                print("accediendo al gestor de ", host)
                indice = servidor.direcciones.index(host)
                result = servidor.gestores[indice].loadcontent(json.loads(data), option)

                if result == GestorDeObra.ACCIONES_ENTRANTES_ACEPTADAS:
                    clientsocket.send("acciones aceptadas")
                elif result == GestorDeObra.ACCIONES_ENTRANTES_BLOQUEADAS:
                    clientsocket.send("acciones bloqueadas")
            else:
                print("creando un nuevo gestor con ip ", host)
                servidor.direcciones.append(host)
                servidor.gestores.append(GestorDeObra.Gestor(host))
                servidor.gestores[-1].loadcontent(json.loads(data), option)
            clientsocket.send("información enviada")
            clientsocket.close()

        except ValueError:
            print("Se perdió comunicación con la aplicacion de ip ", host)


if __name__ == "__main__":
    main()
