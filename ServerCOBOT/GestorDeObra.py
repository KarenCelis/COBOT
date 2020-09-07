import json

class jsonObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        if "Emotion" in jdata:
            emocion = emotionObject(json.dumps(jdata["Emotion"]))
            print("debug emocion ", emocion.name)
        if "Actions" in jdata:
            for accion in jdata["Actions"]:
                acciones = actionObject(json.dumps(accion))
                print("debug accion", acciones.value)
        if "EmergentAction" in jdata:
            emergentAction = emergentActionObject(jdata)
            print("debug accion emergente ", emergentAction)
        # if "Scenarios" in jdata:
            # for scenario in jdata["Scenarios"]:
                # worldModel = worldModelObject(json.dumps(scenario))
                # print("debug modelo del mundo ", worldModel.node_names)
        # print(json.dumps(jdata["SignsOfLife"], indent=4, sort_keys=True))
        # if "SignsOfLife" in jdata:
            # for signo in jdata["SignsOfLife"]:
                # print("test")
                # print(json.dumps(signo, indent=4, sort_keys=True))
                # signOfLife = signsOfLifeObject(json.dumps(signo))
                # print("debug signos de vida ", signOfLife["name"])
        # print(json.dumps(jdata, indent=4, sort_keys=True))


class emotionObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))

class actionObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))

class emergentActionObject(object):
    def __init__(self, data_received):
        self.__dict__ = data_received
        print(json.dumps(data_received, indent=4, sort_keys=True))

class signsOfLifeObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))

class worldModelObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
        print(json.dumps(jdata, indent=4, sort_keys=True))
