package com.example.rowanschischka.rawsensor;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor sensor;
    private LinearLayout linearLayout;
    private int sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();
    }

    private void addTextView(String text) {
        TextView tv = new TextView(MainActivity.this);
        tv.setText(text);
        linearLayout.addView(tv);
    }

    public void onSensorRateClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.SENSOR_DELAY_FASTEST:
                if (checked)
                    sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
                break;
            case R.id.SENSOR_DELAY_GAME:
                if (checked)
                    sensorDelay = SensorManager.SENSOR_DELAY_GAME;
                break;
            case R.id.SENSOR_DELAY_NORMAL:
                if (checked)
                    sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
                break;
            case R.id.SENSOR_DELAY_UI:
                if (checked)
                    sensorDelay = SensorManager.SENSOR_DELAY_UI;
                break;
        }
    }

    public void onStartButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, SensorActivity.class);
        intent.putExtra("SENSOR_DELAY", sensorDelay);
        startActivity(intent);
    }

    public void onDestroyClicked(View view) {
        dbHelper.dropTable(db);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Data Deleted");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void onExportClicked(View view) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + DataBaseSchema.TABLE_NAME, null);
        } catch (SQLiteException e) {
            alertDialog("No Data found");
            return;
        }
        cursor.moveToFirst();
        boolean success = true;

        if (!isExternalStorageWritable()) {
            Log.e("FILE", "external not writable");
        }
        //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());//+"/SensorData"+ SystemClock.currentThreadTimeMillis()+".csv");
        // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "SensorData"+ SystemClock.currentThreadTimeMillis());
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
        Log.d("FILE", file.getAbsolutePath());
        try {

            //file.createNewFile();
            if (!file.mkdirs()) {
                Log.e("FILE", "Directory not created");

            }
            PrintWriter out
                    = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile() + "/SensorData" + SystemClock.currentThreadTimeMillis() + ".csv")));
            // file.createNewFile();
            //fileWriter = new PrintWriter(file.getAbsoluteFile()+"/SensorData"+ SystemClock.currentThreadTimeMillis()+".csv");
            //BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String[] columnNames = cursor.getColumnNames();
            for (String s : columnNames) {
                out.write(s + ",");
                Log.d("NAME", s);
            }
            do {
                out.write("\n");
                for (int i = 0; i < columnNames.length; i++) {
                    out.write(cursor.getString(i) + ",");
                }
            } while (cursor.moveToNext());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("WRITE", e.getMessage());
            success = false;
        }
        cursor.close();
        dbHelper.dropTable(db);
        if (success) alertDialog("Data saved");
    }

    private void alertDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}