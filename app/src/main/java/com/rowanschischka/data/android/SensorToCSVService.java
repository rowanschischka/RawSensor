package com.rowanschischka.data.android;

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

import com.rowanschischka.data.DataRow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SensorToCSVService extends Service implements SensorEventListener, LocationListener {
    private static final String TAG = "SensorToCSVService";
    long startTime;
    float[] mRotationMatrixFromVector = new float[9];
    float[] mRotationMatrix = new float[9];
    float[] orientationVals = new float[3];
    //GPS
    private LocationManager locationManager;
    //sensor
    private SensorManager sensorManager;
    private PrintWriter printWriter;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //counters
        startTime = 0;
        //list all sensors to log
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> all = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : all) {
            Log.i(TAG, s.getName());
        }
        //initialize GPS
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String outFilePath = Environment.getExternalStoragePublicDirectory("").getAbsolutePath();
        //File to write to
        Date date = Calendar.getInstance().getTime();
        String filename = date.toString() + ".csv";
        Log.i(TAG, "saving to " + filename);
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
        printWriter.flush();
        printWriter.close();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //sensors
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), DataRow.SENSOR_RATE);//SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), DataRow.SENSOR_RATE);//SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), DataRow.SENSOR_RATE);//SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), DataRow.SENSOR_RATE);//SensorManager.SENSOR_DELAY_FASTEST);
        //GPS
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //Log.i(TAG, "service started");
        printWriter.println(DataRow.getTableHeader());
        return START_STICKY;
    }

    private void checkStartTime() {
        if (startTime == 0) {
            startTime = SystemClock.elapsedRealtimeNanos();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        checkStartTime();
        long elapsedTime = event.timestamp - startTime;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                || event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
                || event.sensor.getType() == Sensor.TYPE_GRAVITY
                || event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            String dataRow = DataRow.eventToString(event, elapsedTime);
            printWriter.println(dataRow);
            //Log.i("TYPE:"event.sensor.getType(), "X:" + event.values[0] + ". Y:" + event.values[1] + ". Z:" + event.values[2]);
        }
        /*
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY_RAW) {
           // Log.i("GRAV","X:"+event.values[0]+". Y:"+event.values[1]+". Z:"+event.values[2]);
        } else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // It is good practice to check that we received the proper sensor event

            // Convert the rotation-vector to a 4x4 matrix.
            SensorManager.getRotationMatrixFromVector(mRotationMatrixFromVector, event.values);
            SensorManager.remapCoordinateSystem(mRotationMatrixFromVector,
                    SensorManager.AXIS_X, SensorManager.AXIS_Z,
                    mRotationMatrix);
            SensorManager.getOrientation(mRotationMatrix, orientationVals);

            // Optionally convert the result from radians to degrees
            orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
            orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
            orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);

        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onLocationChanged(Location location) {
        checkStartTime();
        long elapsedTime = location.getElapsedRealtimeNanos() - startTime;
        String dataRow = DataRow.locationToString(location, elapsedTime);
        //Log.i(TAG, dataRow);
        printWriter.println(dataRow);
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




