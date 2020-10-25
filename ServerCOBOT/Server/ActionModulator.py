# coding=utf-8
from __future__ import division

import RobotProfile
import GestorDeObra
import time


def selectprioritycommand(commands):
    if len(commands) == 1:
        return commands

    chosen = commands[0]

    for i in range(1, len(commands)):

        if commands[i].priority < chosen.priority:
            chosen = commands[i]

    return chosen


def printcontent(parameters):
    for result in parameters:
        if result.values is not None:
            print("Command: {}".format(result.command), ", parameter name: {}".format(result.parametername),
                  ", value: {}".format(result.values))
        else:
            print("Command: {}".format(result.command), ", parameter name: {}".format(result.parametername),
                  ", value RGB: ("),
            for rgb in result.rgb_values:
                print(rgb.values, ", "),
            print(")")


class Modulator(object):
    def __init__(self, acciones, emocion, robotname):
        self.acciones = acciones
        self.emocion = emocion
        self.robotname = robotname
        self.commands = []
        self.parameterset = []
        self.robotprofile = None

    def setprofile(self, accion):
        # El action modulator cuando se ejecuta únicamente hará que el perfil del robot cargue la información del
        # tipo de acción que llega, de esa forma no carga el contenido completo del json sino sólo lo necesario
        self.robotprofile = RobotProfile.RobotProfile.getinstance()
        if isinstance(accion, GestorDeObra.ActionObject):
            RobotProfile.RobotProfile.setmaping(self.robotname)
        elif isinstance(accion, GestorDeObra.EmergentActionObject):
            RobotProfile.RobotProfile.setemergent(self.robotname)
        elif isinstance(accion, GestorDeObra.SignsOfLifeObject):
            RobotProfile.RobotProfile.setsigns(self.robotname)
        else:
            print "No se encontró el tipo de acción enviada"

    def modulateactions(self):

        emotionalinfo = self.getemotionalinformation()
        self.parameterset = []

        for accion in self.acciones:

            for mapeo in RobotProfile.RobotProfile.maping:

                if accion.actionid == mapeo.actionid:

                    moduledparameters = []

                    for eje in mapeo.emotional_axis:

                        if emotionalinfo.axis == eje.name:

                            for emocion in eje.emotions:

                                if emotionalinfo.emotion_name == emocion.name:

                                    for parametro in emocion.parameters:
                                        if isinstance(accion, GestorDeObra.ActionObject):
                                            moduledparameters.append(
                                                Parameters(parametro.priority, parametro.parametername,
                                                           mapeo.commandname, self.applyequation(parametro)))
                                        else:
                                            if type(parametro.values) is unicode:

                                                moduledparameters.append(
                                                    Parameters(parametro.priority, parametro.parametername,
                                                               mapeo.commandname,
                                                               parametro.values))
                                            else:
                                                moduledparameters.append(
                                                    Parameters(parametro.priority, parametro.parametername,
                                                               mapeo.commandname, parametro.values))

                                        self.parameterset.append(moduledparameters[-1])

                    if mapeo.secondary_parameters is not None:
                        for secundarios in mapeo.secondary_parameters:

                            for sec_params in RobotProfile.RobotProfile.secondary_maping:

                                if secundarios.secondary_maping_id == sec_params.parameterid:

                                    for eje in sec_params.emotional_axis:

                                        if emotionalinfo.axis == eje.name:

                                            for emocion in eje.emotions:

                                                if emotionalinfo.emotion_name == emocion.name:

                                                    if emocion.parameters is not None:

                                                        for parametro in emocion.parameters:
                                                            moduledparameters.append(
                                                                Parameters(secundarios.priority,
                                                                           parametro.parametername, mapeo.commandname,
                                                                           self.applyequation(parametro)))
                                                            self.parameterset.append(moduledparameters[-1])

                                                    elif emocion.rgb_values is not None:

                                                        values = []

                                                        for rgb in emocion.rgb_values:
                                                            values.append(self.applyequation(rgb))

                                                        moduledparameters.append(
                                                            Parameters(secundarios.priority, sec_params.parametername,
                                                                       mapeo.commandname, values))
                                                        self.parameterset.append(moduledparameters[-1])

                    if mapeo.no_modulation_maping_id is not None:

                        if mapeo.no_modulation_maping_id == 0:
                            # Significa que no hay parámetros que no se modulan con el valor que llegó de la acción,
                            # por lo tanto, se almacena el valor sin más. Esto permite ingresar el nodo destino de
                            # caminar y correr, el diálogo o audio en sonido o hablar.

                            moduledparameters = [Parameters(0, mapeo.commandname,
                                                            mapeo.commandname, accion.value.encode('utf-8'))]
                            self.parameterset.append(moduledparameters[-1])

                        else:
                            for no_mod_action in RobotProfile.RobotProfile.no_modulation_maping:

                                if mapeo.no_modulation_maping_id == no_mod_action.actionid:

                                    for param in no_mod_action.parameters:

                                        for option in param.options:

                                            if option.name == accion.value:
                                                moduledparameters.append(
                                                    Parameters(param.priority, param.parametername,
                                                               no_mod_action.commandname, option.values))
                                                self.parameterset.append(moduledparameters[-1])

                    # print("parámetros modulados para la acción ", accion.actionid, ": ")
                    # printcontent(moduledparameters)
        self.parameterset = self.managePriority()
        # print("-- Gestionando prioridades --")
        # printcontent(self.parameterset)

        print("-- Comandos y parámetros --")
        printcontent(self.parameterset)
        self.setcommands()

    # La ecuación se aplica únicamente con los parámetros primarios, ya que los secundarios ya vienen discretizados
    # por porcentajes
    def applyequation(self, parameter):

        return (((self.emocion.value + 1.0) / 2.0) * (parameter.values[-1] - parameter.values[0])) + parameter.values[0]

    def getemotionalinformation(self):

        axis = None
        emotion_range = None
        emotion_name = None

        for eje in RobotProfile.RobotProfile.emotional_axis_information:

            if eje.name == self.emocion.name:

                axis = eje.name

                for emocion in eje.emotions:

                    if emocion.emotion_range[0] <= self.emocion.value <= emocion.emotion_range[-1]:
                        emotion_range = emocion.emotion_range
                        emotion_name = emocion.name

        return EmotionalInformation(axis, emotion_name, emotion_range)

    def managePriority(self):

        self.parameterset.sort(key=lambda x: x.parametername, reverse=True)

        i = 0
        m = 0
        total = []

        while i < len(self.parameterset):

            chosen = [self.parameterset[i]]

            for j in range(i, len(self.parameterset)):

                if self.parameterset[i].parametername == self.parameterset[j].parametername:

                    chosen.append(self.parameterset[j])
                    m = j + 1

                else:
                    m = j
                    break

            total.append(selectprioritycommand(chosen))
            i = m

        return total

    def setcommands(self):

        self.parameterset.sort(key=lambda x: x.command, reverse=True)

        i = 0
        m = 0

        while i < len(self.parameterset):

            chosen = [self.parameterset[i]]

            for j in range(i, len(self.parameterset)):

                if self.parameterset[i].command == self.parameterset[j].command:

                    chosen.append(self.parameterset[j])
                    m = j + 1

                else:
                    m = j
                    break

            self.commands.append(Command(self.parameterset[i].command, chosen))
            i = m


class Command(object):
    def __init__(self, commandname, parameters):
        self.commandname = commandname
        self.parameters = parameters


class Parameters(object):
    def __init__(self, priority, parametername, command, values=None, rgb_values=None):
        self.priority = priority
        self.parametername = parametername
        self.values = values
        self.rgb_values = rgb_values
        self.command = command


class EmotionalInformation(object):
    def __init__(self, axis, emotion_name, emotion_range):
        self.axis = axis
        self.emotion_name = emotion_name
        self.emotion_range = emotion_range


if __name__ == "__main__":
    start_time = time.time()
    RobotProfile.RobotProfile.getinstance()
    RobotProfile.RobotProfile.setmaping("nao")
    print("--- %s seconds ---" % (time.time() - start_time))
