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
    private final float[] mRotationMatrix = new float[16];
    //GPS
    LocationManager locationManager;
    //data
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    //sensor
    private SensorManager sensorManager = null;
    private Sensor mRotationVectorSensor, mAccelerationSensor, mGeomagneticSensor, mGyroscopeSensor;
    private TextView sensorTV, gpsTV;
    private int restartCounter;
    private int gpsCounter;
    private int accelerometerCounter;
    private int rotationCounter;
    private int magneticFieldCounter;

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
        //initialize sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotationVectorSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);
        mAccelerationSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mGeomagneticSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        mGyroscopeSensor = sensorManager.getDefaultSensor(
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
        sensorManager.registerListener(this, mRotationVectorSensor, 20000);
        sensorManager.registerListener(this, mAccelerationSensor, 20000);
        sensorManager.registerListener(this, mGeomagneticSensor, 20000);
        sensorManager.registerListener(this, mGyroscopeSensor, 20000);
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
                insertXYZ(XYZColumns.TABLE_MAGNETIC, event);
                magneticFieldCounter++;
                break;
            case Sensor.TYPE_ACCELEROMETER:
            insertXYZ(XYZColumns.TABLE_ACCELEROMETER, event);
            accelerometerCounter++;
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
            insertXYZ(XYZColumns.TABLE_ROTATION_VECTOR, event);
            rotationCounter++;
                break;
            default:
                return;
        }
        String text = "SENSORS" +
                "\nTime: " + event.timestamp +
                "\nNumeber of restarts: " + restartCounter +
                "\nAccelerometer entries: " + accelerometerCounter +
                "\nRotation vector entries: " + rotationCounter +
                "\nMagnetic field counter: " + magneticFieldCounter;
        sensorTV.setText(text);
    }

    private void insertXYZ(String tableName, SensorEvent event) {
        ContentValues values = new ContentValues();
        values.put(XYZColumns.COLUMN_NAME_X, event.values[0]);
        values.put(XYZColumns.COLUMN_NAME_Y, event.values[1]);
        values.put(XYZColumns.COLUMN_NAME_Z, event.values[2]);
        values.put(XYZColumns.COLUMN_NAME_TIME, event.timestamp);
        db.insert(tableName, null, values);
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




