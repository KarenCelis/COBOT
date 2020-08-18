package com.example.cobot.Acciones;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.cobot.CentralActivity;
import com.example.cobot.MainActivity;
import com.example.cobot.R;

public class AccionSonido extends Activity implements View.OnClickListener {

    private Button[] btn = new Button[4];
    private Button btn_unfocus, botontn;
    private int[] btn_id = {R.id.btn_cantar, R.id.btn_gritar, R.id.btn_silbar, R.id.btn_reir};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accion_sonido);


//Sombrear Botones


        for (int i = 0; i < btn.length; i++) {
            btn[i] = (Button) findViewById(btn_id[i]);
            btn[i].setBackgroundColor(Color.rgb(207, 207, 207));
            btn[i].setOnClickListener(this);
        }

        btn_unfocus = btn[0];

        ////Actualizar el estado del boton
        Intent resultado = getIntent();
        int idresult = resultado.getIntExtra("id2", 0);
        if (idresult != 0) {
            botontn = findViewById(idresult);
            botontn.setBackgroundColor(Color.rgb(3, 106, 150));
            btn_unfocus = botontn;
        }

        //Dimesiones de la actividad
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int with = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (with * .9), (int) (height * .9));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);


    }

    private void volver(int id) {
        Intent intent = new Intent();
        intent.putExtra("id", id);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_cantar:
                setFocus(btn_unfocus, btn[0]);
                volver(R.id.btn_cantar);
                //Log.d("pruebaid",String.valueOf(v.getId()));
                //Log.d("prueba",v.toString());

                break;

            case R.id.btn_gritar:
                setFocus(btn_unfocus, btn[1]);
                volver(R.id.btn_gritar);
                //Log.d("prueba",v.toString());
                break;

            case R.id.btn_silbar:
                setFocus(btn_unfocus, btn[2]);
                volver(R.id.btn_silbar);
                break;

            case R.id.btn_reir:
                setFocus(btn_unfocus, btn[3]);
                volver(R.id.btn_reir);
                break;


        }
    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setBackgroundColor(Color.rgb(207, 207, 207));
        btn_focus.setBackgroundColor(Color.rgb(3, 106, 150));
        this.btn_unfocus = btn_focus;
    }
}