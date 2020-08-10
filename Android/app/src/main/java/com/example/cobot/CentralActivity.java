package com.example.cobot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cobot.Classes.Action;
import com.example.cobot.Classes.Obra;
import com.example.cobot.Classes.Scene;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class CentralActivity extends AppCompatActivity {

    private ImageView IVCharacterIcon;
    private TextView TVNombrePersonaje;
    private LinearLayout LLHEscenas;
    private GridLayout GLAcciones;
    private Obra obra;
    private int idPersonaje;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private static final String TAG = "ViewsCreation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        obra = (Obra)getIntent().getSerializableExtra("obra");
        idPersonaje = (int)getIntent().getSerializableExtra("itemSelected");

        if(Picasso.get()==null){
            Picasso picasso = new Picasso.Builder(getApplicationContext())
                    .downloader(new OkHttp3Downloader(getApplicationContext(),Integer.MAX_VALUE))
                    .build();
            Picasso.setSingletonInstance(picasso);
        }

        IVCharacterIcon = findViewById(R.id.IVCharacterIcon);
        Picasso.get().load(obra.getCharacters()[idPersonaje-1].getCharacterIconUrl()).into(IVCharacterIcon);

        TVNombrePersonaje = findViewById(R.id.TVNombrePersonaje);
        TVNombrePersonaje.setText(obra.getCharacters()[idPersonaje-1].getName());

        LLHEscenas = findViewById(R.id.LLHEscenas);
        Scene[]escenas = obra.getScenes();
        for(final Scene iterator : escenas){
            for(int i=0 ; i < iterator.getCharacterIds().length ; i++){
                if(iterator.getCharacterIds()[i]==idPersonaje){
                    Log.i(TAG, "Dentro de la escena "+iterator.getId());
                    Button BEscena = new Button(this);
                    BEscena.setText(iterator.getId()+"");
                    BEscena.setMinimumWidth(0);
                    BEscena.setMinWidth(0);
                    BEscena.setMinimumHeight(0);
                    BEscena.setMinHeight(0);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,10,0);
                    params.gravity = Gravity.START;
                    params.weight = 1;
                    BEscena.setLayoutParams(params);
                    LLHEscenas.addView(BEscena);
                    BEscena.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            establecerAccionesDisponibles(iterator.getId());
                        }
                    });
                }
            }
        }
    }
    public void establecerAccionesDisponibles(final int idEscena){
        Log.i(TAG, "Estableciendo acciones para la escena "+idEscena+ " y el personaje "+idPersonaje);
        GLAcciones = findViewById(R.id.GLAcciones);
        GLAcciones.removeAllViews();
        final Scene escenaEscogida = obra.getScenes()[idEscena-1];
        for(final Action iterator: escenaEscogida.getActions()){
            if(iterator.getCharacterId() == 0 || iterator.getCharacterId() == idPersonaje){
                Log.i(TAG, "Accion encontrada para el personaje "+idPersonaje);
                ImageButton IBAccion = new ImageButton(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 120);
                IBAccion.setLayoutParams(params);
                Picasso.get().load(obra.getGenericActions()[iterator.getIdGeneric()-1].getActionIconUrl()).resize(120, 120).into(IBAccion);
                GLAcciones.addView(IBAccion);
                IBAccion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iniciarDialogos(idEscena, iterator.getId(), iterator.getIdGeneric());
                    }
                });
            }
        }

    }

    public void iniciarDialogos(int idScene, int idAccion, int idActionGeneric){
        Action accion = obra.getScenes()[idScene-1].getActions()[idAccion-1];
        switch (accion.getActionName()) {
            case "hablar":
                createDialogForHablar();
                break;
            case "caminar":
                createDialogForCaminar();
                break;
            case "girar":
                createDialogForGirar();
                break;
            case "mirar":
                createDialogForMirar();
                break;
            case "sonido":
                createDialogForSonido();
                break;
            case "correr":
                createDialogForCorrer();
                break;
            default:
                Toast.makeText(getApplicationContext(), "No hay parámetros para esta acción", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void createDialogForHablar(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View actionPopup = getLayoutInflater().inflate(R.layout.layout_hablar, null);
        Button hablar1 = actionPopup.findViewById(R.id.btn_h1);
        Button hablar2 = actionPopup.findViewById(R.id.btn_h2);
        Button hablar3 = actionPopup.findViewById(R.id.btn_h3);
        Button hablar4 = actionPopup.findViewById(R.id.EditTextDesde);
        dialogBuilder.setView(actionPopup);
        dialog = dialogBuilder.create();
        dialog.show();
        hablar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Hola, ¿Qué tal?", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        hablar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "No, gracias", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        hablar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Tengo pereza", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        hablar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "¡Ya voy!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }
    public void createDialogForCaminar(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View actionPopup = getLayoutInflater().inflate(R.layout.layout_caminar, null);
        Button caminar1 = actionPopup.findViewById(R.id.btn_h1);
        Button caminar2 = actionPopup.findViewById(R.id.btn_h2);
        Button caminar3 = actionPopup.findViewById(R.id.btn_h3);
        Button caminar4 = actionPopup.findViewById(R.id.btn_h4);
        dialogBuilder.setView(actionPopup);
        dialog = dialogBuilder.create();
        dialog.show();
        caminar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Caminando hacia su pupitre", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        caminar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Caminando hacia la puerta", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        caminar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Caminando hacia el tablero", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        caminar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Caminando hacia el espacio vacío", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }
    public void createDialogForGirar(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View actionPopup = getLayoutInflater().inflate(R.layout.layout_girar, null);
        Button girarDerecha = actionPopup.findViewById(R.id.btn_girarDerech);
        Button girarIzquierda = actionPopup.findViewById(R.id.btn_girarIzq);
        Button girarFrente = actionPopup.findViewById(R.id.btn_girarFrente);
        Button girarDeEspaldas = actionPopup.findViewById(R.id.btn_girarDeEspaldas);
        dialogBuilder.setView(actionPopup);
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
    public void createDialogForMirar(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View actionPopup = getLayoutInflater().inflate(R.layout.layout_mirar, null);
        Button mirarDerecha = actionPopup.findViewById(R.id.btn_mirarDerec);
        Button mirarIzquierda = actionPopup.findViewById(R.id.btn_mirarIzq);
        Button mirarFrente = actionPopup.findViewById(R.id.btn_mirarFrente);
        Button mirarAbajo = actionPopup.findViewById(R.id.btn_MirarAbajo);

        dialogBuilder.setView(actionPopup);
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
        mirarAbajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "mirarAbajo", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }
    public void createDialogForSonido(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View actionPopup = getLayoutInflater().inflate(R.layout.layout_sonido, null);
        Button cantar = actionPopup.findViewById(R.id.btn_cantar);
        Button gritar = actionPopup.findViewById(R.id.btn_gritar);
        Button silbar = actionPopup.findViewById(R.id.btn_silbar);
        Button reir = actionPopup.findViewById(R.id.btn_reir);
        dialogBuilder.setView(actionPopup);
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
    public void createDialogForCorrer(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View actionPopup = getLayoutInflater().inflate(R.layout.layout_correr, null);
        Button correr1 = actionPopup.findViewById(R.id.btn_h1);
        Button correr2 = actionPopup.findViewById(R.id.btn_h2);
        Button correr3 = actionPopup.findViewById(R.id.btn_h3);
        Button correr4 = actionPopup.findViewById(R.id.btn_h4);
        dialogBuilder.setView(actionPopup);
        dialog = dialogBuilder.create();
        dialog.show();
        correr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Corriendo hacia su pupitre", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        correr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Corriendo hacia la puerta", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        correr3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Corriendo hacia el tablero", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        correr4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Corriendo hacia el espacio vacío", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

}