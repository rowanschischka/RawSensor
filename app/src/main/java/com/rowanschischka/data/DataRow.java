package com.rowanschischka.data;

/**
 * Created by rowanschischka on 28/09/16.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;

/**
 * @author rowanschischka
 */
public class DataRow implements Comparable {
    //region static values
    public static final String TYPE_ACCELEROMETER_RAW = "ACCELEROMETER_RAW";
    public static final String TYPE_GRAVITY_RAW = "TYPE_GRAVITY_RAW";
    public static final String TYPE_LINEAR_ACCELERATION = "TYPE_LINEAR_ACCELERATION";
    public static final String TYPE_MAGNETOMETER_RAW = "MAGNETOMETER_RAW";
    public static final String TYPE_GPS = "GPS_RAW";
    public static final String TYPE_ROTATION_RAW = "ROTATION_RAW";
    public static final int SENSOR_RATE = SensorManager.SENSOR_DELAY_GAME;
    public static final int NUM_COLUMNS = 7;
    private static final int COLUMN_TYPE = 0;
    private static final int COLUMN_TIME = 1;
    private static final int COLUMN_X = 2;
    private static final int COLUMN_Y = 3;
    private static final int COLUMN_Z = 4;
    private static final int COLUMN_ACCURACY = 5;
    private static final int COLUMN_GPS_SPEED = 6;
    private static final int COLUMN_GPS_ALT = COLUMN_X;
    private static final int COLUMN_GPS_LAT = COLUMN_Y;
    private static final int COLUMN_GPS_LONG = COLUMN_Z;
    //endregion
    //region DataRow data variables
    private String type;
    private long time;
    private float[] values = new float[NUM_COLUMNS];
    //endregion

    public DataRow(String input) {
        String[] data = input.split(",");
        setTime(data[COLUMN_TIME]);
        setType(data[COLUMN_TYPE]);
        this.values[COLUMN_X] = Float.parseFloat(data[COLUMN_X]);
        this.values[COLUMN_Y] = Float.parseFloat(data[COLUMN_Y]);
        this.values[COLUMN_Z] = Float.parseFloat(data[COLUMN_Z]);
        if (data.length > COLUMN_ACCURACY)
            this.values[COLUMN_ACCURACY] = Float.parseFloat(data[COLUMN_ACCURACY]);
        if (data.length > COLUMN_GPS_SPEED)
            this.values[COLUMN_GPS_SPEED] = Float.parseFloat(data[COLUMN_GPS_SPEED]);
    }

    /**
     * for entering new data
     *
     * @param time
     * @param vector
     */
    public DataRow(String type, long time, float[] vector) {
        this.time = time;
        this.type = type;
        this.values[COLUMN_X] = vector[0];
        this.values[COLUMN_Y] = vector[1];
        this.values[COLUMN_Z] = vector[2];
    }

    public static String getTableHeader() {
        String[] headers = new String[NUM_COLUMNS];
        headers[COLUMN_TYPE] = "TYPE";
        headers[COLUMN_TIME] = "TIME";
        headers[COLUMN_X] = "X";
        headers[COLUMN_Y] = "Y";
        headers[COLUMN_Z] = "Z";
        headers[COLUMN_ACCURACY] = "ACCURACY";
        headers[COLUMN_GPS_SPEED] = "SPEED";
        return getCsvRow(headers);
    }

    public static String getCsvRow(String[] values) {
        String result = "";
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                result += values[i];
                if (i < values.length - 1) {
                    result += ",";
                }
            }
        }
        return result;
    }

    public static String sensorTypeToString(int sensorType) {
        if (sensorType == Sensor.TYPE_ACCELEROMETER)
            return TYPE_ACCELEROMETER_RAW;
        else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD)
            return TYPE_MAGNETOMETER_RAW;
        else if (sensorType == Sensor.TYPE_GRAVITY)
            return TYPE_GRAVITY_RAW;
        else if (sensorType == Sensor.TYPE_ROTATION_VECTOR)
            return TYPE_ROTATION_RAW;
        return null;
    }

    public static String eventToString(SensorEvent event, long elapsedTime) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD || event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                || event.sensor.getType() == Sensor.TYPE_GRAVITY || event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            String[] values;
            values = new String[NUM_COLUMNS];
            values[COLUMN_TYPE] = sensorTypeToString(event.sensor.getType());
            values[COLUMN_TIME] = String.valueOf(elapsedTime);
            values[COLUMN_X] = String.valueOf(event.values[0]);
            values[COLUMN_Y] = String.valueOf(event.values[1]);
            values[COLUMN_Z] = String.valueOf(event.values[2]);
            values[COLUMN_ACCURACY] = String.valueOf(event.accuracy);
            values[COLUMN_GPS_SPEED] = "";
            String result = getCsvRow(values);
            return result;
        }
        return null;
    }

    public static String locationToString(Location location, long elapsedTime) {
        String[] values;
        values = new String[NUM_COLUMNS];
        values[COLUMN_TYPE] = TYPE_GPS;
        values[COLUMN_TIME] = String.valueOf(elapsedTime);
        values[COLUMN_GPS_ALT] = String.valueOf(location.getAltitude());
        values[COLUMN_GPS_LAT] = String.valueOf(location.getLatitude());
        values[COLUMN_GPS_LONG] = String.valueOf(location.getLongitude());
        values[COLUMN_GPS_SPEED] = String.valueOf(location.getSpeed());
        values[COLUMN_ACCURACY] = String.valueOf(location.getAccuracy());
        String result = getCsvRow(values);
        return result;
    }

    public float[] getXYZ() {
        return new float[]{values[COLUMN_X], values[COLUMN_Y], values[COLUMN_Z]};
    }

    public String getCsvRow() {
        String result = this.type + "," + this.time + ",";
        if (values != null) {
            for (int i = 2; i < values.length; i++) {
                result += String.valueOf(values[i]);
                if (i < values.length - 1) {
                    result += ",";
                }
            }
        }
        return result;
    }

    public float[] getVector() {
        float[] vector = new float[3];
        vector[0] = this.getX();
        vector[1] = this.getY();
        vector[2] = this.getZ();
        return vector;
    }

    @Override
    public String toString() {
        return getCsvRow();
    }

    //region get & set methods
    public float getGpsSpeed() {
        return values[COLUMN_GPS_SPEED];
    }

    public float getGpsAltitude() {
        return values[COLUMN_GPS_ALT];
    }

    public float getGpsLat() {
        return values[COLUMN_GPS_LAT];
    }

    public float getGpsLong() {
        return values[COLUMN_GPS_LONG];
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(String time_) {
        this.time = Long.parseLong(time_);
    }

    public float getX() {
        return values[COLUMN_X];
    }

    public float getY() {
        return values[COLUMN_Y];
    }

    public float getZ() {
        return values[COLUMN_Z];
    }

    @Override
    public int compareTo(Object o) {
        DataRow otherDataRow = (DataRow) o;
        long diff = this.time - otherDataRow.getTime();
        if (diff < 0) {
            return -1;
        }
        if (diff > 0) {
            return 1;
        }
        return 0;
    }
}

