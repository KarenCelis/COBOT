# coding=utf-8


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
            vertices[nodo.nodeid] = vertex
            indices[nodo.nodeid] = nodo.nodeid - 1
        WorldModel.model = Graph(vertices, structure.adjacencymatrix, indices)
        WorldModel.model.print_graph()


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

    def add_vertex(self, vertex):
        if isinstance(vertex, Vertex) and vertex.name not in self.vertices:
            self.vertices[vertex.name] = vertex
            for row in self.edges:
                row.append(0)
            self.edges.append([0] * (len(self.edges) + 1))
            self.edge_indices[vertex.name] = len(self.edge_indices)
            return True
        else:
            return False

    def add_edge(self, first, second, weight=1):
        if first in self.vertices and second in self.vertices:
            self.edges[self.edge_indices[first]][self.edge_indices[second]] = weight
            self.edges[self.edge_indices[second]][self.edge_indices[first]] = weight
            return True
        else:
            return False

    def print_graph(self):
        for v, i in sorted(self.edge_indices.items()):
            print(v, " ")
            for j in range(len(self.edges)):
                print(self.edges[i][j])
            print(" ")
