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
    public static final String TYPE_MAGNETOMETER_RAW = "MAGNETOMETER_RAW";
    public static final String TYPE_GPS = "GPS_RAW";
    public static final String TYPE_ROTATION_RAW = "ROTATION_RAW";
    public static final int SENSOR_RATE = SensorManager.SENSOR_DELAY_GAME;
    public static final int NUM_COLUMNS = 8;
    public static final int COLUMN_TYPE = 0;
    public static final int COLUMN_TIMESTAMP = 1;
    public static final int COLUMN_X = 2;
    public static final int COLUMN_Y = 3;
    public static final int COLUMN_Z = 4;
    public static final int COLUMN_ACCURACY = 5;
    public static final int COLUMN_GPS_SPEED = 6;
    public static final int COLUMN_UPTIME = 7;
    public static final int COLUMN_GPS_ALT = COLUMN_X;
    public static final int COLUMN_GPS_LAT = COLUMN_Y;
    public static final int COLUMN_GPS_LONG = COLUMN_Z;
    //endregion
    //region DataRow data variables
    private String type;
    private long timeStamp;
    private long upTime;
    private float[] values = new float[NUM_COLUMNS];
    //endregion

    public DataRow(String input) {
        String[] data = input.split(",");
        setTimeStamp(data[COLUMN_TIMESTAMP]);
        setType(data[COLUMN_TYPE]);
        setUpTime(data[COLUMN_UPTIME]);
        if (isNumber(data[COLUMN_X]))
            this.values[COLUMN_X] = Float.parseFloat(data[COLUMN_X]);
        if (isNumber(data[COLUMN_Y]))
            this.values[COLUMN_Y] = Float.parseFloat(data[COLUMN_Y]);
        if (isNumber(data[COLUMN_Z]))
            this.values[COLUMN_Z] = Float.parseFloat(data[COLUMN_Z]);
        if (isNumber(data[COLUMN_ACCURACY]))
            this.values[COLUMN_ACCURACY] = Float.parseFloat(data[COLUMN_ACCURACY]);
        if (isNumber(data[COLUMN_GPS_SPEED]))
            this.values[COLUMN_GPS_SPEED] = Float.parseFloat(data[COLUMN_GPS_SPEED]);
        if (isNumber(data[COLUMN_UPTIME]))
            this.values[COLUMN_UPTIME] = Float.parseFloat(data[COLUMN_UPTIME]);
    }

    /**
     * for entering new data
     *
     * @param timeStamp
     * @param vector
     */
    public DataRow(String type, long timeStamp, float[] vector, long upTime) {
        this.timeStamp = timeStamp;
        this.type = type;
        this.values[COLUMN_X] = vector[0];
        this.values[COLUMN_Y] = vector[1];
        this.values[COLUMN_Z] = vector[2];
        this.upTime = upTime;
    }

    public static String getTableHeader() {
        String[] headers = new String[NUM_COLUMNS];
        headers[COLUMN_TYPE] = "TYPE";
        headers[COLUMN_TIMESTAMP] = "TIME";
        headers[COLUMN_X] = "X";
        headers[COLUMN_Y] = "Y";
        headers[COLUMN_Z] = "Z";
        headers[COLUMN_ACCURACY] = "ACCURACY";
        headers[COLUMN_GPS_SPEED] = "SPEED";
        headers[COLUMN_UPTIME] = "UPTIME";
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

    public static String eventToString(SensorEvent event, long elapsedTime, long upTime) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD || event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                || event.sensor.getType() == Sensor.TYPE_GRAVITY || event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            String[] values;
            values = new String[NUM_COLUMNS];
            values[COLUMN_TYPE] = sensorTypeToString(event.sensor.getType());
            values[COLUMN_TIMESTAMP] = String.valueOf(elapsedTime);
            values[COLUMN_X] = String.valueOf(event.values[0]);
            values[COLUMN_Y] = String.valueOf(event.values[1]);
            values[COLUMN_Z] = String.valueOf(event.values[2]);
            values[COLUMN_ACCURACY] = String.valueOf(event.accuracy);
            values[COLUMN_UPTIME] = String.valueOf(upTime);
            values[COLUMN_GPS_SPEED] = "X";
            String result = getCsvRow(values);
            return result;
        }
        return null;
    }

    public static String locationToString(Location location, long elapsedTime, long upTime) {
        String[] values;
        values = new String[NUM_COLUMNS];
        values[COLUMN_TYPE] = TYPE_GPS;
        values[COLUMN_TIMESTAMP] = String.valueOf(elapsedTime);
        values[COLUMN_GPS_ALT] = String.valueOf(location.getAltitude());
        values[COLUMN_GPS_LAT] = String.valueOf(location.getLatitude());
        values[COLUMN_GPS_LONG] = String.valueOf(location.getLongitude());
        values[COLUMN_GPS_SPEED] = String.valueOf(location.getSpeed());
        values[COLUMN_ACCURACY] = String.valueOf(location.getAccuracy());
        values[COLUMN_UPTIME] = String.valueOf(upTime);
        String result = getCsvRow(values);
        return result;
    }

    public boolean isNumber(String n) {
        try {
            Float.parseFloat(n);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public float[] getXYZ() {
        return new float[]{values[COLUMN_X], values[COLUMN_Y], values[COLUMN_Z]};
    }

    public String getCsvRow() {
        String result = "";
        String[] values;
        values = new String[NUM_COLUMNS];
        values[COLUMN_TYPE] = this.type;
        values[COLUMN_TIMESTAMP] = String.valueOf(this.timeStamp);
        values[COLUMN_X] = String.valueOf(this.values[COLUMN_X]);
        values[COLUMN_Y] = String.valueOf(this.values[COLUMN_Y]);
        values[COLUMN_Z] = String.valueOf(this.values[COLUMN_Z]);
        values[COLUMN_ACCURACY] = String.valueOf(this.values[COLUMN_ACCURACY]);
        values[COLUMN_UPTIME] = String.valueOf(upTime);
        values[COLUMN_GPS_SPEED] = String.valueOf(this.values[COLUMN_GPS_SPEED]);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = Long.parseLong(timeStamp);
    }

    public float getY() {
        return values[COLUMN_Y];
    }

    @Override
    public int compareTo(Object o) {
        DataRow otherDataRow = (DataRow) o;
        long diff = this.upTime - otherDataRow.timeStamp;
        if (diff < 0) {
            return -1;
        }
        if (diff > 0) {
            return 1;
        }
        return 0;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = Long.parseLong(upTime);
    }
}

