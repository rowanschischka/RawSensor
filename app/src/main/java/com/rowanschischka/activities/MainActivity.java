package com.rowanschischka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rowanschischka.data.android.SensorToCSVService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startRecording(View view) {
        findViewById(R.id.start).setVisibility(View.INVISIBLE);
        findViewById(R.id.stop).setVisibility(View.VISIBLE);
        startService(new Intent(getBaseContext(), SensorToCSVService.class));
    }

    public void stopRecording(View view) {
        findViewById(R.id.stop).setVisibility(View.INVISIBLE);
        findViewById(R.id.start).setVisibility(View.VISIBLE);
        stopService(new Intent(getBaseContext(), SensorToCSVService.class));
    }
}