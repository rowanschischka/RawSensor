package com.example.rowanschischka.rawsensor;

import android.provider.BaseColumns;

/**
 * Created by rowanschischka on 15/08/16.
 */
public abstract class XYZColumns implements BaseColumns {
    public static final String TABLE_ACCELEROMETER = "accelerometerData";
    public static final String TABLE_ROTATION_RAW = "rotationRawData";
    public static final String TABLE_ROTATION_ADJUSTED = "rotationAdjusteedData";
    public static final String COLUMN_NAME_X = "x";
    public static final String COLUMN_NAME_Y = "y";
    public static final String COLUMN_NAME_Z = "z";
    public static final String COLUMN_NAME_TIME = "time";
}
