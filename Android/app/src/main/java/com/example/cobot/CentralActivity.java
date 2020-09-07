package com.example.cobot;

import androidx.annotation.Nullable;
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

import com.example.cobot.Classes.Action;
import com.example.cobot.Classes.Character;
import com.example.cobot.Classes.Emotion;
import com.example.cobot.Classes.Obra;
import com.example.cobot.Classes.Position;
import com.example.cobot.Classes.Scene;
import com.example.cobot.Utils.SocketClient;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CentralActivity extends AppCompatActivity  {
//

    String[] num = {"Muy Triste", "Triste", "Normal", "Feliz", "Muy Feliz"};
    String[] num2 = {"Llorar", "Suspirar", "Normal", "Yei!!", "Yupi!!"};
    int[] arrimg = {
            R.drawable.emotionb,
            R.drawable.emot,
            R.drawable.emotionc,
            R.drawable.emotion,
            R.drawable.emotiona
    };

    IndicatorSeekBar seekBarWithTickText;
    Button boton;
    ImageView img;
    //
    private ImageButton[] btn = new ImageButton[5];
    private ImageButton btn_unfocus;
    private Button BEscenaUnFocus;
   // private int[] ArregloBEmociones = {R.id.IBMuyTriste, R.id.IBTriste, R.id.IBNormal, R.id.IBFeliz, R.id.IBMuyFeliz};
    private ImageButton[] actionButtons;
    private int latestActionId;

    //Objetos recibidos por el intent
    private Obra obra;
    private int idPersonaje;

    //Índices de la opción seleccionada para cada acción
    private int[] actionReturns;

    //Variables para la recolección de los datos al momento de ejecutar
    private boolean isEmotionSelected;
    private int LatestActionSelectedId;
    private String emotionSelected;
    private int emotionIntensitySelected;
    private Map<Integer, String> ActionsSelected;

    private static final String TAG = "ViewsCreation";
    private static final String TAG2 = "DataCollection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//
        boton = findViewById(R.id.buttonAE);
        img = findViewById(R.id.imageView);
        seekBarWithTickText = findViewById(R.id.custom_text);
        seekBarWithTickText.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                int y = 0;

                if (seekParams.progress <= 50) {
                    y = seekParams.progress / (100 / (num.length - 1));
                } else {
                    y = (int) Math.ceil((seekParams.progress * 1.0) / (100.0 / ((num.length * 1.0) - 1.0)));
                }

                seekBarWithTickText.setIndicatorTextFormat(num[y] + " : " + "${PROGRESS}");
                boton.setText(num2[y]);
                img.setImageResource(arrimg[y]);

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            }
        });

        //
        obra = (Obra) getIntent().getSerializableExtra("obra");
        idPersonaje = (int) getIntent().getSerializableExtra("itemSelected");

        if (Picasso.get() == null) {
            Picasso picasso = new Picasso.Builder(getApplicationContext())
                    .downloader(new OkHttp3Downloader(getApplicationContext(), Integer.MAX_VALUE))
                    .build();
            Picasso.setSingletonInstance(picasso);
        }

        isEmotionSelected = false;
        LatestActionSelectedId = 0;
        emotionIntensitySelected = 100;
        ActionsSelected = new HashMap<>();

        ImageView IVCharacterIcon = findViewById(R.id.IVCharacterIcon);
        Picasso.get().load(obra.getCharacters()[idPersonaje - 1].getCharacterIconUrl()).into(IVCharacterIcon);
        TextView TVNombrePersonaje = findViewById(R.id.TVNombrePersonaje);
        TVNombrePersonaje.setText(obra.getCharacters()[idPersonaje - 1].getName());
