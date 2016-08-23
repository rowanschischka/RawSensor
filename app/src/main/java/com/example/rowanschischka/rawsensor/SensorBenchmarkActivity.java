package com.example.rowanschischka.rawsensor;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by rowanschischka on 20/08/16.
 */
public class SensorBenchmarkActivity extends AppCompatActivity implements SensorEventListener {

    int[] sensorDelay = {
            SensorManager.SENSOR_DELAY_FASTEST,
            SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_GAME,
            SensorManager.SENSOR_DELAY_UI
    };
    /*
    * results array[x][y]
    * index [x] = sensorDelay fastest, normal, game, ui
    * index [y] :   0 = count
    *               1 = accuracyChangeCount
    * */
    int[][] results = new int[sensorDelay.length][2];
    TextView[] resultViews;
    private SensorManager sensorManager;
    private Sensor sensor;
    private int numberOfSensorEvents = 1000;
    private int delayIndex = 0;
    private long startTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_benchmark);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        TextView settingsTV = (TextView) findViewById(R.id.settings_value);
        String setText = "Min Delay: " + sensor.getMinDelay();

        settingsTV.setText(setText);

        resultViews = new TextView[]{
                (TextView) findViewById(R.id.fastest_result),
                (TextView) findViewById(R.id.normal_result),
                (TextView) findViewById(R.id.game_result),
                (TextView) findViewById(R.id.ui_result),
        };
        sensorManager.registerListener(this, sensor, sensorDelay[delayIndex]);
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    //// TODO: 24/08/16 write results to disk and repeat 10X 
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor eSensor = event.sensor;
        if (eSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (startTime < 0) {
                startTime = SystemClock.uptimeMillis();
            }
            results[delayIndex][0]++;
            if (results[delayIndex][0] >= numberOfSensorEvents) {
                long timeTaken = (SystemClock.uptimeMillis() - startTime);
                long eventCount = (long) results[delayIndex][0];
                String resultString = eventCount + " events in " + timeTaken + " ms.\n";
                //4 bytes per float * 3 floats for x, y, z = 12 bytes per event
                resultString += (12 * eventCount / timeTaken) + " bytes/ms.\n";
                resultString += (timeTaken / eventCount) + " ms between events.\n";
                resultString += eventCount + " accuracy change occurred";
                resultViews[delayIndex].setText(resultString);
                startTime = -1;
                sensorManager.unregisterListener(this);
                delayIndex++;
                if (delayIndex < sensorDelay.length) {
                    sensorManager.registerListener(this, sensor, sensorDelay[delayIndex]);
                }
            }
        }
    }

    /**
     * count sensor changed
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            results[delayIndex][1]++;
        }
    }
}
