package com.example.rowanschischka.rawsensor;

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
import android.util.Log;
import android.widget.TextView;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = SensorActivity.class.getSimpleName();
    //GPS
    LocationManager locationManager;
    //UI
    private TextView sensorTV, gpsTV;
    //data
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    //sensor
    private SensorManager sensorManager = null;
    private Sensor rotationVectorSensor, accelerationSensor, geomagneticSensor, gyroSensor;
    private int restartCounter, gpsCounter, accelerometerCounter, rotationCounter, magneticFieldCounter, gyroCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //user interface
        setContentView(R.layout.activity_sensor);
        sensorTV = (TextView) findViewById(R.id.text_recording);
        gpsTV = (TextView) findViewById(R.id.gps_recording);
        //counters
        restartCounter = 0;
        gpsCounter = 0;
        accelerometerCounter = 0;
        rotationCounter = 0;
        magneticFieldCounter = 0;
        gyroCounter = 0;
        //initialize sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);
        accelerationSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        geomagneticSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        gyroSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_GYROSCOPE);
        //initialize database
        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
        //initialize GPS
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                saveLocationChanged(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    protected void onResume() {
        //Toast.makeText(this, "Recording resumed", Toast.LENGTH_SHORT).show();
        sensorManager.registerListener(this, rotationVectorSensor, 20000);
        sensorManager.registerListener(this, accelerationSensor, 20000);
        sensorManager.registerListener(this, geomagneticSensor, 20000);
        sensorManager.registerListener(this, gyroSensor, 20000);
        restartCounter++;
        super.onResume();
    }

    protected void saveLocationChanged(Location location) {
        ContentValues values = new ContentValues();
        values.put(LocationColumns.accuracy, location.getAccuracy());
        values.put(LocationColumns.altitude, location.getAltitude());
        values.put(LocationColumns.elapsedTime, location.getElapsedRealtimeNanos());
        values.put(LocationColumns.latitude, location.getLatitude());
        values.put(LocationColumns.longitutde, location.getLongitude());
        values.put(LocationColumns.provider, location.getProvider());
        values.put(LocationColumns.speed, location.getSpeed());
        values.put(LocationColumns.time, location.getTime());
        db.insert(LocationColumns.TABLE_NAME, null, values);
        String text = "GPS" +
                "\nGPS entries: " + gpsCounter +
                "\naccuracy " + location.getAccuracy() +
                "\naltitude " + location.getAltitude() +
                "\nspeed " + location.getSpeed() +
                "\ntime " + location.getTime();
        gpsTV.setText(text);
        gpsCounter++;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                insertSensor(Sensor.TYPE_MAGNETIC_FIELD, event);
                magneticFieldCounter++;
                break;
            case Sensor.TYPE_ACCELEROMETER:
                insertSensor(Sensor.TYPE_ACCELEROMETER, event);
                accelerometerCounter++;
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                insertSensor(Sensor.TYPE_ROTATION_VECTOR, event);
                rotationCounter++;
                break;
            case Sensor.TYPE_GYROSCOPE:
                insertSensor(Sensor.TYPE_GYROSCOPE, event);
                gyroCounter++;
                break;
            default:
                return;
        }
        String text = "SENSORS" +
                "\nTime: " + event.timestamp +
                "\nNumber of restarts: " + restartCounter +
                "\nAccelerometer [type " + Sensor.TYPE_ACCELEROMETER + "] entries: " + accelerometerCounter +
                "\nRotation vector [type " + Sensor.TYPE_ROTATION_VECTOR + "] entries: " + rotationCounter +
                "\nMagnetic field [type " + Sensor.TYPE_MAGNETIC_FIELD + "] counter: " + magneticFieldCounter +
                "\nGyro [type " + Sensor.TYPE_GYROSCOPE + "] counter: " + gyroCounter;
        sensorTV.setText(text);
    }

    private void insertSensor(int type, SensorEvent event) {
        ContentValues values = new ContentValues();
        values.put(SensorColumns.COLUMN_NAME_X, event.values[0]);
        values.put(SensorColumns.COLUMN_NAME_Y, event.values[1]);
        values.put(SensorColumns.COLUMN_NAME_Z, event.values[2]);
        values.put(SensorColumns.COLUMN_NAME_TIME, event.timestamp);
        values.put(SensorColumns.COLUMN_TYPE, type);
        db.insert(SensorColumns.TABLE_NAME, null, values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        long time = SystemClock.uptimeMillis();
        int type = sensor.getType();
        ContentValues values = new ContentValues();
        values.put(AccuracyColumns.COLUMN_NAME_ACCURACY, accuracy);
        values.put(AccuracyColumns.COLUMN_NAME_SENSOR_TYPE, type);
        values.put(AccuracyColumns.COLUMN_NAME_TIME, time);
        db.insert(AccuracyColumns.TABLE_NAME, null, values);
        Log.d(TAG, "Inserted Accuracy change");
    }
}




