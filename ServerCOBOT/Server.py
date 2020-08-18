import socket
import json

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

buffer_size = 4096

serverSocket.bind((socket.gethostname(), 1235))
serverSocket.listen(10)

while True:
    clientsocket, address = serverSocket.accept()
    print("Connection received from %s..." % str(address))
    clientsocket.send(bytes("Welcome to the python server"))

    #data = clientsocket.recv(buffer_size)
    #jdata = json.loads(data.decode('utf-8'))

    #clientsocket.send(jdata)
    #print("the data sent was:\n", jdata)
    clientsocket.close()
