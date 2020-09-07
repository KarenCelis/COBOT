#!/usr/bin/env python
# -*- coding: utf-8 -*-
import socket
import json
import JSONReader

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

buffer_size = 4096

serverSocket.bind((socket.gethostname(), 1235))
serverSocket.listen(10)

while True:
    print("server listening!")
    clientsocket, address = serverSocket.accept()
    print("Connection received from %s..." % str(address))
    clientsocket.send(bytes("Welcome to the python server"))

    data = clientsocket.recv(buffer_size)
    print(data)
    instrucciones = JSONReader.jsonObject(data)
    # clientsocket.send(bytes("Welcome to the python server"))

    # clientsocket.send(jdata)
    # print("the data received is:\n", jdata)
    clientsocket.close()
