package com.example.cobot.Acciones;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.cobot.CentralActivity;
import com.example.cobot.MainActivity;
import com.example.cobot.R;

public class AccionSonido extends AppCompatActivity implements View.OnClickListener {

    private Button[] btn = new Button[4];
    private Button btn_unfocus;
    private int[] btn_id = {R.id.btn_cantar, R.id.btn_gritar, R.id.btn_silbar, R.id.btn_reir};
    Button listo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accion_sonido);

        listo = findViewById(R.id.btn_listo);
//Sombrear Botones


        for (int i = 0; i < btn.length; i++) {
            btn[i] = (Button) findViewById(btn_id[i]);
            btn[i].setBackgroundColor(Color.rgb(207, 207, 207));
            btn[i].setOnClickListener(this);
        }

        btn_unfocus = btn[0];
/*
        //Dimesiones de la actividad
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int with = dm.widthPixels;
        int height = dm.heightPixels;


        getWindow().setLayout((int) (with * .9), (int) (height * .9));


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;

        getWindow().setAttributes(params);
*/
        listo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CentralActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save in savedInstanceState.

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state from the savedInstanceState.
        if(savedInstanceState != null) {

        }

    }

    @Override
    public void onClick(View v) {
        //setForcus(btn_unfocus, (Button) findViewById(v.getId()));
        //Or use switch

        switch (v.getId()) {
            case R.id.btn_cantar:
                setFocus(btn_unfocus, btn[0]);

                break;

            case R.id.btn_gritar:
                setFocus(btn_unfocus, btn[1]);

                break;

            case R.id.btn_silbar:
                setFocus(btn_unfocus, btn[2]);

                break;

            case R.id.btn_reir:
                setFocus(btn_unfocus, btn[3]);

                break;


        }
    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {
        //btn_unfocus.setTextColor(Color.rgb(49, 50, 51));
        btn_unfocus.setBackgroundColor(Color.rgb(207, 207, 207));
        // btn_focus.setTextColor(Color.rgb(255, 255, 255));
        btn_focus.setBackgroundColor(Color.rgb(3, 106, 150));
        this.btn_unfocus = btn_focus;
    }
}