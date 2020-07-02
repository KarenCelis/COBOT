package com.example.cobot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class ConfigureAction extends AppCompatActivity {
    com.google.android.material.slider.Slider sliderEmotion;
    com.google.android.material.slider.Slider sliderMood;
    Spinner spinnerAction;
    Spinner spinnerDialogue;
    Button ejecutar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_action);

        sliderEmotion = findViewById(R.id.slider_emotion);
        sliderMood = findViewById(R.id.slider_mood);
        spinnerAction = findViewById(R.id.spinner_accion);
        spinnerDialogue = findViewById(R.id.spinner_dialogo);
        ejecutar = findViewById(R.id.Button_Ejecutar);


        ejecutar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emotion = Float.toString(sliderEmotion.getValue());
                String mood = Float.toString(sliderMood.getValue());
                String action = spinnerAction.getSelectedItem().toString();
                String dialogue = spinnerDialogue.getSelectedItem().toString();
                Intent intent = new Intent(v.getContext(),ResultConfigureAction.class);
                Bundle bundle= new Bundle();
                bundle.putString("emocion",emotion);
                bundle.putString("mood",mood);
                bundle.putString("accion",action);
                bundle.putString("dialogo",dialogue);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
    }
}