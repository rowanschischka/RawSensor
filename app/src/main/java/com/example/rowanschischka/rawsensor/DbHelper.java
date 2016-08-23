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

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DataBaseSchema.TABLE_NAME + " (" +
                DataBaseSchema._ID + " INTEGER PRIMARY KEY," +
                DataBaseSchema.COLUMN_NAME_X + FLOAT_TYPE + COMMA_SEP +
                DataBaseSchema.COLUMN_NAME_Y + FLOAT_TYPE + COMMA_SEP +
                DataBaseSchema.COLUMN_NAME_Z + FLOAT_TYPE + COMMA_SEP +
                DataBaseSchema.COLUMN_NAME_TIME + LONG_TYPE + " )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseSchema.TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dropTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseSchema.TABLE_NAME);
        onCreate(db);
    }
}
