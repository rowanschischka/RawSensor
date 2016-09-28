package com.rowanschischka.data;

import android.hardware.SensorManager;

/**
 * Created by rowanschischka on 28/09/16.
 */
public class SensorMathFunctions {
    public static float smoothingFilter(float currentValue, float previousValue, float smoothing) {
        currentValue += (previousValue - currentValue) / smoothing;
        return currentValue;
    }

    public static float[] calculateAngleRadians(float[] gravity, float[] magnet) {
        float[] angle = gravityCalculations(gravity, magnet);
        return angle;
    }

    public static float[] calculateAngleDegrees(float[] gravity, float[] magnet) {
        float[] angle = gravityCalculations(gravity, magnet);
        angle[0] = (float) Math.toDegrees(angle[0]);
        angle[1] = (float) Math.toDegrees(angle[1]);
        angle[2] = (float) Math.toDegrees(angle[2]);
        return angle;
    }

    public static float[] gravityCalculations(float[] gravity, float[] magnet) {
        float R[] = new float[9];
        float I[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, gravity, magnet);
        if (success) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);
            return orientation;
        }
        //investigate these failures
        return null;
    }
}
