# coding=utf-8
import math
import sys


def distance(v1, v2):
    return round(math.sqrt(pow(v2.xaxis - v1.xaxis, 2) + pow(v2.yaxis - v1.yaxis, 2)), 2)


class WorldModel(object):
    _instance = None
    model = None

    @staticmethod
    def getinstance():
        if WorldModel._instance is None:
            WorldModel()
        return WorldModel._instance

    def __init__(self):
        if WorldModel._instance is not None:
            raise Exception("El modelo del mundo es Singleton!")
        else:
            WorldModel._instance = self

    @staticmethod
    def setworldmodel(structure):
        vertices = {}
        indices = {}
        for nodo in structure.node:
            vertex = Vertex(nodo.nodeid, nodo.name, nodo.xaxis, nodo.yaxis)
            vertices[nodo.nodeid-1] = vertex
            indices[nodo.nodeid-1] = nodo.nodeid - 1

        matriz = structure.adjacencymatrix

        for i in range(0, len(matriz)):
            for j in range(0, len(matriz[i])):

                if matriz[i][j] == 1:
                    matriz[i][j] = distance(vertices[i], vertices[j])
                    # print matriz[i][j]

        WorldModel.model = Graph(vertices, matriz, indices)
        # WorldModel.model.print_graph()

    @staticmethod
    def dijkstra(src, dstn):

        row = len(WorldModel.model.edges)
        col = len(WorldModel.model.edges[0])

        dist = [sys.maxsize] * row
        path = [-1] * row

        dist[src] = 0
        queue = []

        for i in range(row):
            queue.append(i)
        while queue:
            u = WorldModel.minDistance(dist, queue)
            queue.remove(u)
            for i in range(col):

                if WorldModel.model.edges[u][i] and i in queue:
                    if dist[u] + WorldModel.model.edges[u][i] < dist[i]:
                        dist[i] = dist[u] + WorldModel.model.edges[u][i]
                        path[i] = u

                        # print the constructed distance array
        return WorldModel.printSolution(path, dstn)

    @staticmethod
    def printSolution(path, dstn):
        finalpath = []
        WorldModel.printPath(path, dstn, finalpath)
        return finalpath

    @staticmethod
    def printPath(path, j, finalpath):

        # Base Case : If j is source
        if path[j] == -1:
            finalpath.append(WorldModel.model.vertices[j])
            return
        WorldModel.printPath(path, path[j], finalpath)
        finalpath.append(WorldModel.model.vertices[j])

    @staticmethod
    def minDistance(dist, queue):

        minimum = sys.maxsize
        min_index = -1

        for v in range(len(WorldModel.model.vertices)):
            if dist[v] < minimum and v in queue:
                minimum = dist[v]
                min_index = v

        return min_index


class Vertex:
    def __init__(self, nodeid, name, xaxis, yaxis):
        self.nodeid = nodeid
        self.name = name
        self.xaxis = xaxis
        self.yaxis = yaxis


class Graph:

    def __init__(self, vertices, edges, edge_indices):
        self.vertices = vertices
        self.edges = edges
        self.edge_indices = edge_indices

