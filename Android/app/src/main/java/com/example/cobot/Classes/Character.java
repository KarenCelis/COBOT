package com.example.cobot;

import android.media.Image;

public class Character {
    private int Id;
    private String Name;
    private Image CharacterIcon;
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

    public Image getCharacterIcon() {
        return CharacterIcon;
    }

    public void setCharacterIcon(Image characterIcon) {
        CharacterIcon = characterIcon;
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
}
