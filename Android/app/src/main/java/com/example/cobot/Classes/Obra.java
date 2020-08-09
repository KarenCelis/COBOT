package com.example.cobot.Classes;

import java.io.Serializable;
import java.util.Arrays;

public class Obra implements Serializable {
    private String Title;
    private int SceneAmount;
    private Character[] Characters;
    private Scene[] Scenes;
    //El escenario de la obra es un grafo, aún no se crearán las instancias de los nodos por lo que no aparecerá el escenario hasta entonces.

    public Obra(String Title, int SceneAmount, Character[]Characters, Scene[]Scenes){
        this.Title=Title;
        this.SceneAmount=SceneAmount;
        this.Characters=Characters;
        this.Scenes=Scenes;
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
