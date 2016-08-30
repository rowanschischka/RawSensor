package com.example.rowanschischka.rawsensor;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    private SensorManager mSensorManager = null;

    private Sensor mRotationVectorSensor, mAccelerationSensor;
    private final float[] mRotationMatrix = new float[16];
    private volatile float[] mAccelerometerMatrix = new float[4];

    private long startTime = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        //sensor create
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);
        mAccelerationSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        //initialize the rotation matrix to identity
        mRotationMatrix[0] = 1;
        mRotationMatrix[4] = 1;
        mRotationMatrix[8] = 1;
        mRotationMatrix[12] = 1;
        //database
        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
        //sensor returns nano seconds so need to multiply this by 1,000,000
        //startTime = SystemClock.uptimeMillis()*1000000;
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
        mSensorManager.registerListener(this, mAccelerationSensor, 5000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (startTime < 0) {
            startTime = event.timestamp;
        }
        long time = event.timestamp - startTime;
        //Log.d("Time", startTime + ", " + event.timestamp + ", " + time);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometerMatrix[0] = event.values[0];
            mAccelerometerMatrix[1] = event.values[1];
            mAccelerometerMatrix[2] = event.values[2];
            mAccelerometerMatrix[3] = 0;


            ContentValues values = new ContentValues();
            values.put(AccelerometerColumns.COLUMN_NAME_X, event.values[0]);
            values.put(AccelerometerColumns.COLUMN_NAME_Y, event.values[1]);
            values.put(AccelerometerColumns.COLUMN_NAME_Z, event.values[2]);
            values.put(AccelerometerColumns.COLUMN_NAME_TIME, time);

            db.insert(AccelerometerColumns.TABLE_NAME, null, values);
            //Log.d("db", "Inserted ACC time=" + time);
        }
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix.
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);

            float[] rotationMatrix = new float[16];
            float[] sensorMatrix = new float[4];
            float[] result = new float[16];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            Matrix.invertM(result, 0, rotationMatrix, 0);

            sensorMatrix[0] = 0;
            sensorMatrix[1] = 0;
            sensorMatrix[2] = 0;
            sensorMatrix[3] = 0;

            Matrix.multiplyMV(sensorMatrix, 0, result, 0, mAccelerometerMatrix, 0);

            /*Log.d("values[0]", Float.toString(event.values[0]));
            Log.d("values[1]", Float.toString(event.values[1]));
            Log.d("values[2]", Float.toString(event.values[2]));

            Log.d("sensorMatrix[0]", Float.toString(sensorMatrix[0]));
            Log.d("sensorMatrix[1]", Float.toString(sensorMatrix[1]));
            Log.d("sensorMatrix[2]", Float.toString(sensorMatrix[2]));*/

            ContentValues values = new ContentValues();
            values.put(RotationSensorColumns.COLUMN_NAME_X, event.values[0]);
            values.put(RotationSensorColumns.COLUMN_NAME_Y, event.values[1]);
            values.put(RotationSensorColumns.COLUMN_NAME_Z, event.values[2]);
            values.put(RotationSensorColumns.COLUMN_NAME_TIME, time);

            db.insert(RotationSensorColumns.TABLE_NAME, null, values);
        }
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
    }
}
