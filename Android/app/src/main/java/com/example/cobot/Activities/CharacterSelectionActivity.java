package com.example.cobot.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cobot.Classes.Character;
import com.example.cobot.Classes.Obra;
import com.example.cobot.R;
import com.example.cobot.Utils.SocketClient;
import com.example.cobot.Utils.Writer;

import org.json.JSONException;

import java.util.ArrayList;

public class CharacterSelectionActivity extends AppCompatActivity {

    private Obra obra;
    private int itemSelected = 1;

    private CharacterSelectionActivity.MyReceiver myReceiver;
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
        setContentView(R.layout.activity_character_selection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        try {
            SocketClient.setJsonToSend(Writer.writeServerCommunicationJSON("Conectando con el servidor...").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                try {
                    enviarSocket();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        myReceiver = new CharacterSelectionActivity.MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ACTION");
        registerReceiver(myReceiver, intentFilter);
    }

    private void IniciarActividadCentral() {
        Intent intent = new Intent(this, CentralActivity.class);
        intent.putExtra("obra", obra);
        intent.putExtra("itemSelected", itemSelected);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            startService(new Intent(CharacterSelectionActivity.this, SocketClient.class));
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SocketClient.ACTION);
            registerReceiver(myReceiver, intentFilter);
            enviarSocket();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
        stopService(new Intent(CharacterSelectionActivity.this, SocketClient.class));
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

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String message = "conectando...";
            if (extras != null) {
                if (extras.containsKey(SocketClient.SERVER_RESPONSE)) {
                    message = intent.getStringExtra(SocketClient.SERVER_RESPONSE);
                }else{
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

    private void doBindService(){
        bindService(new Intent(CharacterSelectionActivity.this, SocketClient.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    public void enviarSocket() throws JSONException {
        if(isNetworkConnected()){
            SocketClient.setJsonToSend(Writer.writeSignsOfLife(obra.getSignsOfLife(), itemSelected).toString());
            Log.i("Enviando", "C: Enviando"+SocketClient.jsonToSend);
            startService(new Intent(CharacterSelectionActivity.this, SocketClient.class));
            doBindService();
            //mBoundService.sendMessage(Writer.writeConnectionJSON(ip.getText().toString(), Integer.parseInt(port.getText().toString())).toString());
        }else{
            Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
        }
    }
}