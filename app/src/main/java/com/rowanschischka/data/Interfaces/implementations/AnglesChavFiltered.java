package com.rowanschischka.data.Interfaces.implementations;

import com.rowanschischka.data.DataRow;
import com.rowanschischka.data.Interfaces.DataFunction;
import com.rowanschischka.data.SensorMath;

/**
 * calculates angles in degrees from raw rotation type
 */

public class AnglesChavFiltered implements DataFunction {
    boolean valid = true;
    float[] mRotationMatrixFromVector = new float[9];
    float[] mRotationMatrix = new float[9];
    float[] orientationVals = new float[3];
    int windowPos = 0;
    float[][] window;
    boolean windowFull = false;
    float speed = 0;
    float speedLimit;
    float gravMin;
    float gravMax;
    float gravity = 0;

    public AnglesChavFiltered(int windowSize, float speedLimit, float gravMin, float gravMax) {
        window = new float[windowSize][3];
        this.speedLimit = speedLimit;
        this.gravMin = gravMin;
        this.gravMax = gravMax;
    }

    @Override
    public String getType() {
        return "ANGLES_CHAV_WINDOW";
    }

    @Override
    public String getFileName() {
        return "ANGLES_CHAV_WINDOW" + window.length + "_SPEEDLIMIT" + speedLimit + "_GRAVMIN" + gravMin + "_GRAVMAX" + gravMax;
    }

    @Override
    public DataRow processEvent(DataRow dataRow) {
        if (dataRow.getType().equals(DataRow.TYPE_GPS)) {
            speed = dataRow.getGpsSpeed();
        } else if (dataRow.getType().equals(DataRow.TYPE_GRAVITY_RAW)) {
            gravity = dataRow.getY();
        } else if (dataRow.getType().equals(DataRow.TYPE_ROTATION_RAW)) {
            //ignore low speed
            if (speed < speedLimit) {
                window = new float[window.length][3];
                return null;
            }
            if (gravity < gravMin || gravity > gravMax) {
                window = new float[window.length][3];
                return null;
            }
            SensorMath.getRotationMatrixFromVector(mRotationMatrixFromVector, dataRow.getXYZ());
            SensorMath.remapCoordinateSystem(mRotationMatrixFromVector,
                    SensorMath.AXIS_X, SensorMath.AXIS_Z,
                    mRotationMatrix);
            SensorMath.getOrientation(mRotationMatrix, orientationVals);

            orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
            orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
            orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);

            window[windowPos] = orientationVals;
            windowPos++;
            if (windowPos == window.length) windowFull = true;
            if (windowPos >= window.length) windowPos = 0;

            if (windowFull) {
                orientationVals = SensorMath.getChauvenet(orientationVals, window);
                DataRow result = new DataRow(getType(), dataRow.getTime(), orientationVals);
                return result;
            }
        }
        return null;
    }
}
