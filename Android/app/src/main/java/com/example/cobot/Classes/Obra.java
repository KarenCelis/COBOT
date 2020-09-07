package com.example.cobot.Classes;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.Arrays;

public class Obra implements Serializable {
    private String Title;
    private int SceneAmount;
    private Character[] Characters;
    private Scene[] Scenes;
    private Scenario[]Scenarios;
    private GenericAction[]GenericActions;
    private String jsonScenarios, jsonSignsOfLife;

    //El escenario de la obra es un grafo, aún no se crearán las instancias de los nodos por lo que no aparecerá el escenario hasta entonces.

    public Obra(String Title, int SceneAmount, Character[]Characters, Scene[]Scenes, Scenario[] Scenarios, GenericAction[]GenericActions, String jsonScenarios, String jsonSignsOfLife){
        this.Title=Title;
        this.SceneAmount=SceneAmount;
        this.Characters=Characters;
        this.Scenes=Scenes;
        this.Scenarios = Scenarios;
        this.GenericActions = GenericActions;
        this.jsonScenarios = jsonScenarios;
        this.jsonSignsOfLife = jsonSignsOfLife;
    }
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getSceneAmount() {
        return SceneAmount;
    }

    public void setSceneAmount(int sceneAmount) {
        SceneAmount = sceneAmount;
    }

    public Character[] getCharacters() {
        return Characters;
    }

    public void setCharacters(Character[] characters) {
        Characters = characters;
    }

    public Scene[] getScenes() {
        return Scenes;
    }

    public void setScenes(Scene[] scenes) {
        Scenes = scenes;
    }

    public Scenario[] getScenarios() {
        return Scenarios;
    }

    public void setScenarios(Scenario[] scenarios) {
        Scenarios = scenarios;
    }

    public GenericAction[] getGenericActions() {
        return GenericActions;
    }

    public void setGenericActions(GenericAction[] genericActions) {
        GenericActions = genericActions;
    }

    public String getJsonScenarios() {
        return jsonScenarios;
    }

    public void setJsonScenarios(String jsonScenarios) {
        this.jsonScenarios = jsonScenarios;
    }

    public String getJsonSignsOfLife() {
        return jsonSignsOfLife;
    }

    public void setJsonSignsOfLife(String jsonSignsOfLife) {
        this.jsonSignsOfLife = jsonSignsOfLife;
    }

    @Override
    public String toString() {
        return "Obra{" +
                "Title='" + Title + '\'' +
                ", SceneAmount=" + SceneAmount +
                ", Characters=" + Arrays.toString(Characters) +
                ", Scenes=" + Arrays.toString(Scenes) +
                '}';
    }
}
