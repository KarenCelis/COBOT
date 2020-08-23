package com.example.cobot.Classes;

import java.io.Serializable;

public class Character implements Serializable {
    private int Id;
    private String Name;
    private String CharacterIconUrl;
    public int NodeId;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCharacterIconUrl() {
        return CharacterIconUrl;
    }

    public void setCharacterIconUrl(String characterIconUrl) {
        CharacterIconUrl = characterIconUrl;
    }

    public int getNodeId() {
        return NodeId;
    }

    public void setNodeId(int nodeId) {
        NodeId = nodeId;
    }
}
