package com.example.cobot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.cobot.Utils.RecyclerViewAdapter;

import java.util.ArrayList;

public class ActionActivity extends AppCompatActivity implements RecyclerViewAdapter.OnOptionListener{

    private String[] options;
    private static final String TAG = "ActionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        RecyclerView recyclerView = findViewById(R.id.RVActions);

        Intent resultado = getIntent();

        options = resultado.getStringArrayExtra("options");
        boolean hasImages = resultado.getBooleanExtra("hasImages", false);
        String[] imageResourceIds = resultado.getStringArrayExtra("imageResourceIds");
        int actionSelected = resultado.getIntExtra("id", 10);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(options, hasImages, imageResourceIds, this, actionSelected);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    public void onOptionClick(int position) {
        Log.d(TAG, "onOptionClick: "+options[position]);
        Intent intent = new Intent();
        intent.putExtra("parameter", options[position]);
        intent.putExtra("id", position);
        setResult(RESULT_OK, intent);
        finish();
    }
}