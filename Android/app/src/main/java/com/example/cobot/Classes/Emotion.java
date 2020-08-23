package com.example.cobot.Classes;

public class Emotion {
    private String EmotionName;
    private int intensity;

    public Emotion(String emotionName, int intensity) {
        EmotionName = emotionName;
        this.intensity = intensity;
    }

    public String getEmotionName() {
        return EmotionName;
    }

    public void setEmotionName(String emotionName) {
        EmotionName = emotionName;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public String print() {
        return "Emotion{" +
                "EmotionName='" + EmotionName + '\'' +
                ", intensity=" + intensity +
                '}';
    }
}
