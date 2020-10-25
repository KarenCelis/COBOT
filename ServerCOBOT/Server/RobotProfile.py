# coding=utf-8
import json


# Singleton class
class RobotProfile(object):
    _instance = None
    maping = None
    secondary_maping = None
    emotional_axis_information = None
    source_code = None
    emergent_actions = None
    signs_of_life = None
    no_modulation_maping = None

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

        RobotProfile.setparallelinfo(robot)
        RobotProfile.setnotmodulationmaping(robot)

        if robot == "nao":
            RobotProfile.loadmapingfromfile("../Nao/NaoProfile.json")

        elif robot == "quyca":
            RobotProfile.loadmapingfromfile("../Quyca/QuycaProfile.json")

        else:
            return None

    @staticmethod
    def setnotmodulationmaping(robot):
        if robot == "nao":
            RobotProfile.loadnomodulationmapingfromfile("../Nao/NaoProfile.json")

        elif robot == "quyca":
            RobotProfile.loadnomodulationmapingfromfile("../Quyca/QuycaProfile.json")

        else:
            return None

    @staticmethod
    def setemergent(robot):

        RobotProfile.setparallelinfo(robot)

        if robot == "nao":
            RobotProfile.loademergentfromfile("../Nao/NaoProfile.json")

        elif robot == "quyca":
            RobotProfile.loademergentfromfile("../Quyca/QuycaProfile.json")

        else:
            return None

    @staticmethod
    def setsigns(robot):

        RobotProfile.setparallelinfo(robot)

        if robot == "nao":
            RobotProfile.loadsignsfromfile("../Nao/NaoProfile.json")

        elif robot == "quyca":
            RobotProfile.loadsignsfromfile("../Quyca/QuycaProfile.json")

        else:
            return None

    @staticmethod
    def setparallelinfo(robot):

        if robot == "nao":
            RobotProfile.loadparallelinfo("../Nao/NaoProfile.json")

        elif robot == "quyca":
            RobotProfile.loadparallelinfo("../Quyca/QuycaProfile.json")

        else:
            return None

    @staticmethod
    def loademergentfromfile(filename):
        with open(filename) as f:
            data = json.load(f)

            if "emergent_actions" in data:
                RobotProfile.maping = RobotProfile.loadmaping("emergent_actions", data)

            if "secondary_maping" in data:
                RobotProfile.secondary_maping = RobotProfile.loadmaping("secondary_maping", data)

    @staticmethod
    def loadsignsfromfile(filename):
        with open(filename) as f:
            data = json.load(f)

            if "signs_of_life" in data:
                RobotProfile.maping = RobotProfile.loadmaping("signs_of_life", data)

            if "secondary_maping" in data:
                RobotProfile.secondary_maping = RobotProfile.loadmaping("secondary_maping", data)

    @staticmethod
    def loadparallelinfo(filename):
        with open(filename) as f:
            data = json.load(f)

            if "source_code" in data:
                RobotProfile.source_code = json.dumps(data["source_code"]).decode('utf-8')

            if "emotional_axis_information" in data:
                RobotProfile.emotional_axis_information = RobotProfile.loademotioninfo(data)

    @staticmethod
    def loadmapingfromfile(filename):

        with open(filename) as f:

            data = json.load(f)

            if "maping" in data:
                RobotProfile.maping = RobotProfile.loadmaping("maping", data)

            if "secondary_maping" in data:
                RobotProfile.secondary_maping = RobotProfile.loadmaping("secondary_maping", data)

    @staticmethod
    def loadnomodulationmapingfromfile(filename):
        with open(filename) as f:
            data = json.load(f)

            if "no_modulation_maping" in data:
                RobotProfile.no_modulation_maping = RobotProfile.loadnomodulationmaping(data)

    @staticmethod
    def loadmaping(maping, data):

        newmaping = []

        for actionmap in data[maping]:

            json_data = json.dumps(actionmap).decode('utf-8')

            if maping == "secondary_maping":
                newmaping.append(SecondaryMaping(**json.loads(json_data)))
            else:
                newmaping.append(Maping(**json.loads(json_data)))

            maping_data = json.loads(json_data)

            if "emotional_axis" in maping_data:

                newemotionalaxis = []

                for axis in maping_data["emotional_axis"]:

                    json_axis = json.dumps(axis).decode('utf-8')
                    newemotionalaxis.append(EmotionalAxis(**json.loads(json_axis)))

                    axis_data = json.loads(json_axis)

                    if "emotions" in axis_data:

                        newemotionparameters = []

                        for emotion in axis_data["emotions"]:

                            json_emotion = json.dumps(emotion).decode('utf-8')
                            newemotionparameters.append(EmotionParameters(**json.loads(json_emotion)))

                            emotion_data = json.loads(json_emotion)

                            if "parameters" in emotion_data:

                                newparameters = []

                                for parameter in emotion_data["parameters"]:
                                    json_data_parameters = json.dumps(parameter).decode('utf-8')
                                    newparameters.append(Parameters(**json.loads(json_data_parameters)))

                                newemotionparameters[-1].parameters = newparameters
                                newemotionparameters[-1].rgb_values = None

                            if "rgb_values" in emotion_data:

                                newrgb = []

                                for rgb in emotion_data["rgb_values"]:
                                    json_data_rgb = json.dumps(rgb).decode('utf-8')
                                    newrgb.append(RGBValues(**json.loads(json_data_rgb)))

                                newemotionparameters[-1].rgb_values = newrgb
                                newemotionparameters[-1].parameters = None

                        newemotionalaxis[-1].emotions = newemotionparameters

                newmaping[-1].emotional_axis = newemotionalaxis

            if "secondary_parameters" in maping_data:

                newsecondaryparams = []

                for secondary in maping_data["secondary_parameters"]:
                    json_secondary = json.dumps(secondary).decode('utf-8')
                    newsecondaryparams.append(SecondaryParameters(**json.loads(json_secondary)))

                newmaping[-1].secondary_parameters = newsecondaryparams

        return newmaping

    @staticmethod
    def loadnomodulationmaping(data):

        newmaping = []

        for actionmap in data["no_modulation_maping"]:

            json_data = json.dumps(actionmap).decode('utf-8')

            newmaping.append(NoModulationMaping(**json.loads(json_data)))

            maping_data = json.loads(json_data)

            if "parameters" in maping_data:

                newparameters = []

                for param in maping_data["parameters"]:

                    json_param = json.dumps(param).decode('utf-8')
                    newparameters.append(NoModulationParams(**json.loads(json_param)))

                    param_data = json.loads(json_param)

                    if "options" in param_data:

                        newoptions = []

                        for option in param_data["options"]:
                            json_option = json.dumps(option).decode('utf-8')
                            newoptions.append(Options(**json.loads(json_option)))

                        newparameters[-1].options = newoptions

                newmaping[-1].parameters = newparameters

        return newmaping

    @staticmethod
    def loademotioninfo(data):

        newinfo = []

        for axismap in data["emotional_axis_information"]:

            json_data = json.dumps(axismap).decode('utf-8')
            newinfo.append(EmotionalAxisInformation(**json.loads(json_data)))
            axis_data = json.loads(json_data)

            if "emotions" in axis_data:

                newemotions = []

                for emotion in axis_data["emotions"]:
                    json_emotion = json.dumps(emotion).decode('utf-8')
                    newemotions.append(EmotionRanges(**json.loads(json_emotion)))

                newinfo[-1].emotions = newemotions

        return newinfo


class Maping(object):
    def __init__(self, actionid, action, commandname, emotional_axis, secondary_parameters=None,
                 no_modulation_maping_id=None):
        self.actionid = actionid
        self.action = action
        self.commandname = commandname
        self.emotional_axis = emotional_axis
        self.secondary_parameters = secondary_parameters
        self.no_modulation_maping_id = no_modulation_maping_id


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


# Clases para la carga del mapeo no modulado

class NoModulationMaping(object):
    def __init__(self, actionid, action, commandname, parameters=None):
        self.actionid = actionid
        self.action = action
        self.commandname = commandname
        self.parameters = parameters


class NoModulationParams(object):
    def __init__(self, priority, parameterid, parametername, options=None):
        self.priority = priority
        self.parameterid = parameterid
        self.parametername = parametername
        self.options = options


class Options(object):
    def __init__(self, name, values):
        self.name = name
        self.values = values
