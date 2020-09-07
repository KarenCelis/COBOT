import json

class jsonObject(object):
    def __init__(self, data_received):
        self.__dict__ = json.loads(data_received)
        jdata = json.loads(data_received)
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
