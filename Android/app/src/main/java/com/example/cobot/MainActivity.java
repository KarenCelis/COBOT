package com.example.cobot;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cobot.Classes.Obra;
import com.example.cobot.Utils.Reader;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private static final int READ_REQUEST_CODE = 42;
    private static final String TAG = "FileRead";
    private Button btnSonido,btnMirar,btnGirar,BCargarObra;
    private Obra obra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Button BAbrirArchivo = findViewById(R.id.BAbrirArchivo);
        btnSonido = findViewById(R.id.btn_sonido);
        btnMirar = findViewById(R.id.btn_mirar);
        btnGirar = findViewById(R.id.btn_girar);
        BCargarObra = findViewById(R.id.BCargarObra);

        BAbrirArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("*/*");
                intent = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                final Uri uri = data.getData();
                assert uri != null;
                String path = uri.getPath();
                TextView TVSeleccionArchivo = findViewById(R.id.TVSeleccionarArchivo);

                TVSeleccionArchivo.setText(path);
                BCargarObra.setVisibility(View.VISIBLE);

                BCargarObra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            IniciarActividadDeSeleccionDePersonaje(uri);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        else{
            Log.i(TAG, "No se pudo obtener la dirección del archivo");
        }
    }

    private void IniciarActividadDeSeleccionDePersonaje(Uri uri) throws IOException, JSONException {
        cargarObra(uri);
        Intent intent = new Intent(this, CharacterSelectionActivity.class);
        //pasar la información de la uri de la obra para ser cargada mientras se crea la actividad.
        intent.putExtra("obra", obra);
        startActivity(intent);
    }

    private void cargarObra(Uri uri) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        Log.i(TAG, "Uri path: "+uri.getPath());
        InputStream inputStream = getContentResolver().openInputStream(uri);
        assert inputStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String NL = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            sb.append(line).append(NL);
        }
        reader.close();
        String Result = sb.toString();
        obra = Reader.crearObraDesdeJSON(Result);
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
