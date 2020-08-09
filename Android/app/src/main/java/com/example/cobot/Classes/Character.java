package com.example.cobot.Classes;

import android.media.Image;
import android.widget.ImageView;

import java.io.Serializable;

public class Character implements Serializable {
    private int Id;
    private String Name;
    private String CharacterIconUrl;
    private int InitialPosition;
    private int InitialAlignment;
    private Position CurrentLocation;

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

    public int getInitialPosition() {
        return InitialPosition;
    }

    public void setInitialPosition(int initialPosition) {
        InitialPosition = initialPosition;
    }

    public int getInitialAlignment() {
        return InitialAlignment;
    }

    public void setInitialAlignment(int initialAlignment) {
        InitialAlignment = initialAlignment;
    }

    public Position getCurrentLocation() {
        return CurrentLocation;
    }

    public void setCurrentLocation(Position currentLocation) {
        CurrentLocation = currentLocation;
    }

    public String getCharacterIconUrl() {
        return CharacterIconUrl;
    }

    public void setCharacterIconUrl(String characterIconUrl) {
        CharacterIconUrl = characterIconUrl;
    }
}
