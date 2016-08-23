package com.example.rowanschischka.rawsensor;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by rowanschischka on 20/08/16.
 */
public class SensorBenchmarkActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private int count = 0;
    private int accuracyChangeCount = 0;
    private int numberOfSensorEvents = 1000;

    int[] sensorDelay = {
            SensorManager.SENSOR_DELAY_FASTEST,
            SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_GAME,
            SensorManager.SENSOR_DELAY_UI
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_benchmark);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * switch to next sensor delay value
     */
    protected void nextSensor() {

    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor eSensor = event.sensor;

        if (eSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            count++;
            if (count > numberOfSensorEvents){

            }
        }
    }

    /**
     * count sensor changed
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
