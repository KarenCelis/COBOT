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

import com.example.cobot.R;

public class AccionMirar extends Activity implements View.OnClickListener {

    private Button[] btn = new Button[3];
    private Button btn_unfocus, botontn;
    private int[] btn_id = {R.id.btn_mirarIzq, R.id.btn_mirarDerec, R.id.btn_mirarFrente};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accion_mirar);



//Sombrear Botones


        for (int i = 0; i < btn.length; i++) {
            btn[i] = (Button) findViewById(btn_id[i]);
            btn[i].setBackgroundColor(Color.rgb(207, 207, 207));
            btn[i].setOnClickListener(this);
        }

        btn_unfocus = btn[0];

        ////
        Intent resultado = getIntent();
        int idresult = resultado.getIntExtra("id2",0);
        if(idresult!=0){
            botontn = findViewById(idresult);
            botontn.setBackgroundColor(Color.rgb(3, 106, 150));
            btn_unfocus=botontn;
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
        //setForcus(btn_unfocus, (Button) findViewById(v.getId()));
        //Or use switch

        switch (v.getId()) {
            case R.id.btn_mirarIzq:
                setFocus(btn_unfocus, btn[0]);
                volver(R.id.btn_mirarIzq);
                //Log.d("pruebaid",String.valueOf(v.getId()));
                //Log.d("prueba",v.toString());

                break;

            case R.id.btn_mirarDerec:
                setFocus(btn_unfocus, btn[1]);
                volver(R.id.btn_mirarDerec);
                //Log.d("prueba",v.toString());
                break;

            case R.id.btn_mirarFrente:
                setFocus(btn_unfocus, btn[2]);
                volver(R.id.btn_mirarFrente);
                break;




        }
    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setBackgroundColor(Color.rgb(207, 207, 207));
        btn_focus.setBackgroundColor(Color.rgb(3, 106, 150));
        this.btn_unfocus = btn_focus;
    }
}