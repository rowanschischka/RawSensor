package com.example.rowanschischka.rawsensor;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Calendar;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private LinearLayout linearLayout;
    private TextView accelerometerX, accelerometerY, accelerometerZ;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        linearLayout = (LinearLayout) findViewById(R.id.sensorLinearLayout);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        accelerometerX = (TextView) findViewById(R.id.accelerometerX);
        accelerometerY = (TextView) findViewById(R.id.accelerometerY);
        accelerometerZ = (TextView) findViewById(R.id.accelerometerZ);

        Intent intent = getIntent();
        sensorManager.registerListener(this, sensor, intent.getIntExtra("SENSOR_DELAY", SensorManager.SENSOR_DELAY_FASTEST));

        TextView sensorResolutionTV = (TextView) findViewById(R.id.resolution);
        sensorResolutionTV.setText("Resolution " + sensor.getResolution());

        TextView vendor = (TextView) findViewById(R.id.vendor);
        vendor.setText("Vendor: " + sensor.getVendor() + " version: " +
                sensor.getVersion());

        //TextView delay = (TextView) findViewById(R.id.delay);
        //delay.setText(""+intent.getIntExtra("SENSOR_DELAY", SensorManager.SENSOR_DELAY_FASTEST));
        dbHelper = new DbHelper(this);
        // Gets the data repository in write mode
        db = dbHelper.getWritableDatabase();
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        sensorManager.registerListener(this, sensor, intent.getIntExtra("SENSOR_DELAY", SensorManager.SENSOR_DELAY_FASTEST));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor eSensor = event.sensor;

        if (eSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            accelerometerX.setText("X: " + x);
            accelerometerY.setText("Y: " + y);
            accelerometerZ.setText("Z: " + z);

            long upTime = SystemClock.uptimeMillis();

            TextView clockTV = (TextView) findViewById(R.id.clock);
            clockTV.setText("Time " + upTime);

            ContentValues values = new ContentValues();
            values.put(DataBaseSchema.COLUMN_NAME_X, x);
            values.put(DataBaseSchema.COLUMN_NAME_Y, y);
            values.put(DataBaseSchema.COLUMN_NAME_Z, z);
            values.put(DataBaseSchema.COLUMN_NAME_TIME, upTime);

            db.insert(DataBaseSchema.TABLE_NAME, null, values);
            Log.d("db", "Inserted " + x + ", " + y + ", " + z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
