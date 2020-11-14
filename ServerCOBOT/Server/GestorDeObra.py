# coding=utf-8
import json
import ActionExecutor
import ActionModulator
import WorldModel
import threading
import random

EJECUTANDO_SIGNOS_DE_VIDA = 1
EJECUTANDO_ACCIONES_SIMPLES = 2
EJECUTANDO_ACCION_EMERGENTE = 3
CONFIGURACION = 0
INACTIVO = -1

ACCIONES_ENTRANTES_BLOQUEADAS = 0
ACCIONES_ENTRANTES_EJECUTADAS = 1

print_lock = threading.Lock()


# Clase que recibe la información desde la app y la mapea con sus elementos respectivos
class Gestor(object):
    def __init__(self, applicationip):

        self.applicationip = applicationip
        self.connection = None

        self.characterselected = 0

        self.emocion = None
        self.acciones = []
        self.emergentaction = []
        self.signsoflife = []

        self.worldmodel = WorldModel.WorldModel.getinstance()
        self.position = None

        self.momento = CONFIGURACION
        self.actionmodulator = None

        self.actionexecutor = None

        self.data_received = None
        self.returned_message = None

    def loadcontent(self, data_received, option):

        self.data_received = data_received

        if option == 1:
            self.loadconnection()
        elif option == 2:
            self.loadsignsoflife()
        elif option == 3:
            self.loadworldmodel()
        elif option == 4:
            self.loadsimpleactions()
        elif option == 5:
            self.loademergentactions()
        elif option == 6:
            self.sendsingsoflife()
        else:
            print("opción inválida")

    def loadconnection(self):

        if "Connection" in self.data_received:
            json_data = json.dumps(self.data_received["Connection"])
            self.connection = ConnectionObject(**json.loads(json_data))
            print("Robot ip: ", self.connection.ip)

    def loadsignsoflife(self):

        if "characterSelected" in self.data_received:
            self.characterselected = self.data_received["characterSelected"]

        if "SignsOfLife" in self.data_received:
            self.signsoflife = []
            for sign in self.data_received["SignsOfLife"]:
                json_data = json.dumps(sign)
                self.signsoflife.append(SignsOfLifeObject(**json.loads(json_data)))
            self.momento = INACTIVO

    def sendsingsoflife(self):
        # Modular signos de vida
        if self.momento == INACTIVO:

            self.momento = EJECUTANDO_SIGNOS_DE_VIDA
            print_lock.acquire()
            print "ejecutando signos de vida para {}".format(self.applicationip)
            print_lock.release()

            # Se envía solo un valor de los signos de vida y se fija como arreglo ya que el action modulator funciona
            # genéricamente para todos los tipos de acción, y las acciones simples pueden ser más de una, por lo que se
            # hace un ciclo y es necesario un arreglo siempre. En este caso, el ciclo se hará sobre una única acción.
            sign = [random.choice(self.signsoflife)]
            if self.emocion is None:
                self.emocion = EmotionObject("happyness_sadness", 0.11)
            self.actionmodulator = ActionModulator.Modulator(sign,
                                                             self.emocion, self.connection.robot)
            self.actionmodulator.setprofile(sign[-1])
            self.actionmodulator.modulateactions()

            # Ejecutar signos de vida
            self.callexecutor()

            self.momento = INACTIVO
        else:
            print_lock.acquire()
            print "ejecutando signos de vida para {}".format(self.applicationip)
            print_lock.release()

    def loadworldmodel(self):

        if "scenario" in self.data_received:
            json_data = json.dumps(self.data_received["scenario"])
            data = json.loads(json_data)
            scenariostructure = WorldModelStructureObject(**json.loads(json_data))
            print("debug escenario {}".format(scenariostructure.name))
            if "node" in data:
                nodos = []
                for nodeiterator in data["node"]:
                    json_data = json.dumps(nodeiterator)
                    nodos.append(NodeStructure(**json.loads(json_data)))
                scenariostructure.setnodestructure(nodos)
            WorldModel.WorldModel.setworldmodel(scenariostructure)

        if "position" in self.data_received:
            json_data = json.dumps(self.data_received["position"])
            self.position = PositionObject(**json.loads(json_data))
            print("debug posicion {}".format(self.position.CharacterId))

    def loadsimpleactions(self):

        if "Emotion" in self.data_received:
            json_data = json.dumps(self.data_received["Emotion"])
            self.emocion = EmotionObject(**json.loads(json_data))
            print("debug emocion {}".format(self.emocion.name))

        if "Actions" in self.data_received:

            nuevas_acciones = []
            for accion in self.data_received["Actions"]:
                json_data = json.dumps(accion)
                nuevas_acciones.append(ActionObject(**json.loads(json_data)))
                print("debug accion de id: {}, value: {}".format(nuevas_acciones[-1].actionid,
                                                                 nuevas_acciones[-1].value))

            if self.isexecutoravailable():

                self.momento = EJECUTANDO_ACCIONES_SIMPLES
                self.acciones = nuevas_acciones
                self.actionmodulator = ActionModulator.Modulator(self.acciones, self.emocion, self.connection.robot)
                # Se le ordena al action modulator cargar el perfil del robot para acciones simples, por eso se manda
                # una de ellas para que el modulador compruebe que es una instancia de una acción simple y no una
                # emergente o un signo de vida

                self.actionmodulator.setprofile(self.acciones[0])
                self.actionmodulator.modulateactions()

                self.callexecutor()

            else:
                return ACCIONES_ENTRANTES_BLOQUEADAS

        return ACCIONES_ENTRANTES_EJECUTADAS

    def loademergentactions(self):

        if "Emotion" in self.data_received:
            json_data = json.dumps(self.data_received["Emotion"])
            self.emocion = EmotionObject(**json.loads(json_data))
            print("debug emocion {}".format(self.emocion.name))

        if "EmergentAction" in self.data_received:
            json_data = json.dumps(self.data_received["EmergentAction"])
            self.emergentaction.append(EmergentActionObject(**json.loads(json_data)))
            print("debug accion emergente {}".format(self.emergentaction[-1].actionid))

            if self.isexecutoravailable():
                self.momento = EJECUTANDO_ACCION_EMERGENTE
                self.actionmodulator = ActionModulator.Modulator(self.emergentaction, self.emocion,
                                                                 self.connection.robot)

                self.actionmodulator.setprofile(self.emergentaction[-1])
                self.actionmodulator.modulateactions()
                self.callexecutor()

            self.emergentaction = []

    def isexecutoravailable(self):

        if self.momento == EJECUTANDO_ACCIONES_SIMPLES:
            return False
        elif self.momento == INACTIVO:
            return True
        elif self.momento == EJECUTANDO_SIGNOS_DE_VIDA:
            return True
        elif self.momento == EJECUTANDO_ACCION_EMERGENTE:
            return False
        elif self.momento == CONFIGURACION:
            return False

    def callexecutor(self):
        self.actionexecutor = ActionExecutor.Executor(self.actionmodulator.commands, self.connection,
                                                      self.position)

        self.returned_message = self.actionexecutor.executeactions()

        if self.returned_message.status:
            print "acciones ejecutadas correctamente!"
            self.connection.theta = self.returned_message.new_theta
            print "El robot se encuentra mirando hacia el ángulo {}".format(self.connection.theta)
            if self.position is not None:
                self.position.NodeId = self.returned_message.new_position
                print "El robot se encuentra en el nodo {}".format(self.position.NodeId)

        else:
            print "Hubo un error al ejecutar las acciones, verifique la información enviada"

        self.momento = INACTIVO

    def stopTask(self):
        if self.momento == EJECUTANDO_SIGNOS_DE_VIDA:
            self.actionexecutor = ActionExecutor.Executor(self.actionmodulator.commands, self.connection,
                                                          self.position)
            self.actionexecutor.stopTask(self.returned_message)


