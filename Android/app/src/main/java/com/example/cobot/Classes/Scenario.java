package com.example.cobot.Classes;

import java.io.Serializable;

public class Scenario implements Serializable {

    private int id;
    private String name;
    private int nodes;
    private int[][]adjacencyMatrix;
    private String[] node_names;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNodes() {
        return nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public void setAdjacencyMatrix(int[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public String[] getNodeNames() {
        return node_names;
    }

    public void setNodeNames(String[] node_names) {
        this.node_names = node_names;
    }
}
