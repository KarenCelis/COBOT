package com.example.cobot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cobot.Acciones.AccionCaminar;
import com.example.cobot.Acciones.AccionCorrer;
import com.example.cobot.Acciones.AccionGirar;
import com.example.cobot.Acciones.AccionHablar;
import com.example.cobot.Acciones.AccionMirar;
import com.example.cobot.Acciones.AccionSonido;
import com.example.cobot.Classes.Action;
import com.example.cobot.Classes.Emotion;
import com.example.cobot.Classes.Obra;
import com.example.cobot.Classes.Scene;
import com.example.cobot.Utils.SocketClient;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class CentralActivity extends AppCompatActivity  implements View.OnClickListener {


    private ImageView IVCharacterIcon;
    private TextView TVNombrePersonaje;
    private ImageButton[] btn = new ImageButton[5];
    private ImageButton btn_unfocus;
    private int[] btn_id = {R.id.IBMuyTriste, R.id.IBTriste, R.id.IBNormal, R.id.IBFeliz,R.id.IBMuyFeliz};
    private Button BEjecutarCentral;
    private LinearLayout LLHEscenas, LLHAcciones;
    private Obra obra;
    private int idPersonaje;
    private int returnInt = 0, returnInt1 = 0,returnInt2 = 0,returnInt3 = 0,returnInt4 = 0,returnInt5 = 0;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private boolean isEmotionSelected;
    private int LatestActionSelectedId;
    private String emotionSelected;
    private int emotionIntensitySelected;
    private Map<Integer, String> ActionsSelected;
    //private Action[] ActionsSelected;

    private static final String TAG = "ViewsCreation";
    private static final String TAG2 = "DataCollection";
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private static final int THIRD_ACTIVITY_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        obra = (Obra) getIntent().getSerializableExtra("obra");
        idPersonaje = (int) getIntent().getSerializableExtra("itemSelected");

        if (Picasso.get() == null) {
            Picasso picasso = new Picasso.Builder(getApplicationContext())
                    .downloader(new OkHttp3Downloader(getApplicationContext(), Integer.MAX_VALUE))
                    .build();
            Picasso.setSingletonInstance(picasso);
        }

        //Al presionar el botón BEjecutar, se revisa que se haya seleccionado la emoción y al menos una acción con su parámetro.
        isEmotionSelected = false;
        LatestActionSelectedId = 0;
        emotionIntensitySelected = 100;
        ActionsSelected = new HashMap<>();

        IVCharacterIcon = findViewById(R.id.IVCharacterIcon);
        Picasso.get().load(obra.getCharacters()[idPersonaje - 1].getCharacterIconUrl()).into(IVCharacterIcon);

        TVNombrePersonaje = findViewById(R.id.TVNombrePersonaje);
        TVNombrePersonaje.setText(obra.getCharacters()[idPersonaje - 1].getName());

        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
            btn[i].setBackgroundColor(Color.rgb(255, 255, 255));
            btn[i].setOnClickListener(this);
        }
        btn_unfocus = btn[0];

        LLHEscenas = findViewById(R.id.LLHEscenas);
        Scene[] escenas = obra.getScenes();
        for (final Scene iterator : escenas) {
            for (int i = 0; i < iterator.getCharacterIds().length; i++) {
                if (iterator.getCharacterIds()[i] == idPersonaje) {
                    Log.i(TAG, "Dentro de la escena " + iterator.getId());
                    Button BEscena = new Button(this);
                    BEscena.setText(iterator.getId() + "");
                    BEscena.setMinimumWidth(0);
                    BEscena.setMinWidth(0);
                    BEscena.setMinimumHeight(0);
                    BEscena.setMinHeight(0);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 10, 0);
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

        BEjecutarCentral = findViewById(R.id.BEjecutarCentral);
        BEjecutarCentral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Collect all the actions and emotions selected, 50% done
                if(isEmotionSelected && ActionsSelected.size() > 0){
                    Emotion em = new Emotion(emotionSelected, emotionIntensitySelected);
                    Log.i(TAG2, "Se ha seleccionado lo siguiente:");
                    Log.i(TAG2, ActionsSelected.keySet().toString());
                    Log.i(TAG2, ActionsSelected.values().toString());
                    new SocketClient().execute();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Por favor selecciona una emoción", Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    public void establecerAccionesDisponibles(final int idEscena) {
        Log.i(TAG, "Estableciendo acciones para la escena " + idEscena + " y el personaje " + idPersonaje);
        LLHAcciones = findViewById(R.id.LLHAcciones);
        LLHAcciones.removeAllViews();
        final Scene escenaEscogida = obra.getScenes()[idEscena - 1];
        for (final Action iterator : escenaEscogida.getActions()) {
            if (iterator.getCharacterId() == 0 || iterator.getCharacterId() == idPersonaje) {
                Log.i(TAG, "Acciones encontrada para el personaje " + idPersonaje);
                ImageButton IBAccion = new ImageButton(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 120);
                params.setMargins(10, 0, 0, 0);
                IBAccion.setLayoutParams(params);
                Picasso.get().load(obra.getGenericActions()[iterator.getIdGeneric() - 1].getActionIconUrl()).resize(120, 120).into(IBAccion);
                LLHAcciones.addView(IBAccion);
                IBAccion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iniciarDialogos(idEscena, iterator.getId(), iterator.getIdGeneric());
                    }
                });
            }
        }

    }

    public void iniciarDialogos(int idScene, int idAccion, int idActionGeneric) {
        Action accion = obra.getScenes()[idScene - 1].getActions()[idAccion - 1];
        //Almacenar la acción seleccionada
        LatestActionSelectedId = idActionGeneric-1;
        ActionsSelected.put(LatestActionSelectedId, "none");
        Intent intent;
        switch (accion.getActionName()) {
            case "hablar":
                intent = new Intent(getApplicationContext(), AccionHablar.class);
                intent.putExtra("id2", returnInt);
                startActivityForResult(intent,0);
                break;
            case "caminar":
                intent = new Intent(getApplicationContext(), AccionCaminar.class);
                intent.putExtra("id2", returnInt1);
                startActivityForResult(intent, 1);
                break;
            case "girar":
                intent = new Intent(getApplicationContext(), AccionGirar.class);
                intent.putExtra("id2", returnInt2);
                startActivityForResult(intent, 2);
                break;
            case "mirar":
                intent = new Intent(getApplicationContext(), AccionMirar.class);
                intent.putExtra("id2", returnInt3);
                startActivityForResult(intent, 3);
                break;
            case "sonido":
                intent = new Intent(getApplicationContext(), AccionSonido.class);
                intent.putExtra("id2", returnInt4);
                startActivityForResult(intent, 4);

                break;
            case "correr":
                intent = new Intent(getApplicationContext(), AccionCorrer.class);
                intent.putExtra("id2", returnInt5);
                startActivityForResult(intent, 5);
                break;
            default:
                Toast.makeText(getApplicationContext(), "No hay parámetros para esta acción", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                // Get String data from Intent
                returnInt = data.getIntExtra("id", returnInt);
               // Toast.makeText(getApplicationContext(), String.valueOf(returnInt), Toast.LENGTH_LONG).show();
                //returnInt = data.getIntExtra("id",0);

                //Obtener el parámetro de la acción y ponerlo en el map paralelo a la id de la acción genérica
                String parameter = data.getStringExtra("parameter");
                ActionsSelected.put(LatestActionSelectedId, parameter);

                Toast.makeText(getApplicationContext(), String.valueOf(returnInt), Toast.LENGTH_LONG).show();
                // Set text view with string
                // TextView textView = (TextView) findViewById(R.id.textView);
                //textView.setText(returnString);
            }


        }

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                returnInt1 = data.getIntExtra("id", returnInt1);
            }
        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                returnInt2 = data.getIntExtra("id", returnInt2);
            }
        }
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                returnInt3 = data.getIntExtra("id", returnInt3);
            }
        }
        if (requestCode == 4) {
            if (resultCode == RESULT_OK) {
                returnInt4 = data.getIntExtra("id", returnInt4);
            }
        }
        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                returnInt5 = data.getIntExtra("id", returnInt5);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.IBMuyTriste:
                setFocus(btn_unfocus, btn[0]);
                emotionSelected = "TooSad";
                break;
            case R.id.IBTriste:
                setFocus(btn_unfocus, btn[1]);
                emotionSelected = "Sad";
                break;
            case R.id.IBNormal:
                setFocus(btn_unfocus, btn[2]);
                emotionSelected = "Normal";
                break;
            case R.id.IBFeliz:
                setFocus(btn_unfocus, btn[3]);
                emotionSelected = "Happy";
                break;
            case R.id.IBMuyFeliz:
                setFocus(btn_unfocus, btn[4]);
                emotionSelected = "TooHappy";
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void setFocus(ImageButton btn_unfocus, ImageButton btn_focus){
        //btn_unfocus.setTextColor(Color.rgb(49, 50, 51));
        btn_unfocus.setBackgroundColor(Color.rgb(255,255,255));
        // btn_focus.setTextColor(Color.rgb(255, 255, 255));
        btn_focus.setBackgroundColor(R.color.pressed_color);
        this.btn_unfocus = btn_focus;
        isEmotionSelected = true;
    }
}
