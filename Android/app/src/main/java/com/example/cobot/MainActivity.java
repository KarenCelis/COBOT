package com.example.cobot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button ContinuarM;
    Button buscarArchivo;
    TextView textoDeArchivoDeObra;
    Intent myFileIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContinuarM = findViewById(R.id.BotonContinuarMainActivity);
        buscarArchivo = findViewById(R.id.BuscarArchivoDeObra);
        textoDeArchivoDeObra = findViewById(R.id.TextoDeArchivoDeObra);
        buscarArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                myFileIntent.setType("*/*");
                startActivityForResult(myFileIntent, 10);
            }
        });
        ContinuarM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TrancisionConfigureAction = new Intent(ContinuarM.getContext(), ConfigureAction.class);
                startActivity(TrancisionConfigureAction);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 10:
                if (resultCode== RESULT_OK) {
                    String path = data.getData().getPath();
                    textoDeArchivoDeObra.setText(path);

                }
                break;

        }
    }
}