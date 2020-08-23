package com.example.cobot.Classes;

import java.io.Serializable;

public class Action implements Serializable {
    private int Id;
    private int idGeneric;
    private int CharacterId;
    private int Duration;
    private String ActionName;
    //El atributo DisplayText hace referencia a los diálogos posibles para escoger, por eso es un arreglo y es paralelo al arreglo audiofile.
    private String[] DisplayText;
    //El atributo AudioFile puede ser nulo para aquellas acciones que no involucren sonidos.
    private String[] AudioFile;
    //Si las opciones de la acción tienen una imagen explicativa en el sistema entonces es true, de lo contrario se asigna la imagen de la acción genérica
    private boolean hasImages;
    private String[]imageUrls;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getIdGeneric() {
        return idGeneric;
    }

    public void setIdGeneric(int idGeneric) {
        this.idGeneric = idGeneric;
    }

    public int getCharacterId() {
        return CharacterId;
    }

    public void setCharacterId(int characterId) {
        CharacterId = characterId;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public String getActionName() {
        return ActionName;
    }

    public void setActionName(String actionName) {
        ActionName = actionName;
    }

    public String[] getDisplayText() {
        return DisplayText;
    }

    public void setDisplayText(String[] displayText) {
        DisplayText = displayText;
    }

    public String[] getAudioFile() {
        return AudioFile;
    }

    public void setAudioFile(String[] audioFile) {
        AudioFile = audioFile;
    }

    public boolean isHasImages() {
        return hasImages;
    }

    public void setHasImages(boolean hasImages) {
        this.hasImages = hasImages;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }
}
