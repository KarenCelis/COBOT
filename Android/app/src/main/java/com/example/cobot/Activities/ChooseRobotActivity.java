package com.example.cobot.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cobot.R;
import com.example.cobot.Utils.SocketClient;
import com.example.cobot.Utils.Writer;

import org.json.JSONException;

public class ChooseRobotActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton[] btn = new ImageButton[2];
    private ImageButton btn_unfocus;
    private int[] btn_id = {R.id.imgbtn_1, R.id.imgbtn_2};
    private Button BListo;

    private ChooseRobotActivity.MyReceiver myReceiver;
    private boolean mIsBound;

    private SocketClient mBoundService;
    private String robot;

    private String server_port;
    private String ipAddress;
    private SharedPreferences preferences;
    private TextView TVServerip;

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
        setContentView(R.layout.activity_choose_robot);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        for(int i = 0; i < btn.length; i++){
            btn[i] = findViewById(btn_id[i]);
            btn[i].setBackgroundColor(Color.rgb(207, 207, 207));
            btn[i].setOnClickListener(this);
        }

        BListo = findViewById(R.id.BListo);
        BListo.setOnClickListener(this);

        btn_unfocus = btn[0];

        preferences = this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        ipAddress = preferences.getString(getString(R.string.pref_ip_address), getString(R.string.pref_ip_address_default));
        server_port = preferences.getString(getString(R.string.pref_port), getString(R.string.pref_port_default));

        TVServerip = findViewById(R.id.TVServerip);
        String texto = ipAddress;
        TVServerip.setText(texto);

        Button BCambiaripServer = findViewById(R.id.BCambiaripServer);
        BCambiaripServer.setOnClickListener(this);

        myReceiver = new ChooseRobotActivity.MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ACTION");
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public void onClick(View v){

        switch (v.getId()){

            case R.id.imgbtn_1 :
                robot = "nao";

                setFocus(btn_unfocus, btn[0]);
                createDialogForConnection();
                ////quuitar esto
                BListo.setVisibility(View.VISIBLE);
                break;
            case R.id.imgbtn_2 :
                robot = "quyca";
                setFocus(btn_unfocus, btn[1]);
                createDialogForConnection();
                ////quuitar esto
                BListo.setVisibility(View.VISIBLE);
                break;
            case R.id.BListo:
                Intent intent = new Intent(v.getContext(), ChooseFileActivity.class);
                startActivity(intent);
                break;
            case R.id.BCambiaripServer:
                createDialogForConnectionToServer();
                break;
        }
    }

    private void setFocus(ImageButton btn_unfocus, ImageButton btn_focus){
        btn_unfocus.setBackgroundColor(Color.rgb(207, 207, 207));
        btn_focus.setBackgroundColor(Color.rgb(171, 252, 143));
        this.btn_unfocus = btn_focus;
    }

    public void createDialogForConnection(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View actionPopup = getLayoutInflater().inflate(R.layout.layout_connection, null);
        final EditText ip = actionPopup.findViewById(R.id.edtxt_ip);
        final EditText port = actionPopup.findViewById(R.id.edtxt_port);
        ip.setFilters(setIpFilter());
        Button connect = actionPopup.findViewById(R.id.btn_connect);

        dialogBuilder.setView(actionPopup);
        final AlertDialog dialog = dialogBuilder.create();

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    SocketClient.getServerConnectionInstance(ipAddress,Integer.parseInt(server_port));
                    SocketClient.getConnectionInstance(ip.getText().toString(),Integer.parseInt(port.getText().toString()), robot);
                    enviarSocket(ip.getText().toString(), Integer.parseInt(port.getText().toString()),1);
                    BListo.setVisibility(View.VISIBLE);
                }catch (final NumberFormatException | JSONException e) {
                    Toast.makeText(getApplicationContext(), "Ingrese una ip y puerto válidos", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }finally{
                    dialog.dismiss();
                }
            }
        });
        dialog.show();

    }

    public void createDialogForConnectionToServer(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View actionPopup = getLayoutInflater().inflate(R.layout.layout_connection, null);
        final EditText ip = actionPopup.findViewById(R.id.edtxt_ip);
        ip.setText(ipAddress);
        final EditText port = actionPopup.findViewById(R.id.edtxt_port);
        port.setText(server_port);
        ip.setFilters(setIpFilter());
        Button connect = actionPopup.findViewById(R.id.btn_connect);

        dialogBuilder.setView(actionPopup);
        final AlertDialog dialog = dialogBuilder.create();

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    String newIp = ip.getText().toString();
                    String newPort = port.getText().toString();

                    if(!newIp.equals(ipAddress)){
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.pref_ip_address), ip.getText()+"");
                        editor.apply();
                        ipAddress = ip.getText()+"";
                        String texto = "conectado al servidor "+ipAddress;
                        TVServerip.setText(texto);
                    }
                    if(!newPort.equals(server_port)){
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.pref_port), port.getText()+"");
                        editor.apply();
                        server_port = port.getText()+"";
                    }

                    SocketClient.getServerConnectionInstance(ip.getText().toString(),Integer.parseInt(port.getText().toString()));
                    enviarSocket(ip.getText().toString(), Integer.parseInt(port.getText().toString()), 0);
                }catch (final NumberFormatException | JSONException e) {
                    Toast.makeText(getApplicationContext(), "Ingrese una ip y puerto válidos", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }finally{
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public InputFilter[] setIpFilter(){
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend);
                    if (!resultingTxt
                            .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (String split : splits) {
                            if (Integer.parseInt(split) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };
        return filters;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void doBindService(){
        bindService(new Intent(ChooseRobotActivity.this, SocketClient.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //SocketClient.setJsonToSend(Writer.writeServerCommunicationJSON("Conectando con el servidor...").toString());
            //startService(new Intent(ChooseRobotActivity.this, SocketClient.class));
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SocketClient.ACTION);
            registerReceiver(myReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
        stopService(new Intent(ChooseRobotActivity.this, SocketClient.class));
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String message = "conectando...";
            if (extras != null) {
                if (extras.containsKey(SocketClient.SERVER_RESPONSE)) {
                    message = intent.getStringExtra(SocketClient.SERVER_RESPONSE);
                }else if(extras.containsKey(SocketClient.SERVER_SET_CONNECTION)){
                    message = intent.getStringExtra(SocketClient.SERVER_SET_CONNECTION);
                }else{
                    message = intent.getStringExtra(SocketClient.SERVER_CONNECTION);
                }
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
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

    public void enviarSocket(String ip, int port, int option) throws JSONException {
        if(isNetworkConnected()){
            if(option == 0){
                SocketClient.setJsonToSend(Writer.writeServerCommunicationJSON(ip, port).toString());
            }else{
                SocketClient.setJsonToSend(Writer.writeConnectionJSON(ip, port, robot).toString());
            }
            Log.i("Enviando", "C: Enviando"+SocketClient.jsonToSend);
            startService(new Intent(ChooseRobotActivity.this, SocketClient.class));
            doBindService();
            //mBoundService.sendMessage(Writer.writeConnectionJSON(ip.getText().toString(), Integer.parseInt(port.getText().toString())).toString());
        }else{
            Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
        }
    }
}