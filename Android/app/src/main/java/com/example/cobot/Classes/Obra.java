package com.example.cobot;

public class Obra {
    private String Title;
    private int SceneAmount;
    private Character[] Characters;
    private Scene[] Scenes;
    //El escenario de la obra es un grafo, aún no se crearán las instancias de los nodos por lo que no aparecerá el escenario hasta entonces.


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
}
