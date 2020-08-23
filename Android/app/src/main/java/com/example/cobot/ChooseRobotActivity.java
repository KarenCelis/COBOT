package com.example.cobot;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ChooseRobotActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageButton[] btn = new ImageButton[2];
    private ImageButton btn_unfocus;
    private int[] btn_id = {R.id.imgbtn_1, R.id.imgbtn_2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_robot);
        for(int i = 0; i < btn.length; i++){
            btn[i] = (ImageButton) findViewById(btn_id[i]);
            btn[i].setBackgroundColor(Color.rgb(207, 207, 207));
            btn[i].setOnClickListener(this);
        }

        btn_unfocus = btn[0];

    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent(this, MainActivity.class);
        switch (v.getId()){
            case R.id.imgbtn_1 :
                setFocus(btn_unfocus, btn[0]);
                startActivity(intent);
                break;
            case R.id.imgbtn_2 :
                setFocus(btn_unfocus, btn[1]);
                startActivity(intent);
                break;
        }
    }

    private void setFocus(ImageButton btn_unfocus, ImageButton btn_focus){
        btn_unfocus.setBackgroundColor(Color.rgb(207, 207, 207));
        btn_focus.setBackgroundColor(Color.rgb(171, 252, 143));
        this.btn_unfocus = btn_focus;
    }
}