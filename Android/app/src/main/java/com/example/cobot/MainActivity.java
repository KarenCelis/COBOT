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

    private Button btnSonido,btnMirar,btnGirar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSonido = findViewById(R.id.btn_sonido);
        btnMirar = findViewById(R.id.btn_mirar);
        btnGirar = findViewById(R.id.btn_girar);

        btnSonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContactDiaglogSonido();
            }
        });
        btnMirar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContactDiaglogMirar();
            }
        });
        btnGirar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContactDiaglogGirar();
            }
        });

    }

    public void createContactDiaglogSonido() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopup = getLayoutInflater().inflate(R.layout.layout_sonido, null);
        Button cantar = contactPopup.findViewById(R.id.btn_cantar);
        Button gritar = contactPopup.findViewById(R.id.btn_gritar);
        Button silbar = contactPopup.findViewById(R.id.btn_silbar);
        Button reir = contactPopup.findViewById(R.id.btn_reir);
        dialogBuilder.setView(contactPopup);
        dialog = dialogBuilder.create();
        dialog.show();
        cantar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "cantar", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        gritar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "gritar", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        silbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "silbar", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        reir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "reir", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

    }

    public void createContactDiaglogMirar() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopup = getLayoutInflater().inflate(R.layout.layout_mirar, null);
        Button mirarDerecha = contactPopup.findViewById(R.id.btn_mirarDerec);
        Button mirarIzquierda = contactPopup.findViewById(R.id.btn_mirarIzq);
        Button mirarFrente = contactPopup.findViewById(R.id.btn_mirarFrente);

        dialogBuilder.setView(contactPopup);
        dialog = dialogBuilder.create();
        dialog.show();
        mirarDerecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "mirarDerecha", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        mirarIzquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "mirarIzquierda", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        mirarFrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "mirarFrente", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

    }

    public void createContactDiaglogGirar() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopup = getLayoutInflater().inflate(R.layout.layout_girar, null);
        Button girarDerecha = contactPopup.findViewById(R.id.btn_girarDerech);
        Button girarIzquierda = contactPopup.findViewById(R.id.btn_girarIzq);
        Button girarFrente = contactPopup.findViewById(R.id.btn_girarFrente);
        Button girarDeEspaldas = contactPopup.findViewById(R.id.btn_girarDeEspaldas);
        dialogBuilder.setView(contactPopup);
        dialog = dialogBuilder.create();
        dialog.show();
        girarDerecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "girarDerecha", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        girarIzquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "girarIzquierda", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        girarFrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "girarFrente", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        girarDeEspaldas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "girarDeEspaldas", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }
}
