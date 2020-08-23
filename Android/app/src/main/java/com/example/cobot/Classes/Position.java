package com.example.cobot.Classes;

import java.io.Serializable;

public class Position implements Serializable {
    private int CharacterId;
    private int NodeId;

    public int getCharacterId() {
        return CharacterId;
    }

    public void setCharacterId(int characterId) {
        CharacterId = characterId;
    }

    public int getNodeId() {
        return NodeId;
    }

    public void setNodeId(int NodeId) {
        this.NodeId = NodeId;
    }
}
