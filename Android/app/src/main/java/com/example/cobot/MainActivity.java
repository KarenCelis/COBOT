package com.example.cobot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button Continuar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Continuar = findViewById(R.id.BotonContinuarMainActivity);
        Continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TrancisionConfigureAction = new Intent(Continuar.getContext(), RobotConection.class);
                startActivity(TrancisionConfigureAction);
            }
        });
    }
}