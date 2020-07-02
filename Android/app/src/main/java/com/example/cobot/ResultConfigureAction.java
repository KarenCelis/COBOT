package com.example.cobot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ResultConfigureAction extends AppCompatActivity {
    TextView a, b, c, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_configure_action);
        a = findViewById(R.id.textView1);
        b = findViewById(R.id.textView11);
        c = findViewById(R.id.textView111);
        d = findViewById(R.id.textView1111);
        Bundle bundle = getIntent().getBundleExtra("bundle");

        a.setText(bundle.getString("accion"));
        b.setText(bundle.getString("mood"));
        c.setText(bundle.getString("emocion"));
        d.setText(bundle.getString("dialogo"));


    }
}