package com.rowanschischka.rawsensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class SensorActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    long startTime;
    //GPS
    LocationManager locationManager;
    float[] mGravity;
    float[] mGeomagnetic;
    float[] orientation;
    //UI
    private TextView sensorTV, gpsTV;
    private SQLiteDatabase db;
    //sensor
    private SensorManager sensorManager = null;
    private Sensor rotationVectorSensor, accelerationSensor, gyroSensor, magnetometer;

    public void onClickStop(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //user interface
        setContentView(R.layout.activity_sensor);
        sensorTV = (TextView) findViewById(R.id.text_recording);
        gpsTV = (TextView) findViewById(R.id.gps_recording);
        //counters
        startTime = 0;
        //initialize sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);
        accelerationSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        orientation = new float[3];
        //initialize database
        DbHelper dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
        //initialize GPS
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        //sensors
        sensorManager.registerListener(this, accelerationSensor, 20000);
        sensorManager.registerListener(this, magnetometer, 20000);
        //GPS
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkStartTime() {
        if (startTime == 0) {
            startTime = SystemClock.elapsedRealtimeNanos();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        checkStartTime();
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                insertSensor("ACCELEROMETER", event.values, event.timestamp);
                mGravity = event.values;
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                //insertSensor("TYPE_ROTATION_VECTOR", event.values, event.timestamp);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
            default:
                return;
        }
        String text = "SENSORS" +
                "\nTime: " + (event.timestamp - startTime);

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                insertSensor("RADIANS", orientation, event.timestamp);
                float[] inDegrees = {
                        (float) Math.toDegrees(orientation[0]),
                        (float) Math.toDegrees(orientation[1]),
                        (float) Math.toDegrees(orientation[2])};
                insertSensor("DEGREES", inDegrees, event.timestamp);
            }
            mGravity = null;
            mGeomagnetic = null;
        }
        text += "\n" +
                "\nAzimut " + Math.toDegrees(orientation[0]) +
                "\nPitch " + Math.toDegrees(orientation[1]) +
                "\nRoll " + Math.toDegrees(orientation[2]);
        sensorTV.setText(text);
    }

    private void insertSensor(String type, float[] data, long time) {
        ContentValues values = new ContentValues();
        values.put(SensorColumns.COLUMN_NAME_X, data[0]);
        values.put(SensorColumns.COLUMN_NAME_Y, data[1]);
        values.put(SensorColumns.COLUMN_NAME_Z, data[2]);
        values.put(SensorColumns.COLUMN_NAME_TIME, time - startTime);
        values.put(SensorColumns.COLUMN_TYPE, type);
        db.insert(SensorColumns.TABLE_NAME, null, values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        checkStartTime();
        long time = SystemClock.elapsedRealtimeNanos() - startTime;
        int type = sensor.getType();
        ContentValues values = new ContentValues();
        values.put(AccuracyColumns.COLUMN_NAME_ACCURACY, accuracy);
        values.put(AccuracyColumns.COLUMN_NAME_SENSOR_TYPE, type);
        values.put(AccuracyColumns.COLUMN_NAME_TIME, time);
        db.insert(AccuracyColumns.TABLE_NAME, null, values);
    }

    @Override
    public void onLocationChanged(Location location) {
        checkStartTime();
        long time = location.getElapsedRealtimeNanos() - startTime;
        ContentValues values = new ContentValues();
        values.put(LocationColumns.accuracy, location.getAccuracy());
        values.put(LocationColumns.altitude, location.getAltitude());
        values.put(LocationColumns.elapsedTime, location.getElapsedRealtimeNanos());
        values.put(LocationColumns.latitude, location.getLatitude());
        values.put(LocationColumns.longitutde, location.getLongitude());
        values.put(LocationColumns.provider, location.getProvider());
        values.put(LocationColumns.speed, location.getSpeed());
        values.put(LocationColumns.time, time);
        db.insert(LocationColumns.TABLE_NAME, null, values);
        String text = "GPS speed " + location.getSpeed();
        gpsTV.setText(text);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}




