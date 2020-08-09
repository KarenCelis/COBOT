package com.example.cobot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cobot.Classes.Obra;

import java.util.Objects;

public class CharacterSelectionActivity extends AppCompatActivity {

    private Obra obra;
    private static final String TAG = "FileLoad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_selection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        obra = (Obra)getIntent().getSerializableExtra("obra");
        TextView TVNombreObra = findViewById(R.id.TVNombreObra);
        TVNombreObra.setText(obra.getTitle());

    }
}