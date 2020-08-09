package com.example.cobot;

public class Scene {
    private int Id;
    private int Scenario;
    private int[]CharacterIds;
    private Position[] positions;
    private Action[] actions;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getScenario() {
        return Scenario;
    }

    public void setScenario(int scenario) {
        Scenario = scenario;
    }

    public int[] getCharacterIds() {
        return CharacterIds;
    }

    public void setCharacterIds(int[] characterIds) {
        CharacterIds = characterIds;
    }

    public Position[] getPositions() {
        return positions;
    }

    public void setPositions(Position[] positions) {
        this.positions = positions;
    }

    public Action[] getActions() {
        return actions;
    }

    public void setActions(Action[] actions) {
        this.actions = actions;
    }
}
