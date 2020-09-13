# coding=utf-8
import json

# Clase que recibe la información desde la app y la mapea con sus elementos respectivos

characterSelected = 1


class JsonObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)

        if "Emotion" in jdata:
            emocion = EmotionObject(json.dumps(jdata["Emotion"]))
            print("debug emocion ", emocion.name)

        if "Actions" in jdata:
            for accion in jdata["Actions"]:
                acciones = ActionObject(json.dumps(accion))
                print("debug accion", acciones.value)

        if "EmergentAction" in jdata:
            emergentAction = EmergentActionObject(json.dumps(jdata["EmergentAction"]))
            print("debug accion emergente ", emergentAction.name)

        if "characterSelected" in jdata:
            characterSelected = jdata["characterSelected"]

        if "SignsOfLife" in jdata:
            for sign in jdata["SignsOfLife"]:
                signOfLife = SignsOfLifeObject(json.dumps(sign))
                print("debug signo de vida", signOfLife.name)

        if "scenario" in jdata:
            scenario = WorldModelObject(json.dumps(jdata["scenario"]))
            print("debug escenario", scenario.name)

        if "position" in jdata:
            position = PositionObject(json.dumps(jdata["position"]))
            print("debug posicion", position.CharacterId)

class ConnectionObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))


class EmotionObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))


class ActionObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))


class EmergentActionObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))


class SignsOfLifeObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))


class WorldModelObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))


class PositionObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))