class ConnectionObject(object):
    def __init__(self, ip, port, robot):
        self.ip = ip
        self.port = port
        self.robot = robot
        self.theta = 0


class EmotionObject(object):
    def __init__(self, name, value):
        self.name = name
        self.value = value


class ActionObject(object):
    def __init__(self, actionid, value):
        self.actionid = actionid
        print type(value)
        self.value = value.encode('utf-8')
        print self.value


class EmergentActionObject(object):
    def __init__(self, actionid, name):
        self.actionid = actionid + 1
        self.name = name.encode('utf-8')


class SignsOfLifeObject(object):
    def __init__(self, actionid, name):
        self.actionid = actionid
        self.name = name.encode('utf-8')


class WorldModelStructureObject(object):
    def __init__(self, scenarioid, name, nodes, adjacencymatrix, node_names, node):
        self.scenarioid = scenarioid
        self.name = name.encode('utf-8')
        self.nodes = nodes
        self.adjacencymatrix = adjacencymatrix
        self.node_names = node_names
        self.node = node

    def setnodestructure(self, node):
        self.node = node


class NodeStructure(object):
    def __init__(self, nodeid, name, xaxis, yaxis):
        self.nodeid = nodeid
        self.name = name
        self.xaxis = xaxis
        self.yaxis = yaxis


class PositionObject(object):
    def __init__(self, characterid, nodeid):
        self.CharacterId = characterid
        self.NodeId = nodeid


class ExecutorReturn(object):
    def __init__(self, status, new_theta, new_position):
        self.status = status
        self.new_theta = new_theta
        self.new_position = new_position
        self.processid = []
