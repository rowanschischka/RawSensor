package com.rowanschischka.rawsensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SensorToCSVService extends Service implements SensorEventListener, LocationListener {
    private static final String LOG = "SENSORACTIVITY";
    private static final String SEPARATOR = ",";
    long startTime;
    //GPS
    private LocationManager locationManager;
    //sensor
    private SensorManager sensorManager;
    //counters
    private int magCounter;
    private int accCounter;
    private int locationCounter;
    private PrintWriter printWriter;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //counters
        startTime = 0;
        magCounter = 0;
        accCounter = 0;
        locationCounter = 0;
        //list all sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> all = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : all) {
            Log.i(LOG, s.getName());
        }
        //initialize GPS
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String outFilePath = Environment.getExternalStoragePublicDirectory("").getAbsolutePath();
        //File to write to
        Date date = Calendar.getInstance().getTime();
        String filename = date.toString() + ".csv";
        Log.i(LOG, "saving to " + filename);
        File outFile = new File(outFilePath, filename);
        try {
            outFile.createNewFile();
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outFile.getAbsoluteFile())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        long time = SystemClock.elapsedRealtimeNanos() - startTime;
        String text = "";
        if (locationCounter > 0)
            text += "GPS intervals = " + (time / (float) locationCounter / 1000000F);
        if (accCounter > 0)
            text += "\naccelerometer intervals = " + (time / (float) accCounter / 1000000F);
        if (magCounter > 0)
            text += "\nmagnetometer intervals = " + (time / (float) magCounter / 1000000F);
        Log.i(LOG, text);
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        printWriter.flush();
        printWriter.close();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //sensors
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);//SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);//SensorManager.SENSOR_DELAY_FASTEST);
        //GPS
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Log.i(LOG, "service started");
        printWriter.println("TYPE" + SEPARATOR + "TIME" + SEPARATOR + "0" + SEPARATOR + "1" + SEPARATOR + "2");
        return START_STICKY;
    }

    private void checkStartTime() {
        if (startTime == 0) {
            startTime = SystemClock.elapsedRealtimeNanos();
        }
    }

    private void print(String columnName, long time, float v0, float v1, float v2) {
        printWriter.println(
                columnName + SEPARATOR +
                        time + SEPARATOR +
                        v0 + SEPARATOR +
                        v1 + SEPARATOR +
                        v2);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        checkStartTime();
        long time = event.timestamp - startTime;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                print("ACCELEROMETER", time, event.values[0], event.values[1], event.values[2]);
                accCounter++;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                print("MAGNETOMETER", time, event.values[0], event.values[1], event.values[2]);
                magCounter++;
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onLocationChanged(Location location) {
        checkStartTime();
        long time = location.getElapsedRealtimeNanos() - startTime;
        print("GPS", time, location.getSpeed(), (float) location.getAltitude(), -1);
        locationCounter++;
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



