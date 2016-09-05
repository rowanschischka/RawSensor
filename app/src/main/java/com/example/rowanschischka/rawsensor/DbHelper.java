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
    private static final String TEXT_TYPE = " TEXT";
    private static final String LONG_TYPE = " LONG";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ",";
    private static final String INT_TYPE = " INT";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + XYZColumns.TABLE_ACCELEROMETER + " (" +
                XYZColumns._ID + " INTEGER PRIMARY KEY," +
                XYZColumns.COLUMN_NAME_X + FLOAT_TYPE + COMMA_SEP +
                XYZColumns.COLUMN_NAME_Y + FLOAT_TYPE + COMMA_SEP +
                XYZColumns.COLUMN_NAME_Z + FLOAT_TYPE + COMMA_SEP +
                XYZColumns.COLUMN_NAME_TIME + LONG_TYPE + " )");
        db.execSQL("CREATE TABLE " + XYZColumns.TABLE_ROTATION_VECTOR + " (" +
                XYZColumns._ID + " INTEGER PRIMARY KEY," +
                XYZColumns.COLUMN_NAME_X + FLOAT_TYPE + COMMA_SEP +
                XYZColumns.COLUMN_NAME_Y + FLOAT_TYPE + COMMA_SEP +
                XYZColumns.COLUMN_NAME_Z + FLOAT_TYPE + COMMA_SEP +
                XYZColumns.COLUMN_NAME_TIME + LONG_TYPE + " )");
        db.execSQL("CREATE TABLE " + XYZColumns.TABLE_MAGNETIC + " (" +
                XYZColumns._ID + " INTEGER PRIMARY KEY," +
                XYZColumns.COLUMN_NAME_X + FLOAT_TYPE + COMMA_SEP +
                XYZColumns.COLUMN_NAME_Y + FLOAT_TYPE + COMMA_SEP +
                XYZColumns.COLUMN_NAME_Z + FLOAT_TYPE + COMMA_SEP +
                XYZColumns.COLUMN_NAME_TIME + LONG_TYPE + " )");
        db.execSQL("CREATE TABLE " + AccuracyColumns.TABLE_NAME + " (" +
                AccuracyColumns._ID + " INTEGER PRIMARY KEY," +
                AccuracyColumns.COLUMN_NAME_ACCURACY + INT_TYPE + COMMA_SEP +
                AccuracyColumns.COLUMN_NAME_SENSOR_TYPE + INT_TYPE + COMMA_SEP +
                AccuracyColumns.COLUMN_NAME_TIME + LONG_TYPE + " )");
        db.execSQL("CREATE TABLE " + LocationColumns.TABLE_NAME + " (" +
                LocationColumns._ID + " INTEGER PRIMARY KEY," +
                LocationColumns.accuracy + FLOAT_TYPE + COMMA_SEP +
                LocationColumns.altitude + DOUBLE_TYPE + COMMA_SEP +
                LocationColumns.elapsedTime + LONG_TYPE + COMMA_SEP +
                LocationColumns.latitude + DOUBLE_TYPE + COMMA_SEP +
                LocationColumns.longitutde + DOUBLE_TYPE + COMMA_SEP +
                LocationColumns.provider + TEXT_TYPE + COMMA_SEP +
                LocationColumns.speed + FLOAT_TYPE + COMMA_SEP +
                LocationColumns.time + LONG_TYPE + " )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + XYZColumns.TABLE_ACCELEROMETER);
        db.execSQL("DROP TABLE IF EXISTS " + AccuracyColumns.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + XYZColumns.TABLE_ROTATION_VECTOR);
        db.execSQL("DROP TABLE IF EXISTS " + XYZColumns.TABLE_MAGNETIC);
        db.execSQL("DROP TABLE IF EXISTS " + LocationColumns.TABLE_NAME);
        onCreate(db);
    }
}
