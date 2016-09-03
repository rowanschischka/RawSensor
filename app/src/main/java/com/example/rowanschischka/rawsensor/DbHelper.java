package com.example.rowanschischka.rawsensor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rowanschischka on 15/08/16.
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SensorData.db";
    //private static final String TEXT_TYPE = " TEXT";
    private static final String LONG_TYPE = " LONG";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String COMMA_SEP = ",";
    private static final String INT_TYPE = " INT";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + timeXYZColumns.TABLE_ACCELEROMETER + " (" +
                timeXYZColumns._ID + " INTEGER PRIMARY KEY," +
                timeXYZColumns.COLUMN_NAME_X + FLOAT_TYPE + COMMA_SEP +
                timeXYZColumns.COLUMN_NAME_Y + FLOAT_TYPE + COMMA_SEP +
                timeXYZColumns.COLUMN_NAME_Z + FLOAT_TYPE + COMMA_SEP +
                timeXYZColumns.COLUMN_NAME_TIME + LONG_TYPE + " )");
        db.execSQL("CREATE TABLE " + RotationSensorColumns.TABLE_NAME + " (" +
                RotationSensorColumns._ID + " INTEGER PRIMARY KEY," +
                RotationSensorColumns.COLUMN_NAME_X + FLOAT_TYPE + COMMA_SEP +
                RotationSensorColumns.COLUMN_NAME_Y + FLOAT_TYPE + COMMA_SEP +
                RotationSensorColumns.COLUMN_NAME_Z + FLOAT_TYPE + COMMA_SEP +
                RotationSensorColumns.COLUMN_NAME_TIME + LONG_TYPE + " )");
        db.execSQL("CREATE TABLE " + AccuracyColumns.TABLE_NAME + " (" +
                AccuracyColumns._ID + " INTEGER PRIMARY KEY," +
                AccuracyColumns.COLUMN_NAME_ACCURACY + INT_TYPE + COMMA_SEP +
                AccuracyColumns.COLUMN_NAME_SENSOR_TYPE + INT_TYPE + COMMA_SEP +
                AccuracyColumns.COLUMN_NAME_TIME + LONG_TYPE + " )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + timeXYZColumns.TABLE_ACCELEROMETER);
        db.execSQL("DROP TABLE IF EXISTS " + AccuracyColumns.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RotationSensorColumns.TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + timeXYZColumns.TABLE_ACCELEROMETER);
        db.execSQL("DROP TABLE IF EXISTS " + AccuracyColumns.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RotationSensorColumns.TABLE_NAME);
        onCreate(db);
    }
}
