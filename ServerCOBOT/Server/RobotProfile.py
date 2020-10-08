# coding=utf-8
import json


# Singleton class
class RobotProfile(object):
    _instance = None
    maping = None
    secondary_maping = None
    emotional_axis_information = None
    source_code = None

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
            RobotProfile.loadfromfile("../Nao/NaoProfile.json")

        elif robot == "quyca":
            RobotProfile.loadfromfile("../Quyca/QuycaProfile.json")

        else:
            return None

    @staticmethod
    def loadfromfile(filename):

        with open(filename) as f:

            data = json.load(f)

            if "source_code" in data:
                RobotProfile.source_code = json.dumps(data["source_code"])

            if "emotional_axis_information" in data:
                RobotProfile.emotional_axis_information = RobotProfile.loademotioninfo(data)

            if "maping" in data:
                RobotProfile.maping = RobotProfile.loadmaping("maping", data)

            if "secondary_maping" in data:
                RobotProfile.secondary_maping = RobotProfile.loadmaping("secondary_maping", data)

    @staticmethod
    def loadmaping(maping, data):

        newmaping = []

        for actionmap in data[maping]:

            json_data = json.dumps(actionmap)

            if maping == "maping":
                newmaping.append(Maping(**json.loads(json_data)))
            else:
                newmaping.append(SecondaryMaping(**json.loads(json_data)))

            maping_data = json.loads(json_data)

            if "emotional_axis" in maping_data:

                newemotionalaxis = []

                for axis in maping_data["emotional_axis"]:

                    json_axis = json.dumps(axis)
                    newemotionalaxis.append(EmotionalAxis(**json.loads(json_axis)))

                    axis_data = json.loads(json_axis)

                    if "emotions" in axis_data:

                        newemotionparameters = []

                        for emotion in axis_data["emotions"]:

                            json_emotion = json.dumps(emotion)
                            newemotionparameters.append(EmotionParameters(**json.loads(json_emotion)))

                            emotion_data = json.loads(json_emotion)

                            if "parameters" in emotion_data:

                                newparameters = []

                                for parameter in emotion_data["parameters"]:
                                    json_data_parameters = json.dumps(parameter)
                                    newparameters.append(Parameters(**json.loads(json_data_parameters)))

                                newemotionparameters[-1].parameters = newparameters
                                newemotionparameters[-1].rgb_values = None

                            if "rgb_values" in emotion_data:

                                newrgb = []

                                for rgb in emotion_data["rgb_values"]:
                                    json_data_rgb = json.dumps(rgb)
                                    newrgb.append(RGBValues(**json.loads(json_data_rgb)))

                                newemotionparameters[-1].rgb_values = newrgb
                                newemotionparameters[-1].parameters = None

                        newemotionalaxis[-1].emotions = newemotionparameters

                newmaping[-1].emotional_axis = newemotionalaxis

            if newmaping[-1].emotional_axis[-1].emotions[-1].parameters is not None:
                print(newmaping[-1].emotional_axis[-1].emotions[-1].parameters[-1].parametername)
            else:
                print(newmaping[-1].emotional_axis[-1].emotions[-1].rgb_values[-1].name)

            if "secondary_parameters" in maping_data:

                newsecondaryparams = []

                for secondary in maping_data["secondary_parameters"]:

                    json_secondary = json.dumps(secondary)
                    newsecondaryparams.append(SecondaryParameters(**json.loads(json_secondary)))

                newmaping[-1].secondary_parameters = newsecondaryparams

        return newmaping

    @staticmethod
    def loademotioninfo(data):

        newinfo = []

        for axismap in data["emotional_axis_information"]:

            json_data = json.dumps(axismap)
            newinfo.append(EmotionalAxisInformation(**json.loads(json_data)))
            axis_data = json.loads(json_data)

            if "emotions" in axis_data:

                newemotions = []

                for emotion in axis_data["emotions"]:
                    json_emotion = json.dumps(emotion)
                    newemotions.append(EmotionRanges(**json.loads(json_emotion)))

                newinfo[-1].emotions = newemotions

        return newinfo


class Maping(object):
    def __init__(self, actionid, action, commandname, emotional_axis, secondary_parameters=None):
        self.actionid = actionid
        self.action = action
        self.commandname = commandname
        self.emotional_axis = emotional_axis
        self.secondary_parameters = secondary_parameters


class EmotionalAxis(object):
    def __init__(self, axis_identifier, name, emotions):
        self.axis_identifier = axis_identifier
        self.name = name
        self.emotions = emotions


class EmotionParameters(object):
    def __init__(self, name, parameters=None, rgb_values=None):
        self.name = name
        self.parameters = parameters
        self.rgb_values = rgb_values


class SecondaryParameters(object):
    def __init__(self, priority, parametername, secondary_maping_id):
        self.priority = priority
        self.parametername = parametername
        self.secondary_maping_id = secondary_maping_id


class SecondaryMaping(object):
    def __init__(self, parameterid, parametername, emotional_axis):
        self.parameterid = parameterid
        self.parametername = parametername
        self.emotional_axis = emotional_axis


class Parameters(object):
    def __init__(self, parametername, values=None, priority=None):
        self.parametername = parametername
        self.values = values
        self.priority = priority


class RGBValues(object):
    def __init__(self, name, values):
        self.name = name
        self.values = values


class EmotionalAxisInformation(object):
    def __init__(self, identifier, name, axis_range, emotions):
        self.identifier = identifier
        self.name = name
        self.axis_range = axis_range
        self.emotions = emotions


class EmotionRanges(object):
    def __init__(self, name, emotion_range):
        self.name = name
        self.emotion_range = emotion_range
