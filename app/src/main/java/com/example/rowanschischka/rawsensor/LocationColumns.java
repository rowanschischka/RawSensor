package com.example.rowanschischka.rawsensor;

import android.provider.BaseColumns;

/**
 * Created by rowanschischka on 15/08/16.
 */
public abstract class LocationColumns implements BaseColumns {
    public static final String TABLE_NAME = "GPSData";
    public static final String accuracy = "accuracy";
    public static final String altitude = "altitude";
    public static final String elapsedTime = "elapsedTime";
    public static final String latitude = "latitude";
    public static final String longitutde = "longitutde";
    public static final String provider = "provider";
    public static final String speed = "speed";
    public static final String time = "time";
}
