package com.rowanschischka.rawsensor;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();
        String text = "" +
                "TYPE_ROTATION_VECTOR = " + Sensor.TYPE_ROTATION_VECTOR +
                "\nTYPE_ACCELEROMETER = " + Sensor.TYPE_ACCELEROMETER +
                "\nTYPE_GYROSCOPE = " + Sensor.TYPE_GYROSCOPE;
        terminal(text);
    }

    private void terminal(String text) {
        TextView tv = (TextView) findViewById(R.id.terminal);
        tv.setText(text + "\n" + tv.getText());
    }


    public void onStartButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, SensorActivity.class);
        startActivity(intent);
    }

    public void onDestroyClicked(View view) {
        dbHelper.dropTable(db);
        Toast.makeText(this, R.string.db_delete, Toast.LENGTH_SHORT).show();
    }

    public void onExportClicked(View view) {
        File data = Environment.getDataDirectory();
        String dbPath = db.getPath();
        terminal("Internal database path: " + dbPath);
        File internalDBFile = new File(dbPath);
        if (internalDBFile.exists()) {
            //get filename from user
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            //AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Database Export");
            alertDialog.setMessage("Enter file name");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            final EditText outFileNameEditText = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            outFileNameEditText.setLayoutParams(lp);
            alertDialog.setView(outFileNameEditText);
            
            alertDialog.show();
            //file name to write to
            String outFileName = outFileNameEditText.getText().toString();
            //default to internal storage
            String outFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            //File to write to
            File outFile = new File(outFilePath, outFileName);
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                outFile = new File(sd, outFileName + ".db");
                terminal("Writing to " + outFile.getAbsolutePath());
            } else {
                terminal("unable to access SD card, writing to " + outFile.getAbsolutePath());
            }
            try {

                FileChannel src = new FileInputStream(internalDBFile).getChannel();
                FileChannel dst = new FileOutputStream(outFile).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                terminal("***Database export complete***");
            } catch (Exception e) {
                terminal("Database export failed");
            }
        } else {
            terminal("Database does not exist");
        }
    }

    public void dataToCSV(View view) {
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * FROM " + SensorColumns.TABLE_NAME, null);
        } catch (SQLiteException e) {
            alertDialog("No Data found");
            return;
        }
        cursor.moveToFirst();
        boolean success = true;
        if (!isExternalStorageWritable()) {
            Log.e("FILE", "external not writable");
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
        Log.d("FILE", file.getAbsolutePath());
        try {
            if (!file.mkdirs()) {
                Log.e("FILE", "Directory not created");
            }
            PrintWriter out
                    = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile() + "/SensorData" + SystemClock.currentThreadTimeMillis() + ".csv")));
            String[] columnNames = cursor.getColumnNames();
            for (String s : columnNames) {
                out.write(s + ",");
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
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}