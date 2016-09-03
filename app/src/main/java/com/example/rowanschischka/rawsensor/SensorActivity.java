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
import android.widget.Toast;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = SensorActivity.class.getSimpleName();
    //data
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    //sensor
    private SensorManager mSensorManager = null;
    private Sensor mRotationVectorSensor, mAccelerationSensor;
    private final float[] mRotationMatrix = new float[16];
    private volatile float[] mAccelerometerMatrix = new float[4];
    private long startTime = -1L;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        //sensor
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
        tv = (TextView) findViewById(R.id.text_recording);
    }

    protected void onPause() {
        mSensorManager.unregisterListener(this);
        Toast.makeText(this, "Sensor service done", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    protected void onResume() {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
        mSensorManager.registerListener(this, mAccelerationSensor, 5000);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    float[] geomagnetic = new float[3];
    boolean havemag = false;
    boolean haveacc = false;
    @Override
    public void onSensorChanged(SensorEvent event) {
        String text = "";
        if (startTime < 0) {
            startTime = event.timestamp;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic[0] = event.values[0];
            geomagnetic[1] = event.values[1];
            geomagnetic[2] = event.values[2];
            havemag = true;
        }
        long time = event.timestamp - startTime;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometerMatrix[0] = event.values[0];
            mAccelerometerMatrix[1] = event.values[1];
            mAccelerometerMatrix[2] = event.values[2];
            mAccelerometerMatrix[3] = 0;
            ContentValues values = new ContentValues();
            values.put(timeXYZColumns.COLUMN_NAME_X, event.values[0]);
            values.put(timeXYZColumns.COLUMN_NAME_Y, event.values[1]);
            values.put(timeXYZColumns.COLUMN_NAME_Z, event.values[2]);
            values.put(timeXYZColumns.COLUMN_NAME_TIME, time);
            db.insert(timeXYZColumns.TABLE_ACCELEROMETER, null, values);
            Log.d(TAG, "Inserted ACC");
            haveacc = true;
        }
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix.
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
            float[] rotationMatrix = new float[16];
            float[] sensorMatrix = new float[4];
            float[] result = new float[16];
            //SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            //Matrix.invertM(result, 0, rotationMatrix, 0);
            sensorMatrix[0] = 0;
            sensorMatrix[1] = 0;
            sensorMatrix[2] = 0;
            sensorMatrix[3] = 0;
            Matrix.multiplyMV(sensorMatrix, 0, result, 0, mAccelerometerMatrix, 0);
            ContentValues values = new ContentValues();
            values.put(RotationSensorColumns.COLUMN_NAME_X, event.values[0]);
            values.put(RotationSensorColumns.COLUMN_NAME_Y, event.values[1]);
            values.put(RotationSensorColumns.COLUMN_NAME_Z, event.values[2]);
            values.put(RotationSensorColumns.COLUMN_NAME_X, sensorMatrix[0]);
            values.put(RotationSensorColumns.COLUMN_NAME_Y, sensorMatrix[1]);
            values.put(RotationSensorColumns.COLUMN_NAME_Z, sensorMatrix[2]);
            values.put(RotationSensorColumns.COLUMN_NAME_TIME, time);
            db.insert(RotationSensorColumns.TABLE_NAME, null, values);
            Log.d(TAG, "Inserted ROT");
            text +=
                    "X " + event.values[0] + "\n" + sensorMatrix[0] + "\n" +
                            "X " + Math.toDegrees(event.values[0]) + "\n" + Math.toDegrees(sensorMatrix[0]) + "\n" +
                            "Y " + event.values[1] + "\n" + sensorMatrix[1] + "\n" +
                            "Z " + event.values[2] + "\n" + sensorMatrix[2] + "\n";
        }
        if (!text.isEmpty()) tv.setText(text);
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




