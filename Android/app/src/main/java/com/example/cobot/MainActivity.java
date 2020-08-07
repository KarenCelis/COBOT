package com.example.cobot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button ;
    private Button save;
    private Button boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicial = findViewById(R.id.btn_inicial);

        inicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContactDiaglog();
            }
        });


    }

    public void createContactDiaglog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopup = getLayoutInflater().inflate(R.layout.popup, null);
        boton = contactPopup.findViewById(R.id.btn_opc);
        save = contactPopup.findViewById(R.id.btn_save);
        dialogBuilder.setView(contactPopup);
        dialog = dialogBuilder.create();
        dialog.show();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Hello",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

    }
