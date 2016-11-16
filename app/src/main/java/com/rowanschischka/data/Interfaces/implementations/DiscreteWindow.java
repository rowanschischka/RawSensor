package com.rowanschischka.data.Interfaces.implementations;

import com.rowanschischka.data.DataRow;
import com.rowanschischka.data.Interfaces.DataFunction;
import com.rowanschischka.data.SensorMath;

import java.util.ArrayList;

/**
 * Delete this
 */

public class DiscreteWindow implements DataFunction {
    ArrayList<float[]> angleWindow = new ArrayList<>();
    //window size 50 @ 20ms = 1s window
    int windowSize;
    float gravityThreshold = 9.5f;
    boolean valid = true;
    float[] mRotationMatrixFromVector = new float[9];
    float[] mRotationMatrix = new float[9];
    float[] orientationVals = new float[3];

    public DiscreteWindow(int windowSize) {
        this.windowSize = windowSize;
    }

    @Override
    public String getType() {
        return "DISCRETE_ROTATION";
    }

    @Override
    public String getFileName() {
        return "DISCRETE_ROTATION" + windowSize;
    }

    @Override
    public DataRow processEvent(DataRow dataRow) {
        if (dataRow.getType().equals(DataRow.TYPE_ROTATION_RAW)) {
            SensorMath.getRotationMatrixFromVector(mRotationMatrixFromVector, dataRow.getXYZ());
            SensorMath.remapCoordinateSystem(mRotationMatrixFromVector,
                    SensorMath.AXIS_X, SensorMath.AXIS_Z,
                    mRotationMatrix);
            SensorMath.getOrientation(mRotationMatrix, orientationVals);

            orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
            orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
            orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);

            angleWindow.add(orientationVals);
        } else if (dataRow.getType().equals(DataRow.TYPE_GRAVITY_RAW)) {
            float gravity = dataRow.getY() * 100;
            gravity = Math.round(gravity);
            valid = !(gravity >= 960f && gravity <= 980f);
        }
        if (angleWindow.size() >= windowSize || !valid) {
            float[][] range = new float[angleWindow.size()][3];
            angleWindow.toArray(range);
            angleWindow = new ArrayList<>();
            float[] average = SensorMath.getAverage(range);
            DataRow result = new DataRow(getType(), dataRow.getTime(), average);
            return result;
        }
        return null;
    }
}
