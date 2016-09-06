package com.rowanschischka.rawsensor;

import android.provider.BaseColumns;

/**
 * Created by rowanschischka on 15/08/16.
 */
public abstract class AccuracyColumns implements BaseColumns {
    public static final String TABLE_NAME = "accuracyData";
    public static final String COLUMN_NAME_ACCURACY = "accuracy";
    public static final String COLUMN_NAME_SENSOR_TYPE = "sensor_type";
    public static final String COLUMN_NAME_TIME = "time";
}
