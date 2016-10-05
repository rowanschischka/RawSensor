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
    public static final String TYPE_ACCELEROMETER_LOWPASS_FILTERED = "ACCELEROMETER_LOWPASS_FILTERED";
    public static final String TYPE_GRAVITY = "TYPE_GRAVITY";
    public static final String TYPE_LINEAR_ACCELERATION = "TYPE_LINEAR_ACCELERATION";
    public static final int MAX_COLUMNS_ACCELEROMTER = 5;
    public static final int MAX_COLUMNS_MAGNETOMETER = 5;
    public static final int MAX_COLUMNS_GPS = 7;
    public static final String TYPE_MAGNETOMETER_RAW = "MAGNETOMETER_RAW";
    public static final String TYPE_MAGNETOMETER_HIGHPASS = "TYPE_MAGNETOMETER_HIGHPASS";
    public static final String TYPE_MAGNETOMETER_LOWPASS = "TYPE_MAGNETOMETER_LOWPASS";
    public static final String TYPE_ACCELEROMETER_VECTOR_LENGTH = "ACCELEROMETER_VECTOR_LENGTH";
    public static final String TYPE_GPS = "GPS_RAW";
    public static final String TYPE_ROTATION = "ROTATION";
    public static final int SENSOR_RATE = SensorManager.SENSOR_DELAY_GAME;
    private static final int COLUMN_TYPE = 0;
    private static final int COLUMN_TIME = 1;
    private static final int COLUMN_X = 2;
    private static final int COLUMN_Y = 3;
    private static final int COLUMN_Z = 4;
    private static final int COLUMN_GPS_SPEED = 2;
    private static final int COLUMN_GPS_ALT = 3;
    private static final int COLUMN_GPS_LAT = 4;
    private static final int COLUMN_GPS_LONG = 5;
    private static final int COLUMN_PLAY_ALT = 6;
    private static final int COLUMN_ACCURACY = 2;
    private static final float COLUMN_VECTOR_LENGTH = 2;
    //endregion
    //region DataRow data variables
    //TODO: decrease the crazy number of these
    private String type;
    private long time;
    private float x = -1;
    private float y = -1;
    private float vectorlength = -1;
    private float z = -1;
    private float gpsSpeed = -1;
    private float gpsAltitude = -1;
    private float gpsLat = -1;
    private float gpsLong = -1;
    private float playAltitude = -1;
    private float accuracy = -1;
    //endregion

    public DataRow(String input) {
        String[] data = input.split(",");
        if (data.length < 3) {
            System.err.println("error in data");
            System.exit(-1);
        }
        setTime(data[COLUMN_TIME]);
        setType(data[COLUMN_TYPE]);
        switch (data[COLUMN_TYPE]) {
            case TYPE_ACCELEROMETER_RAW:
                setX(data[COLUMN_X]);
                setY(data[COLUMN_Y]);
                setZ(data[COLUMN_Z]);
                break;
            case TYPE_MAGNETOMETER_RAW:
                setX(data[COLUMN_X]);
                setY(data[COLUMN_Y]);
                setZ(data[COLUMN_Z]);
                break;
            case TYPE_GPS:
                setGpsAltitude(data[COLUMN_GPS_ALT]);
                setGpsLat(data[COLUMN_GPS_LAT]);
                setGpsLong(data[COLUMN_GPS_LONG]);
                setGpsSpeed(data[COLUMN_GPS_SPEED]);
                setPlayAltitude(data[COLUMN_PLAY_ALT]);
                break;
            case TYPE_ACCELEROMETER_VECTOR_LENGTH:
                //TODO: add vector length type
                //TODO: change all the variables above to be just column1,column2, etc
        }
    }

    /**
     * for entering rotation data
     *
     * @param time_
     * @param vector
     */
    public DataRow(String type, long time_, float[] vector) {
        this.time = time_;
        this.type = type;
        this.x = vector[0];
        this.y = vector[1];
        this.z = vector[2];
    }

    /*
        public DataRow(long time_) {
            this.time = time_;
        }
    */
    public static String getTableHeader() {
        //GPS has more columns
        String[] headers = new String[MAX_COLUMNS_GPS];
        headers[COLUMN_TYPE] = "TYPE";
        headers[COLUMN_TIME] = "TIME";
        for (int i = 2; i < headers.length; i++) {
            headers[i] = "c" + i;
        }
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
        return null;
    }

    public static String eventToString(SensorEvent event, long elapsedTime) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD || event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            String[] values;
            values = new String[MAX_COLUMNS_ACCELEROMTER];
            values[COLUMN_TYPE] = sensorTypeToString(event.sensor.getType());
            values[COLUMN_TIME] = String.valueOf(elapsedTime);
            values[COLUMN_X] = String.valueOf(event.values[0]);
            values[COLUMN_Y] = String.valueOf(event.values[1]);
            values[COLUMN_Z] = String.valueOf(event.values[2]);
            String result = getCsvRow(values);
            return result;
        }
        return null;
    }

    public static String locationToString(Location location, long elapsedTime) {
        String[] values;
        values = new String[MAX_COLUMNS_GPS];
        values[COLUMN_TYPE] = TYPE_GPS;
        values[COLUMN_TIME] = String.valueOf(elapsedTime);
        //values[COLUMN_PLAY_ALT] = String.valueOf(playAltitude);
        values[COLUMN_GPS_ALT] = String.valueOf(location.getAltitude());
        values[COLUMN_GPS_LAT] = String.valueOf(location.getLatitude());
        values[COLUMN_GPS_LONG] = String.valueOf(location.getLongitude());
        values[COLUMN_GPS_SPEED] = String.valueOf(location.getSpeed());
        String result = getCsvRow(values);
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
        String[] values = null;
        if (type.equals(TYPE_GPS)) {
            values = new String[MAX_COLUMNS_GPS];
            values[COLUMN_TYPE] = type;
            values[COLUMN_TIME] = String.valueOf(getTime());
            values[COLUMN_PLAY_ALT] = String.valueOf(playAltitude);
            values[COLUMN_GPS_ALT] = String.valueOf(gpsAltitude);
            values[COLUMN_GPS_LAT] = String.valueOf(gpsLat);
            values[COLUMN_GPS_LONG] = String.valueOf(gpsLong);
        } else {
            values = new String[MAX_COLUMNS_ACCELEROMTER];
            values[COLUMN_TYPE] = type;
            values[COLUMN_TIME] = String.valueOf(getTime());
            values[COLUMN_X] = String.valueOf(x);
            values[COLUMN_Y] = String.valueOf(y);
            values[COLUMN_Z] = String.valueOf(z);
        }
        String result = getCsvRow(values);
        return result;
    }

    //region get & set methods
    public float getGpsSpeed() {
        return gpsSpeed;
    }

    public void setGpsSpeed(String gpsSpeed) {
        if (!gpsSpeed.equals("null")) {
            this.gpsSpeed = Float.parseFloat(gpsSpeed);
        }
    }

    public float getGpsAltitude() {
        return gpsAltitude;
    }

    public void setGpsAltitude(String gpsAltitude) {
        if (!gpsAltitude.equals("null")) {
            this.gpsAltitude = Float.parseFloat(gpsAltitude);
        }
    }

    public float getGpsLat() {
        return gpsLat;
    }

    public void setGpsLat(String gpsLat) {
        if (!gpsLat.equals("null")) {

            this.gpsLat = Float.parseFloat(gpsLat);
        }
    }

    public float getGpsLong() {
        return gpsLong;
    }

    public void setGpsLong(String gpsLong) {
        if (!gpsLong.equals("null")) {

            this.gpsLong = Float.parseFloat(gpsLong);
        }
    }

    public float getPlayAltitude() {
        return playAltitude;
    }

    public void setPlayAltitude(String playAltitude) {
        if (!playAltitude.equals("null")) {
            this.playAltitude = Float.parseFloat(playAltitude);
        }
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

    public void setX(String x_) {
        this.x = Float.parseFloat(x_);
    }

    public float getX() {
        return x;
    }

    public void setX(float x_) {
        this.x = x_;
    }

    public float getY() {
        return y;
    }

    public void setY(float y_) {
        this.y = y_;
    }

    public void setY(String y_) {
        this.y = Float.parseFloat(y_);
    }

    public float getZ() {
        return z;
    }

    public void setZ(String z_) {
        this.z = Float.parseFloat(z_);
    }

    public void setZ(float z_) {
        this.z = z_;
    }
    //endregion

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

