package com.example.cobot.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.cobot.Classes.Character;
import com.example.cobot.Classes.Obra;
import com.example.cobot.R;

import java.util.ArrayList;

public class CharacterSelectionActivity extends AppCompatActivity {

    private Obra obra;
    private int itemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_selection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Button BContinuar = findViewById(R.id.BContinuar);
        BContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IniciarActividadCentral();
            }
        });

        obra = (Obra)getIntent().getSerializableExtra("obra");

        ArrayList<String> personajes = new ArrayList<>();
        for(Character cha: obra.getCharacters()){
            personajes.add(cha.getName());
        }

        Spinner SPlistadoDePersonajes = findViewById(R.id.SPListadoDePersonajes);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, personajes);
        SPlistadoDePersonajes.setAdapter(adapter);
        SPlistadoDePersonajes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemSelected = position+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void IniciarActividadCentral() {
        Intent intent = new Intent(this, CentralActivity.class);
        intent.putExtra("obra", obra);
        intent.putExtra("itemSelected", itemSelected);
        startActivity(intent);
    }
}