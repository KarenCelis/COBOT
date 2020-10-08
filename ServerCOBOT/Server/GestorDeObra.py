# coding=utf-8
import json
import ActionExecutor
import ActionModulator
import WorldModel

EJECUTANDO_SIGNOS_DE_VIDA = 1
EJECUTANDO_ACCIONES_SIMPLES = 2
EJECUTANDO_ACCION_EMERGENTE = 3
CONFIGURACION = 0

ACCIONES_ENTRANTES_BLOQUEADAS = 0
ACCIONES_ENTRANTES_ACEPTADAS = 1


# Clase que recibe la información desde la app y la mapea con sus elementos respectivos
class Gestor(object):
    def __init__(self, applicationip):

        self.applicationip = applicationip
        self.connection = None

        self.characterselected = 0

        self.emocion = None
        self.acciones = []
        self.emergentaction = None
        self.signsoflife = []

        self.worldmodel = WorldModel.WorldModel.getinstance()
        self.position = None

        self.momento = CONFIGURACION
        self.actionmodulator = None

        self.actionexecutor = None

        self.data_received = None

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
            for sign in self.data_received["SignsOfLife"]:
                json_data = json.dumps(sign)
                self.signsoflife.append(SignsOfLifeObject(**json.loads(json_data)))
                print("debug accion", self.signsoflife[-1].name)

    def loadworldmodel(self):

        if "scenario" in self.data_received:
            json_data = json.dumps(self.data_received["scenario"])
            data = json.loads(json_data)
            scenariostructure = WorldModelStructureObject(**json.loads(json_data))
            print("debug escenario", scenariostructure.name)
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
            print("debug posicion", self.position.CharacterId)

    def loadsimpleactions(self):

        if "Emotion" in self.data_received:
            json_data = json.dumps(self.data_received["Emotion"])
            self.emocion = EmotionObject(**json.loads(json_data))
            print("debug emocion ", self.emocion.name)

        if "Actions" in self.data_received:

            nuevas_acciones = []
            for accion in self.data_received["Actions"]:
                json_data = json.dumps(accion)
                nuevas_acciones.append(ActionObject(**json.loads(json_data)))
                print("debug accion de id: ", nuevas_acciones[-1].actionid, ", value: ", nuevas_acciones[-1].value)

            if self.isexecutoravailable(nuevas_acciones):

                self.acciones = nuevas_acciones
                self.actionmodulator = ActionModulator.Modulator(self.acciones, self.emocion, self.connection.robot)
                self.actionmodulator.modulateactions()

                self.actionexecutor = ActionExecutor.Executor(self.actionmodulator.commands, self.connection,
                                                              self.position)
                self.momento = EJECUTANDO_ACCIONES_SIMPLES
                self.actionexecutor.executeactions()

            else:
                return ACCIONES_ENTRANTES_BLOQUEADAS

        return ACCIONES_ENTRANTES_ACEPTADAS

    def loademergentactions(self):

        if "EmergentAction" in self.data_received:
            json_data = json.dumps(self.data_received["EmergentAction"])
            self.emergentaction = EmergentActionObject(**json.loads(json_data))
            print("debug accion emergente ", self.emergentaction.name)

    def isexecutoravailable(self, nuevas_acciones):
        if self.momento == CONFIGURACION:
            return True
        elif self.momento == EJECUTANDO_SIGNOS_DE_VIDA:
            return True
        elif self.momento == EJECUTANDO_ACCION_EMERGENTE:
            return False
        elif self.momento == EJECUTANDO_ACCIONES_SIMPLES:
            # verificar que se pueda ejecutar una accion mientras se ejecutan otras
            # se verifica que las acciones no tengan la misma prioridad o no sean las mismas que alguna en ejecución
            for accion in self.acciones:
                for accion_nueva in nuevas_acciones:
                    if accion.id == accion_nueva.id:
                        return False
            return True


class ConnectionObject(object):
    def __init__(self, ip, port, robot):
        self.ip = ip
        self.port = port
        self.robot = robot


class EmotionObject(object):
    def __init__(self, name, value):
        self.name = name
        self.value = value


class ActionObject(object):
    def __init__(self, actionid, value):
        self.actionid = actionid
        self.value = value


class EmergentActionObject(object):
    def __init__(self, name):
        self.name = name


class SignsOfLifeObject(object):
    def __init__(self, characterid, genericactionid, signid, name):
        self.characterId = characterid
        self.genericActionId = genericactionid
        self.id = signid
        self.name = name


class WorldModelStructureObject(object):
    def __init__(self, scenarioid, name, nodes, adjacencymatrix, node_names, node):
        self.scenarioid = scenarioid
        self.name = name
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
