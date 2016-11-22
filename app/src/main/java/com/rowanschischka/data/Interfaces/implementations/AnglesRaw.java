package com.rowanschischka.data.Interfaces.implementations;

import com.rowanschischka.data.DataRow;
import com.rowanschischka.data.Interfaces.DataFunction;
import com.rowanschischka.data.SensorMath;

/**
 * calculates angles in degrees from raw rotation type
 */

public class AnglesRaw implements DataFunction {
    boolean valid = true;
    float[] mRotationMatrixFromVector = new float[9];
    float[] mRotationMatrix = new float[9];
    float[] orientationVals = new float[3];

    @Override
    public String getType() {
        return "ANGLES_RAW";
    }

    @Override
    public String getFileName() {
        return getType();
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

            DataRow result = new DataRow(getType(), dataRow.getTimeStamp(), orientationVals, dataRow.getUpTime());
            return result;
        }
        return null;
    }
}