/*
        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(ArregloBEmociones[i]);
            btn[i].setBackgroundColor(Color.rgb(255, 255, 255));
            btn[i].setOnClickListener(this);
        }
        btn_unfocus = btn[0];
*/
        loadScenes();

        Button BEjecutarCentral = findViewById(R.id.BEjecutarCentral);
        BEjecutarCentral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Collect all the actions and emotions selected, 50%
                if (isEmotionSelected && ActionsSelected.size() > 0) {
                    Emotion em = new Emotion(emotionSelected, emotionIntensitySelected);
                    Log.i(TAG2, "Se ha seleccionado lo siguiente:");
                    Log.i(TAG2, ActionsSelected.keySet().toString());
                    Log.i(TAG2, ActionsSelected.values().toString());
                    Log.i(TAG2, em.print());
                    new SocketClient().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor selecciona una emoción", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void loadScenes() {

        LinearLayout LLHEscenas = findViewById(R.id.LLHEscenas);
        LLHEscenas.removeAllViews();
        Scene[] escenas = obra.getScenes();
        int assignOnce = 0;

        for (final Scene iterator : escenas) {
            for (int i = 0; i < iterator.getCharacterIds().length; i++) {
                if (iterator.getCharacterIds()[i] == idPersonaje) {

                    final Button BEscena = new Button(this);

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
                            setFocusSceneButtons(BEscenaUnFocus, BEscena);
                            establecerAccionesDisponibles(iterator.getId());
                        }
                    });
                    if (assignOnce == 0) {
                        BEscenaUnFocus = BEscena;
                        assignOnce++;
                    }
                }
            }
        }
    }

    public void establecerAccionesDisponibles(final int idEscena) {

        for (Position p : obra.getScenes()[idEscena - 1].getPositions()) {
            for (Character c : obra.getCharacters()) {
                if (c.getId() == p.getNodeId()) {
                    c.setNodeId(p.getNodeId());
                }
            }
        }

        Log.i(TAG, "Estableciendo acciones para la escena " + idEscena + " y el personaje " + idPersonaje);
        LinearLayout LLHAcciones = findViewById(R.id.LLHAcciones);
        LLHAcciones.removeAllViews();
        final Scene escenaEscogida = obra.getScenes()[idEscena - 1];

        //Se crean tantos enteros de acciones como acciones hayan y se inicia en -1 para identificar la primera vez.
        actionReturns = new int[escenaEscogida.getActions().length];
        Arrays.fill(actionReturns, -1);
        //Se crean tantos tags de botones de acciones como acciones haya y se nombran según su posición
        actionButtons = new ImageButton[escenaEscogida.getActions().length];

        for (final Action iterator : escenaEscogida.getActions()) {

            //Si son nulas las opciones entonces es caminar o correr, se asignan los lugares del mapa excepto en donde está, TODO place exception 0%
            if (iterator.getDisplayText() == null) {
                String[]nodeNames = obra.getScenarios()[escenaEscogida.getScenario() - 1].getNodeNames();
                nodeNames = append(nodeNames, "Ninguno");
                iterator.setDisplayText(nodeNames);
            }

            if (iterator.getCharacterId() == 0 || iterator.getCharacterId() == idPersonaje) {

                Log.i(TAG, "Acciones encontrada para el personaje " + idPersonaje);
                final ImageButton IBAccion = new ImageButton(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(130, 130);

                params.setMargins(10, 0, 0, 0);
                IBAccion.setLayoutParams(params);

                Picasso.get().load(obra.getGenericActions()[iterator.getIdGeneric() - 1].getActionIconUrl()).resize(120, 120).into(IBAccion);

                LLHAcciones.addView(IBAccion);
                actionButtons[iterator.getId()-1] = IBAccion;
                IBAccion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IBAccion.setBackgroundColor(v.getResources().getColor(R.color.pressed_color));
                        latestActionId = iterator.getId()-1;
                        startActionActivities(idEscena, iterator.getId(), iterator.getIdGeneric());
                    }
                });
            }
        }
    }

    public void startActionActivities(int idScene, int idAccion, int idActionGeneric) {

        Action accion = obra.getScenes()[idScene - 1].getActions()[idAccion - 1];
        String[] options = accion.getDisplayText();
        String[] imageResourceIds;

        Log.d(TAG, "startActionActivities: " + Arrays.toString(options));

        if (accion.isHasImages() && accion.getImageUrls() != null) {
            imageResourceIds = accion.getImageUrls();
        } else {
            imageResourceIds = new String[1];
            imageResourceIds[0] = obra.getGenericActions()[idActionGeneric - 1].getActionIconUrl();
        }

        Intent intentTest = new Intent(getApplicationContext(), ActionActivity.class);

        intentTest.putExtra("options", options);
        intentTest.putExtra("hasImages", accion.isHasImages());
        intentTest.putExtra("imageResourceIds", imageResourceIds);
        intentTest.putExtra("id", actionReturns[idAccion - 1]);

        startActivityForResult(intentTest, idAccion - 1);

        LatestActionSelectedId = idActionGeneric - 1;
        ActionsSelected.put(LatestActionSelectedId, "none");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            assert data != null;
            actionReturns[requestCode] = data.getIntExtra("id", actionReturns[requestCode]);

            String parameter = data.getStringExtra("parameter");
            assert parameter != null;
            if(parameter.equals("Ninguno")){
                actionButtons[latestActionId].setBackgroundColor(Color.rgb(255, 255, 255));
            }
            ActionsSelected.put(LatestActionSelectedId, parameter);
            Log.i(TAG2, "Se ha actualizado lo siguiente:" + ActionsSelected.get(LatestActionSelectedId));

        }
    }
/*
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

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
*/
    @SuppressLint("ResourceAsColor")
    private void setFocus(ImageButton btn_unfocus, ImageButton btn_focus) {
        btn_unfocus.setBackgroundColor(Color.rgb(255, 255, 255));
        btn_focus.setBackgroundColor(R.color.pressed_color);
        this.btn_unfocus = btn_focus;
        isEmotionSelected = true;
    }

    @SuppressLint("ResourceAsColor")
    private void setFocusSceneButtons(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setBackgroundColor(Color.rgb(255, 255, 255));
        btn_focus.setBackgroundColor(R.color.pressed_color);
        this.BEscenaUnFocus = btn_focus;
    }

    static <T> T[] append(T[] arr, T element) {
        final int N = arr.length;
        arr = Arrays.copyOf(arr, N + 1);
        arr[N] = element;
        return arr;
    }



}
