package com.example.cobot.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.cobot.R;
import com.example.cobot.Utils.Connection;

public class ChooseRobotActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton[] btn = new ImageButton[2];
    private ImageButton btn_unfocus;
    private int[] btn_id = {R.id.imgbtn_1, R.id.imgbtn_2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_robot);
        for(int i = 0; i < btn.length; i++){
            btn[i] = findViewById(btn_id[i]);
            btn[i].setBackgroundColor(Color.rgb(207, 207, 207));
            btn[i].setOnClickListener(this);
        }

        btn_unfocus = btn[0];

    }

    @Override
    public void onClick(View v){

        switch (v.getId()){
            case R.id.imgbtn_1 :
                setFocus(btn_unfocus, btn[0]);

                createDialogForConnection();
                break;
            case R.id.imgbtn_2 :
                setFocus(btn_unfocus, btn[1]);
                createDialogForConnection();
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
        Button connect = actionPopup.findViewById(R.id.btn_connect);
        dialogBuilder.setView(actionPopup);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseFileActivity.class);
                try{
                    Connection connection = new Connection(ip.getText().toString(),Integer.parseInt(port.getText().toString()));
                    Toast.makeText(getApplicationContext(), "conectado", Toast.LENGTH_LONG).show();
                }catch (final NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Ingrese una ip y puerto válidos", Toast.LENGTH_LONG).show();
                }
                startActivity(intent);
               // dialog.dismiss();
            }
        });

    }


}