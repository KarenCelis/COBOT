# coding=utf-8
import json


# Singleton class
class RobotProfile(object):
    _instance = None
    maping = None
    secondary_maping = None

    @staticmethod
    def getinstance():
        if RobotProfile._instance is None:
            RobotProfile()
        return RobotProfile._instance

    def __init__(self):
        if RobotProfile._instance is not None:
            raise Exception("El perfil del robot es Singleton!")
        else:
            RobotProfile._instance = self

    @staticmethod
    def setmaping(robot):
        if robot == "nao":
            RobotProfile.loadfromfile("NaoProfile.json")

        elif robot == "quyca":
            RobotProfile.loadfromfile("QuycaProfile.json")

        else:
            return None

    @staticmethod
    def loadfromfile(filename):
        with open(filename) as f:
            data = json.load(f)
            if "maping" in data:
                RobotProfile.maping = RobotProfile.loadmaping("maping", data)

            if "secondary_maping" in data:
                RobotProfile.secondary_maping = RobotProfile.loadmaping("secondary_maping", data)

    @staticmethod
    def loadmaping(maping, data):

        newmaping = []
        for actionmap in data[maping]:

            newparameters = []
            json_data = json.dumps(actionmap)
            parameterdata = json.loads(json_data)

            if "parameters" in parameterdata:

                for parameter in parameterdata["parameters"]:
                    json_data_params = json.dumps(parameter)
                    newparameters.append(Parameters(**json.loads(json_data_params)))

            if maping == "maping":
                newmaping.append(Maping(**json.loads(json_data)))
            else:
                newmaping.append(SecondaryMaping(**json.loads(json_data)))

            newmaping[-1].parameters = newparameters
            print(newmaping[-1].action)

        return newmaping


class Maping(object):
    def __init__(self, actionid, action, commandname, parameters):
        self.actionid = actionid
        self.action = action
        self.commandname = commandname
        self.parameters = parameters


class Parameters(object):
    def __init__(self, parameterid, parametername, values=None, priority=None, emotional_axis_happyness_sadness=None,
                 secondary_maping_id=None):
        self.parameterid = parameterid
        self.priority = priority
        self.parametername = parametername
        self.values = values
        self.emotional_axis_happyness_sadness = emotional_axis_happyness_sadness
        self.secondary_maping_id = secondary_maping_id


class SecondaryMaping(object):
    def __init__(self, actionid, action, parameters):
        self.actionid = actionid
        self.action = action
        self.parameters = parameters
