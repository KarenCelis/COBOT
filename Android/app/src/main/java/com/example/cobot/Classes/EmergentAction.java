package com.example.cobot.Classes;

import java.io.Serializable;

public class EmergentAction implements Serializable {

    private int id;
    private String name;
    private int[]characterId;

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

    public int[] getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int[] characterId) {
        this.characterId = characterId;
    }

}
