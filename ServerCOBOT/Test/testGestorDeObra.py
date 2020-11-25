# -*- coding: utf-8 -*-
import sys
import unittest

sys.path.insert(0, "../")


def get_connection_json_nao_ipfail():
    return {'Connection': {'ip': '123.0.0.1', 'port': 9554, 'robot': 'nao'}}


def get_connection_json_nao_ipsuccess():
    return {'Connection': {'ip': '127.0.0.1', 'port': 9559, 'robot': 'nao'}}


def get_connection_json_other():
    return {'Connection': {'ip': '127.0.0.1', 'port': 9559, 'robot': 'quyca'}}


def get_lifesigns_json():
    return {'characterSelected': 1,
            'SignsOfLife': [{'actionid': 1, 'name': 'Mirar alrededor'},
                            {'actionid': 2, 'name': 'Mirar la hora'},
                            {'actionid': 3, 'name': 'Pensar'}]}


def get_worldmodel_json():
    return {'position': {
        'characterid': 1,
        'nodeid': 1},
        'scenario': {
            'node': [
                {'yaxis': 0, 'nodeid': 1, 'xaxis': 0, 'name': 'Hacia la coordenada 0-0'},
                {'yaxis': 1, 'nodeid': 2, 'xaxis': 0, 'name': 'Hacia la coordenada 0-1'},
                {'yaxis': 2, 'nodeid': 3, 'xaxis': 0, 'name': 'Hacia la coordenada 0-2'},
                {'yaxis': 3, 'nodeid': 4, 'xaxis': 0, 'name': 'Hacia la coordenada 0-3'},
                {'yaxis': 0, 'nodeid': 5, 'xaxis': 1, 'name': 'Hacia la coordenada 1-0'},
                {'yaxis': 1, 'nodeid': 6, 'xaxis': 1, 'name': 'Hacia la coordenada 1-1'},
                {'yaxis': 2, 'nodeid': 7, 'xaxis': 1, 'name': 'Hacia la coordenada 1-2'},
                {'yaxis': 3, 'nodeid': 8, 'xaxis': 1, 'name': 'Hacia la coordenada 1-3'},
                {'yaxis': 0, 'nodeid': 9, 'xaxis': 2, 'name': 'Hacia la coordenada 2-0'},
                {'yaxis': 1, 'nodeid': 10, 'xaxis': 2, 'name': 'Hacia la coordenada 2-1'},
                {'yaxis': 2, 'nodeid': 11, 'xaxis': 2, 'name': 'Hacia la coordenada 2-2'},
                {'yaxis': 3, 'nodeid': 12, 'xaxis': 2, 'name': 'Hacia la coordenada 2-3'},
                {'yaxis': 0, 'nodeid': 13, 'xaxis': 3, 'name': 'Hacia la coordenada 3-0'},
                {'yaxis': 1, 'nodeid': 14, 'xaxis': 3, 'name': 'Hacia la coordenada 3-1'},
                {'yaxis': 2, 'nodeid': 15, 'xaxis': 3, 'name': 'Hacia la coordenada 3-2'},
                {'yaxis': 3, 'nodeid': 16, 'xaxis': 3, 'name': 'Hacia la coordenada 3-3'}],
            'name': 'Agujero Negro',
            'scenarioid': 1, 'node_names': ['Hacia la coordenada 0-0',
                                            'Hacia la coordenada 0-1',
                                            'Hacia la coordenada 0-2',
                                            'Hacia la coordenada 0-3',
                                            'Hacia la coordenada 1-0',
                                            'Hacia la coordenada 1-1',
                                            'Hacia la coordenada 1-2',
                                            'Hacia la coordenada 1-3',
                                            'Hacia la coordenada 2-0',
                                            'Hacia la coordenada 2-1',
                                            'Hacia la coordenada 2-2',
                                            'Hacia la coordenada 2-3',
                                            'Hacia la coordenada 3-0',
                                            'Hacia la coordenada 3-1',
                                            'Hacia la coordenada 3-2',
                                            'Hacia la coordenada 3-3'],
            'nodes': 16, 'adjacencymatrix': [[0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                             [1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                             [0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                             [0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0],
                                             [1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0],
                                             [0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0],
                                             [0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0],
                                             [0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0],
                                             [0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0],
                                             [0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0],
                                             [0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0],
                                             [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1],
                                             [0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0],
                                             [0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0],
                                             [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1],
                                             [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0]]}}


def get_simpleactions_json_nao_group1():
    return {'Emotion': {'name': 'happyness_sadness', 'value': 0.74},
            'Actions': [{'actionid': 1, 'value': '2'},
                        {'actionid': 3, 'value': '1'}]}


def get_simpleactions_json_nao_group2():
    return {'Emotion': {'name': 'happyness_sadness', 'value': -0.3},
            'Actions': [{'actionid': 4, 'value': 'Al frente'},
                        {'actionid': 5, 'value': '1'},
                        {'actionid': 6, 'value': 'Al frente'}]}


def get_simpleactions_json_other():
    return {'Emotion': {'name': 'happyness_sadness', 'value': 0.74},
            'Actions': [{'actionid': 1, 'value': '2'},
                        {'actionid': 3, 'value': '1'}]}


def get_emergentactions_json():
    return {'Emotion': {'name': 'happyness_sadness', 'value': 0.78},
            'EmergentAction': {'actionid': 4, 'name': '¡¡Yupi!!'}}


class TestGestor(unittest.TestCase):

    def test_loadcontent_conexion(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')

        self.assertEqual(sg.loadcontent(get_connection_json_nao_ipfail(), 1), None)

    def test_loadcontent_carga_signodevida(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')
        self.assertEqual(sg.loadcontent(get_lifesigns_json(), 2), None)

    def test_loadcontent_carga_modelodelmundo(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')

        self.assertEqual(sg.loadcontent(get_worldmodel_json(), 3), None)

    def test_loadcontent_accionessimples_nao_group2_fallo_conexion(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')
        from Server import GestorDeObra
        sg.loadcontent(get_connection_json_nao_ipfail(), 1)
        sg.momento = GestorDeObra.INACTIVO
        sg.loadcontent(get_worldmodel_json(), 3)

        self.assertEqual(sg.loadcontent(get_simpleactions_json_nao_group2(), 4),
                         GestorDeObra.ACCIONES_ENTRANTES_BLOQUEADAS)

    def test_loadcontent_accionessimples_nao_exito_conexion(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')
        from Server import GestorDeObra
        sg.loadcontent(get_connection_json_nao_ipsuccess(), 1)
        sg.momento = GestorDeObra.INACTIVO
        sg.loadcontent(get_worldmodel_json(), 3)

        self.assertEqual(sg.loadcontent(get_simpleactions_json_nao_group2(), 4),
                         GestorDeObra.ACCIONES_ENTRANTES_EJECUTADAS)

    def test_loadcontent_accionessimples_other(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')
        from Server import GestorDeObra
        sg.loadcontent(get_connection_json_other(), 1)
        sg.momento = GestorDeObra.INACTIVO
        sg.loadcontent(get_worldmodel_json(), 3)

        self.assertEqual(sg.loadcontent(get_simpleactions_json_other(), 4),
                         GestorDeObra.ACCIONES_ENTRANTES_BLOQUEADAS)

    def test_loadcontent_emergente_fallo_conexion(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')
        from Server import GestorDeObra
        sg.loadcontent(get_connection_json_nao_ipfail(), 1)
        sg.momento = GestorDeObra.INACTIVO

        self.assertEqual(sg.loadcontent(get_emergentactions_json(), 5), GestorDeObra.ACCIONES_ENTRANTES_BLOQUEADAS)

    def test_loadcontent_emergente_exito_conexion(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')
        from Server import GestorDeObra
        sg.loadcontent(get_connection_json_nao_ipsuccess(), 1)
        sg.momento = GestorDeObra.INACTIVO

        self.assertEqual(sg.loadcontent(get_emergentactions_json(), 5), GestorDeObra.ACCIONES_ENTRANTES_EJECUTADAS)

    def test_sendsignsoflife_fallo_conexion(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')
        from Server import GestorDeObra
        sg.loadcontent(get_connection_json_nao_ipfail(), 1)
        sg.loadcontent(get_lifesigns_json(), 2)
        sg.momento = GestorDeObra.INACTIVO

        self.assertEqual(sg.loadcontent(None, 6), None)

    def test_sendsignsoflife_exito_conexion(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')
        from Server import GestorDeObra
        sg.loadcontent(get_connection_json_nao_ipsuccess(), 1)
        sg.loadcontent(get_lifesigns_json(), 2)
        sg.momento = GestorDeObra.INACTIVO

        self.assertEqual(sg.loadcontent(None, 6), None)

    def test_loadcontent_option_error(self):
        from Server import Gestor
        sg = Gestor('127.0.0.1')

        self.assertEqual(sg.loadcontent(None, 99), None)

    def test_stop_task(self):
        from Server import Gestor
        from Server import GestorDeObra
        sg = Gestor('127.0.0.1')
        sg.loadcontent(get_connection_json_nao_ipsuccess(), 1)
        sg.loadcontent(get_lifesigns_json(), 2)
        sg.momento = GestorDeObra.INACTIVO
        sg.loadcontent(None, 6)

        self.assertEqual(sg.stopTask(), None)


if __name__ == '__main__':
    unittest.main()
