package com.example.cobot.Acciones;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import com.example.cobot.R;

public class AccionSonido extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accion_sonido);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int with = dm.widthPixels;
        int height = dm.heightPixels;


        getWindow().setLayout((int)(with*.9),(int)(height*.9));


        WindowManager.LayoutParams params= getWindow().getAttributes();
        params.gravity= Gravity.CENTER;

        params.x = 10
        ;
        params.y=-10;

        getWindow().setAttributes(params);

    }
}