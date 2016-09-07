package com.rowanschischka.rawsensor;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity implements AlertDialog.OnClickListener {
    private static final String TAG = "SENSOR_RECORDER";
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private EditText outFileNameEditText;

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
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.i(TAG, text);
    }


    public void onStartButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, SensorActivity.class);
        startActivity(intent);
    }

    public void onDestroyClicked(View view) {
        dbHelper.dropTable(db);
        terminal("database data deleted");
    }

    public void onExportClicked(View view) {
        //get filename from user
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Database Export");
        alertBuilder.setMessage("Enter file name");
        alertBuilder.setPositiveButton(R.string.save, this);
        outFileNameEditText = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        outFileNameEditText.setLayoutParams(lp);
        alertBuilder.setView(outFileNameEditText);
        alertBuilder.create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            // int which = -2
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                // int which = -3
                dialog.dismiss();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                // int which = -1
                //file name to write to
                String outFileName = outFileNameEditText.getText().toString();
                //default to internal storage
                new DataExporterAsync().execute(outFileName);
                dialog.dismiss();
                break;
        }
    }

    private class DataExporterAsync extends AsyncTask<String, String, String> {
        @Override
        public String doInBackground(String... outFilenames) {
            String accuracySQL = "SELECT * FROM " + AccuracyColumns.TABLE_NAME;
            String result = dataToCSV(accuracySQL, outFilenames[0] + "_sensor_accuracy.csv");
            publishProgress("accuracy table " + result);
            String locationSQL = "SELECT * FROM " + LocationColumns.TABLE_NAME;
            result = dataToCSV(locationSQL, outFilenames[0] + "_location_data.csv");
            publishProgress("location table " + result);
            String sensorSQL = "SELECT * FROM " + SensorColumns.TABLE_NAME;
            result = dataToCSV(sensorSQL, outFilenames[0] + "_sensor_data.csv");
            publishProgress("sensor table " + result);

            return outFilenames[0];
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            terminal(progress[0]);
        }

        @Override
        protected void onPostExecute(String outFileName) {
        }

        private String dataToCSV(String sqlQuery, String outFileName) {
            //default to internal storage
            String outFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            //File to write to
            File outFile = new File(outFilePath, outFileName);
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                outFile = new File(sd, outFileName);
            }
            Log.i(TAG, "writing to " + outFile.getAbsolutePath());
            Cursor cursor;
            try {
                cursor = db.rawQuery(sqlQuery, null);
            } catch (SQLiteException e) {
                return "SQL query failed";
            }
            if (cursor.getCount() <= 0) {
                return "Table empty";
            }
            cursor.moveToFirst();
            try {
                outFile.createNewFile();
                PrintWriter out
                        = new PrintWriter(new BufferedWriter(new FileWriter(outFile.getAbsoluteFile())));
                String[] columnNames = cursor.getColumnNames();
                for (int i = 0; i < columnNames.length; i++) {
                    if (i < columnNames.length - 1) {
                        out.write(columnNames[i] + ",");
                    } else {
                        out.write(columnNames[i]);
                    }
                }
                do {
                    out.write("\n");
                    for (int i = 0; i < columnNames.length; i++) {
                        if (i < columnNames.length - 1) {
                            out.write(cursor.getString(i) + ",");
                        } else {
                            out.write(cursor.getString(i));
                        }
                    }
                } while (cursor.moveToNext());
                Log.e(TAG, "file write failed 2");
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "file write failed");
                return "file write failed";
            }
            cursor.close();
            return "file writen to " + outFile.getAbsolutePath();
        }
    }

}