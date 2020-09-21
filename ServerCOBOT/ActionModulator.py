# coding=utf-8
from __future__ import division

import RobotProfile


class Modulator(object):
    def __init__(self, acciones, emocion, robotname, position=None):
        self.acciones = acciones
        self.emocion = emocion
        self.robotname = robotname
        self.position = position
        self.maping = []

        self.robotprofile = RobotProfile.RobotProfile.getinstance()
        RobotProfile.RobotProfile.setmaping(self.robotname)

    def calculateIntensity(self):
        moduledparameters = []
        for accion in self.acciones:
            for mapeo in RobotProfile.RobotProfile.maping:
                if accion.id == mapeo.actionid:
                    self.maping.append(Maping(mapeo.actionid, mapeo.commandname))
                    parametroeje = None
                    if accion.id == 1:
                        moduledparameters.append(Parameters("walk", "movement", [self.position.NodeId, accion.value]))
                    for parametro in mapeo.parameters:
                        if parametro.emotional_axis_happyness_sadness is not None:

                            parametroeje = parametro
                            value = self.applyequation(parametro)

                            moduledparameters.append(Parameters(parametro.parameterid, parametro.parametername, value))
                        elif parametro.secondary_maping_id is not None:
                            if parametroeje is None:
                                for mapeo_secundario in RobotProfile.RobotProfile.maping:
                                    if mapeo_secundario.actionid == parametro.secondary_maping_id:
                                        value = mapeo_secundario.parameters[0].values

                                        moduledparameters.append(
                                            Parameters(parametro.parameterid, parametro.parametername, value))
                            else:
                                for mapeo_secundario in RobotProfile.RobotProfile.secondary_maping:
                                    if mapeo_secundario.actionid == parametro.secondary_maping_id:

                                        porcentaje = self.emocion.value / 100

                                        for psecundario in mapeo_secundario.parameters:
                                            if psecundario.emotional_axis_happyness_sadness is not None:

                                                if psecundario.emotional_axis_happyness_sadness[0] <= \
                                                        porcentaje <= psecundario.emotional_axis_happyness_sadness[-1]:
                                                    value = psecundario.values
                                                    moduledparameters.append(
                                                        Parameters(parametro.parameterid,
                                                                   psecundario.parametername, value))
                    self.maping[-1].addparameters(moduledparameters)
        for result in moduledparameters:
            print("name: ", result.parametername, ", value: ", result.values)

    # La ecuación se aplica únicamente con los parámetros primarios, ya que los secundarios ya vienen discretizados
    # por porcentajes
    def applyequation(self, parameter):
        return (parameter.values[0] + ((parameter.values[-1] - parameter.values[0])
                                       * (self.emocion.value / 100)))


class Maping(object):
    def __init__(self, actionid, actionname):
        self.actionid = actionid
        self.actionname = actionname
        self.parameters = None

    def addparameters(self, parameters):
        self.parameters = parameters


class Parameters(object):
    def __init__(self, parameterid, parametername, values):
        self.parameterid = parameterid
        self.parametername = parametername
        self.values = values
