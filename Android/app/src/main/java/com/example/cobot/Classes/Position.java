package com.example.cobot.Classes;

import java.io.Serializable;

public class Position implements Serializable {
    private int CharacterId;
    private String location;

    public int getCharacterId() {
        return CharacterId;
    }

    public void setCharacterId(int characterId) {
        CharacterId = characterId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
