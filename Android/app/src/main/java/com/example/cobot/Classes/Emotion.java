package com.example.cobot.Classes;

public class Emotion {
    private String EmotionName;
    private double intensity;

    public Emotion(String emotionName, double intensity) {
        EmotionName = emotionName;
        this.intensity = intensity;
    }

    public String getEmotionName() {
        return EmotionName;
    }

    public void setEmotionName(String emotionName) {
        EmotionName = emotionName;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public String print() {
        return "Emotion{" +
                "EmotionName='" + EmotionName + '\'' +
                ", intensity=" + intensity +
                '}';
    }
}
