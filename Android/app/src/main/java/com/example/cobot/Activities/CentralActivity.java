package com.example.cobot.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cobot.Classes.Action;
import com.example.cobot.Classes.Character;
import com.example.cobot.Classes.Emotion;
import com.example.cobot.Classes.GenericAction;
import com.example.cobot.Classes.Obra;
import com.example.cobot.Classes.Position;
import com.example.cobot.Classes.Scenario;
import com.example.cobot.Classes.Scene;
import com.example.cobot.R;
import com.example.cobot.Utils.SocketClient;
import com.example.cobot.Utils.Writer;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CentralActivity extends AppCompatActivity implements View.OnClickListener {

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

    private Button BEscenaUnFocus;

    private ImageButton[] actionButtons;
    private int latestActionId;

    //Objetos recibidos por el intent
    private Obra obra;
    private int idPersonaje;

    //Índices de la opción seleccionada para cada acción
    private int[] actionReturns;

    //Variables para la recolección de los datos al momento de ejecutar
    private int LatestActionSelectedId;
    private String emotionSelected = "Normal";
    private int emotionIntensitySelected = 50;
    private Map<Integer, String> actionsSelected;

    private static final String TAG = "ViewsCreation";
    private static final String TAG2 = "DataCollection";

    private CentralActivity.MyReceiver myReceiver;
    private boolean mIsBound;

    private SocketClient mBoundService;

    private final ServiceConnection mConnection = new ServiceConnection() {
        //EDITED PART
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((SocketClient.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        boton = findViewById(R.id.buttonAE);
        boton.setOnClickListener(this);
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

                emotionSelected = num[y];
                emotionIntensitySelected = seekParams.progress;

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            }
        });


        obra = (Obra) getIntent().getSerializableExtra("obra");
        idPersonaje = (int) getIntent().getSerializableExtra("itemSelected");

        if (Picasso.get() == null) {
            Picasso picasso = new Picasso.Builder(getApplicationContext())
                    .downloader(new OkHttp3Downloader(getApplicationContext(), Integer.MAX_VALUE))
                    .build();
            Picasso.setSingletonInstance(picasso);
        }

        LatestActionSelectedId = 0;
        actionsSelected = new HashMap<>();

        ImageView IVCharacterIcon = findViewById(R.id.IVCharacterIcon);
        Picasso.get().load(obra.getCharacters()[idPersonaje - 1].getCharacterIconUrl()).into(IVCharacterIcon);
        TextView TVNombrePersonaje = findViewById(R.id.TVNombrePersonaje);
        TVNombrePersonaje.setText(obra.getCharacters()[idPersonaje - 1].getName());
        loadScenes();

        Button BEjecutarCentral = findViewById(R.id.BEjecutarCentral);
        BEjecutarCentral.setOnClickListener(this);

        myReceiver = new CentralActivity.MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ACTION");
        registerReceiver(myReceiver, intentFilter);
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
                            obtenerModeloDelMundo(iterator.getId());
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

            //Si son nulas las opciones entonces es caminar o correr, se asignan los lugares del mapa excepto en donde está
            if (iterator.getDisplayText() == null) {
                String[] originalNodes = obra.getScenarios()[escenaEscogida.getScenario() - 1].getNode_names();
                ArrayList<String> nodeNames = new ArrayList<>();
                for (int i = 0; i < originalNodes.length; i++) {
                    //remover la posición actual
                    if (i != obra.getCharacters()[idPersonaje - 1].getNodeId() - 1) {
                        nodeNames.add(originalNodes[i]);
                    }
                }
                nodeNames.add("Ninguno");
                String[] nodeNamesArray = new String[nodeNames.size()];
                for (int i = 0; i < nodeNames.size(); i++) {
                    nodeNamesArray[i] = nodeNames.get(i);
                }
                iterator.setDisplayText(nodeNamesArray);
            }

            if (iterator.getCharacterId() == 0 || iterator.getCharacterId() == idPersonaje) {

                Log.i(TAG, "Acciones encontrada para el personaje " + idPersonaje);
                final ImageButton IBAccion = new ImageButton(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(130, 130);

                params.setMargins(10, 0, 0, 0);
                IBAccion.setLayoutParams(params);

                Picasso.get().load(obra.getGenericActions()[iterator.getIdGeneric() - 1].getActionIconUrl()).resize(120, 120).into(IBAccion);

                LLHAcciones.addView(IBAccion);
                actionButtons[iterator.getIdGeneric() - 1] = IBAccion;
                IBAccion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IBAccion.setBackgroundColor(v.getResources().getColor(R.color.pressed_color));
                        IBAccion.setTag(iterator.getIdGeneric());
                        latestActionId = iterator.getIdGeneric() - 1;
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
        actionsSelected.put(LatestActionSelectedId, "none");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            assert data != null;
            actionReturns[requestCode] = data.getIntExtra("id", actionReturns[requestCode]);

            String parameter = data.getStringExtra("parameter");
            assert parameter != null;
            if (parameter.equals("Ninguno")) {
                actionButtons[latestActionId].setBackgroundColor(Color.rgb(255, 255, 255));
                unBlockActions(latestActionId);
            } else {
                blockActions(latestActionId);
            }
            actionsSelected.put(LatestActionSelectedId, parameter);
            Log.i(TAG2, "Se ha actualizado lo siguiente:" + actionsSelected.get(LatestActionSelectedId));

        }
    }

    public void blockActions(int idActionGeneric) {
        GenericAction genericAction = obra.getGenericActions()[idActionGeneric];
        for (int i = 0; i < genericAction.getBlocks().length; i++) {
            for (int j = 0; j < actionButtons.length; j++) {
                Log.i(TAG, "blockActions: " + genericAction.getBlocks()[i] + ", " + j);
                if (genericAction.getBlocks()[i] - 1 == j && actionButtons[j] != null) {
                    actionButtons[j].setEnabled(false);
                }
            }
        }
    }

    public void unBlockActions(int idActionGeneric) {
        GenericAction genericAction = obra.getGenericActions()[idActionGeneric];
        for (int i = 0; i < genericAction.getBlocks().length; i++) {
            for (int j = 0; j < actionButtons.length; j++) {
                Log.i(TAG, "blockActions: " + genericAction.getBlocks()[i] + ", " + j);
                if (genericAction.getBlocks()[i] - 1 == j && actionButtons[j] != null) {
                    actionButtons[j].setEnabled(true);
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private void setFocusSceneButtons(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setBackgroundColor(Color.rgb(255, 255, 255));
        btn_focus.setBackgroundColor(R.color.pressed_color);
        this.BEscenaUnFocus = btn_focus;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            SocketClient.setJsonToSend(Writer.writeServerCommunicationJSON("Volviendo a hacer conexion con el servidor de Python").toString());
            startService(new Intent(CentralActivity.this, SocketClient.class));
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SocketClient.ACTION);
            registerReceiver(myReceiver, intentFilter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
        stopService(new Intent(CentralActivity.this, SocketClient.class));
    }

    private void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAE:
                try {
                    enviarAccionEmergente(boton.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.BEjecutarCentral:
                if (actionsSelected.size() > 0 && verificarAccionesSeleccionadas()) {

                    Emotion em = new Emotion(emotionSelected, emotionIntensitySelected);

                    Log.i(TAG2, "Se ha seleccionado lo siguiente:");
                    Log.i(TAG2, actionsSelected.keySet().toString());
                    Log.i(TAG2, actionsSelected.values().toString());
                    Log.i(TAG2, em.print());

                    try {
                        enviarSocket(actionsSelected, em);
                        //mBoundService.sendMessage(Writer.writeJSON(actionsSelected, em).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Por favor selecciona al menos una acción", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String message = "conectando...";
            if (extras != null) {
                if (extras.containsKey(SocketClient.SERVER_RESPONSE)) {
                    message = intent.getStringExtra(SocketClient.SERVER_RESPONSE);
                } else {
                    message = intent.getStringExtra(SocketClient.SERVER_CONNECTION);
                }
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void doBindService() {
        bindService(new Intent(CentralActivity.this, SocketClient.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void enviarSocket(Map<Integer, String> actionsSelected, Emotion em) throws JSONException {
        if (isNetworkConnected()) {
            SocketClient.setJsonToSend(Writer.writeSimpleActionsJSON(actionsSelected, em).toString());
            Log.i("Enviando", "C: Enviando" + SocketClient.jsonToSend);
            startService(new Intent(CentralActivity.this, SocketClient.class));
            doBindService();
            //mBoundService.sendMessage(Writer.writeConnectionJSON(ip.getText().toString(), Integer.parseInt(port.getText().toString())).toString());
        } else {
            Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
        }
    }

    public void enviarAccionEmergente(String accion) throws JSONException {
        if (isNetworkConnected()) {
            SocketClient.setJsonToSend(Writer.writeEmergentAction(accion).toString());
            Log.i("Enviando", "C: Enviando" + SocketClient.jsonToSend);
            startService(new Intent(CentralActivity.this, SocketClient.class));
            doBindService();
            //mBoundService.sendMessage(Writer.writeConnectionJSON(ip.getText().toString(), Integer.parseInt(port.getText().toString())).toString());
        } else {
            Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
        }
    }

    public boolean verificarAccionesSeleccionadas(){

        for (Map.Entry<Integer, String> entry : actionsSelected.entrySet()) {
            if(!entry.getValue().equals("Ninguno")){
                return true;
            }
        }
        return false;
    }

    public void obtenerModeloDelMundo(int idEscena) {
        try {

            Position positionToSend = new Position();
            Scenario scenarioToSend = new Scenario();

            for (Scene scene : obra.getScenes()) {

                if (scene.getId() == idEscena) {

                    for (Position position : scene.getPositions()) {

                        if (position.getCharacterId() == idPersonaje) {

                            positionToSend = position;
                        }
                    }
                    for (Scenario scenario : obra.getScenarios()) {

                        if (scenario.getId() == scene.getScenario()) {

                            scenarioToSend = scenario;
                        }
                    }
                }
            }

            enviarModeloDelMundo(scenarioToSend, positionToSend);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void enviarModeloDelMundo(Scenario scenario, Position position) throws JSONException {
        if (isNetworkConnected()) {
            SocketClient.setJsonToSend(Writer.writeWorldModel(scenario, position).toString());
            Log.i("Enviando", "C: Enviando" + SocketClient.jsonToSend);
            startService(new Intent(CentralActivity.this, SocketClient.class));
            doBindService();
            //mBoundService.sendMessage(Writer.writeConnectionJSON(ip.getText().toString(), Integer.parseInt(port.getText().toString())).toString());
        } else {
            Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
        }
    }
}
