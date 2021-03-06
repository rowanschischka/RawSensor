package com.rowanschischka.data;

import android.hardware.GeomagneticField;

/**
 * Created by rowanschischka on 28/09/16.
 */
public class SensorMath {
    public static final int AXIS_X = 1;
    public static final int AXIS_Y = 2;
    public static final int AXIS_Z = 3;
    private static final String TAG = "SENSOR MATHS";
    private static final float[] mTempMatrix = new float[16];

    public static float smoothingFilter(float currentValue, float previousValue, float smoothing) {
        currentValue += (previousValue - currentValue) / smoothing;
        return currentValue;
    }

    public static float[] getChauvenet(float[] input, float[][] window) {
        float[] average = getAverage(window);
        double stdDev = getStdDeviation(window, average);
        double distance = getDistance(input, average);
        if (distance <= stdDev) {
            return input;
        }
        return average;
    }

    public static double getStdDeviation(float[][] window, float[] average) {
        double stdDev = 0;
        for (float[] d : window) {
            stdDev += Math.pow(getDistance(average, d), 2);
        }
        stdDev /= window.length;
        return stdDev;
    }

    public static float[] smoothingFilter(float x1, float y1, float z1, float x2, float y2, float z2, float smoothing) {
        return new float[]{
                smoothingFilter(x1, x2, smoothing),
                smoothingFilter(y1, y2, smoothing),
                smoothingFilter(z1, z2, smoothing)
        };
    }

    /**
     * presents the high pass and low pass results of a simple filter
     *
     * @param value     current accelerometer sensor event value
     * @param alpha     alpha is calculated as t / (t + dT)
     *                  with t, the low-pass filter's time-constant
     *                  and dT, the event delivery rate
     * @param prevValue previous prevValue value
     * @return result first three are prevValue, last three is linear acceleration
     */
    public static float[] lowHighPassFilter(float[] value, float alpha, float[] prevValue) {
        float[] result = new float[6];
        result[0] = alpha * prevValue[0] + (1 - alpha) * value[0];
        result[1] = alpha * prevValue[1] + (1 - alpha) * value[1];
        result[2] = alpha * prevValue[2] + (1 - alpha) * value[2];
        result[3] = value[0] - result[0];
        result[4] = value[1] - result[1];
        result[5] = value[2] - result[2];
        return result;
    }

    public static float getVectorLength(float[] value) {
        return (float) Math.sqrt(value[0] * value[0] + value[1] * value[1] + value[2] * value[2]);
    }

    public static double getDistance(float[] v1, float[] v2) {
        float x = v1[0] - v2[0];
        float y = v1[1] - v2[1];
        float z = v1[2] - v2[2];
        x *= x;
        y *= y;
        z *= z;
        float dSqaured = x + y + z;
        double distance = Math.sqrt(dSqaured);
        return distance;
    }

    public static float[] getAverage(float[][] range) {
        float[] average = new float[3];
        for (float[] data : range) {
            average[0] += data[0];
            average[1] += data[1];
            average[2] += data[2];
        }
        average[0] /= range.length;
        average[1] /= range.length;
        average[2] /= range.length;
        return average;
    }

    public static float[] getLowPass(float[] filteredResult) {
        return new float[]{
                filteredResult[0], filteredResult[1], filteredResult[2]};

    }

    public static float[] getHighPass(float[] filteredResult) {
        return new float[]{
                filteredResult[3], filteredResult[4], filteredResult[5]};

    }

    public static float[] calculateAngleRadians(float[] gravity, float[] magnet) {
        float[] angle = gravityCalculations(gravity, magnet);
        return angle;
    }

    public static float[] calculateAngleDegrees(float[] gravity, float[] magnet) {
        float[] angle = gravityCalculations(gravity, magnet);
        if (angle == null) return null;
        angle[0] = (float) Math.toDegrees(angle[0]);
        angle[1] = (float) Math.toDegrees(angle[1]);
        angle[2] = (float) Math.toDegrees(angle[2]);
        //System.out.println("X:" + angle[0] + " Y:" + angle[1] + " Z:" + angle[2]);
        return angle;
    }

    public static float[] gravityCalculations(float[] gravity, float[] magnet) {
        float R[] = new float[9];
        float I[] = new float[9];
        //boolean success = SensorManager.getRotationMatrix(R, I, gravity, magnet);
        boolean success = getRotationMatrix(R, I, gravity, magnet);
        if (success) {
            float[] orientation = new float[3];
            getOrientation(R, orientation);
            return orientation;
        }
        return null;
    }

    public static void getRotationMatrixFromVector(float[] R, float[] rotationVector) {

        float q0;
        float q1 = rotationVector[0];
        float q2 = rotationVector[1];
        float q3 = rotationVector[2];

        if (rotationVector.length >= 4) {
            q0 = rotationVector[3];
        } else {
            q0 = 1 - q1 * q1 - q2 * q2 - q3 * q3;
            q0 = (q0 > 0) ? (float) Math.sqrt(q0) : 0;
        }

        float sq_q1 = 2 * q1 * q1;
        float sq_q2 = 2 * q2 * q2;
        float sq_q3 = 2 * q3 * q3;
        float q1_q2 = 2 * q1 * q2;
        float q3_q0 = 2 * q3 * q0;
        float q1_q3 = 2 * q1 * q3;
        float q2_q0 = 2 * q2 * q0;
        float q2_q3 = 2 * q2 * q3;
        float q1_q0 = 2 * q1 * q0;

        if (R.length == 9) {
            R[0] = 1 - sq_q2 - sq_q3;
            R[1] = q1_q2 - q3_q0;
            R[2] = q1_q3 + q2_q0;

            R[3] = q1_q2 + q3_q0;
            R[4] = 1 - sq_q1 - sq_q3;
            R[5] = q2_q3 - q1_q0;

            R[6] = q1_q3 - q2_q0;
            R[7] = q2_q3 + q1_q0;
            R[8] = 1 - sq_q1 - sq_q2;
        } else if (R.length == 16) {
            R[0] = 1 - sq_q2 - sq_q3;
            R[1] = q1_q2 - q3_q0;
            R[2] = q1_q3 + q2_q0;
            R[3] = 0.0f;

            R[4] = q1_q2 + q3_q0;
            R[5] = 1 - sq_q1 - sq_q3;
            R[6] = q2_q3 - q1_q0;
            R[7] = 0.0f;

            R[8] = q1_q3 - q2_q0;
            R[9] = q2_q3 + q1_q0;
            R[10] = 1 - sq_q1 - sq_q2;
            R[11] = 0.0f;

            R[12] = R[13] = R[14] = 0.0f;
            R[15] = 1.0f;
        }
    }

    /**
     * <p>
     * Computes the inclination matrix <b>I</b> as well as the rotation matrix
     * <b>R</b> transforming a vector from the device coordinate system to the
     * world's coordinate system which is defined as a direct orthonormal basis,
     * where:
     * </p>
     * <p/>
     * <ul>
     * <li>X is defined as the vector product <b>Y.Z</b> (It is tangential to
     * the ground at the device's current location and roughly points East).</li>
     * <li>Y is tangential to the ground at the device's current location and
     * points towards the magnetic North Pole.</li>
     * <li>Z points towards the sky and is perpendicular to the ground.</li>
     * </ul>
     * <p/>
     * <p>
     * <center><img src="../../../images/axis_globe.png"
     * alt="World coordinate-system diagram." border="0" /></center>
     * </p>
     * <p/>
     * <p/>
     * <hr>
     * <p/>
     * By definition:
     * <p/>
     * [0 0 g] = <b>R</b> * <b>gravity</b> (g = magnitude of gravity)
     * <p/>
     * [0 m 0] = <b>I</b> * <b>R</b> * <b>geomagnetic</b> (m = magnitude of
     * geomagnetic field)
     * <p/>
     * <b>R</b> is the identity matrix when the device is aligned with the
     * world's coordinate system, that is, when the device's X axis points
     * toward East, the Y axis points to the North Pole and the device is facing
     * the sky.
     * <p/>
     * <p/>
     * <b>I</b> is a rotation matrix transforming the geomagnetic vector into
     * the same coordinate space as gravity (the world's coordinate space).
     * <b>I</b> is a simple rotation around the X axis. The inclination angle in
     * radians can be computed with {@link #//getInclination}.
     * <hr>
     * <p/>
     * <p/>
     * Each matrix is returned either as a 3x3 or 4x4 row-major matrix depending
     * on the length of the passed array:
     * <p/>
     * <u>If the array length is 16:</u>
     * <p/>
     * <pre>
     *   /  M[ 0]   M[ 1]   M[ 2]   M[ 3]  \
     *   |  M[ 4]   M[ 5]   M[ 6]   M[ 7]  |
     *   |  M[ 8]   M[ 9]   M[10]   M[11]  |
     *   \  M[12]   M[13]   M[14]   M[15]  /
     * </pre>
     * <p/>
     * This matrix is ready to be used by OpenGL ES's
     * {@link javax.microedition.khronos.opengles.GL10#glLoadMatrixf(float[], int)
     * glLoadMatrixf(float[], int)}.
     * <p/>
     * Note that because OpenGL matrices are column-major matrices you must
     * transpose the matrix before using it. However, since the matrix is a
     * rotation matrix, its transpose is also its inverse, conveniently, it is
     * often the inverse of the rotation that is needed for rendering; it can
     * therefore be used with OpenGL ES directly.
     * <p/>
     * Also note that the returned matrices always have this form:
     * <p/>
     * <pre>
     *   /  M[ 0]   M[ 1]   M[ 2]   0  \
     *   |  M[ 4]   M[ 5]   M[ 6]   0  |
     *   |  M[ 8]   M[ 9]   M[10]   0  |
     *   \      0       0       0   1  /
     * </pre>
     * <p/>
     * <p/>
     * <u>If the array length is 9:</u>
     * <p/>
     * <pre>
     *   /  M[ 0]   M[ 1]   M[ 2]  \
     *   |  M[ 3]   M[ 4]   M[ 5]  |
     *   \  M[ 6]   M[ 7]   M[ 8]  /
     * </pre>
     * <p/>
     * <hr>
     * <p/>
     * The inverse of each matrix can be computed easily by taking its
     * transpose.
     * <p/>
     * <p/>
     * The matrices returned by this function are meaningful only when the
     * device is not free-falling and it is not close to the magnetic north. If
     * the device is accelerating, or placed into a strong magnetic field, the
     * returned matrices may be inaccurate.
     *
     * @param R           is an array of 9 floats holding the rotation matrix <b>R</b> when
     *                    this function returns. R can be null.
     *                    <p/>
     * @param I           is an array of 9 floats holding the rotation matrix <b>I</b> when
     *                    this function returns. I can be null.
     *                    <p/>
     * @param gravity     is an array of 3 floats containing the gravity vector expressed in
     *                    the device's coordinate. You can simply use the
     *                    {@link android.hardware.SensorEvent#values values} returned by a
     *                    {@link android.hardware.SensorEvent SensorEvent} of a
     *                    {@link android.hardware.Sensor Sensor} of type
     *                    {@link android.hardware.Sensor#TYPE_ACCELEROMETER
     *                    TYPE_ACCELEROMETER_RAW}.
     *                    <p/>
     * @param geomagnetic is an array of 3 floats containing the geomagnetic vector
     *                    expressed in the device's coordinate. You can simply use the
     *                    {@link android.hardware.SensorEvent#values values} returned by a
     *                    {@link android.hardware.SensorEvent SensorEvent} of a
     *                    {@link android.hardware.Sensor Sensor} of type
     *                    {@link android.hardware.Sensor#TYPE_MAGNETIC_FIELD
     *                    TYPE_MAGNETIC_FIELD}.
     * @return <code>true</code> on success, <code>false</code> on failure (for
     * instance, if the device is in free fall). Free fall is defined as
     * condition when the magnitude of the gravity is less than 1/10 of
     * the nominal value. On failure the output matrices are not modified.
     * @see #//getInclination(float[])
     * @see #getOrientation(float[], float[])
     * @see #//remapCoordinateSystem(float[], int, int, float[])
     */
    public static boolean getRotationMatrix(float[] R, float[] I,
                                            float[] gravity, float[] geomagnetic) {
        // TODO: move this to native code for efficiency
        float Ax = gravity[0];
        float Ay = gravity[1];
        float Az = gravity[2];

        final float normsqA = (Ax * Ax + Ay * Ay + Az * Az);
        final float g = 9.81f;
        final float freeFallGravitySquared = 0.01f * g * g;
        if (normsqA < freeFallGravitySquared) {
            // gravity less than 10% of normal value
            return false;
        }

        final float Ex = geomagnetic[0];
        final float Ey = geomagnetic[1];
        final float Ez = geomagnetic[2];
        float Hx = Ey * Az - Ez * Ay;
        float Hy = Ez * Ax - Ex * Az;
        float Hz = Ex * Ay - Ey * Ax;
        final float normH = (float) Math.sqrt(Hx * Hx + Hy * Hy + Hz * Hz);

        if (normH < 0.1f) {
            // device is close to free fall (or in space?), or close to
            // magnetic north pole. Typical values are  > 100.
            return false;
        }
        final float invH = 1.0f / normH;
        Hx *= invH;
        Hy *= invH;
        Hz *= invH;
        final float invA = 1.0f / (float) Math.sqrt(Ax * Ax + Ay * Ay + Az * Az);
        Ax *= invA;
        Ay *= invA;
        Az *= invA;
        final float Mx = Ay * Hz - Az * Hy;
        final float My = Az * Hx - Ax * Hz;
        final float Mz = Ax * Hy - Ay * Hx;
        if (R != null) {
            if (R.length == 9) {
                R[0] = Hx;
                R[1] = Hy;
                R[2] = Hz;
                R[3] = Mx;
                R[4] = My;
                R[5] = Mz;
                R[6] = Ax;
                R[7] = Ay;
                R[8] = Az;
            } else if (R.length == 16) {
                R[0] = Hx;
                R[1] = Hy;
                R[2] = Hz;
                R[3] = 0;
                R[4] = Mx;
                R[5] = My;
                R[6] = Mz;
                R[7] = 0;
                R[8] = Ax;
                R[9] = Ay;
                R[10] = Az;
                R[11] = 0;
                R[12] = 0;
                R[13] = 0;
                R[14] = 0;
                R[15] = 1;
            }
        }
        if (I != null) {
            // compute the inclination matrix by projecting the geomagnetic
            // vector onto the Z (gravity) and X (horizontal component
            // of geomagnetic vector) axes.
            final float invE = 1.0f / (float) Math.sqrt(Ex * Ex + Ey * Ey + Ez * Ez);
            final float c = (Ex * Mx + Ey * My + Ez * Mz) * invE;
            final float s = (Ex * Ax + Ey * Ay + Ez * Az) * invE;
            if (I.length == 9) {
                I[0] = 1;
                I[1] = 0;
                I[2] = 0;
                I[3] = 0;
                I[4] = c;
                I[5] = s;
                I[6] = 0;
                I[7] = -s;
                I[8] = c;
            } else if (I.length == 16) {
                I[0] = 1;
                I[1] = 0;
                I[2] = 0;
                I[4] = 0;
                I[5] = c;
                I[6] = s;
                I[8] = 0;
                I[9] = -s;
                I[10] = c;
                I[3] = I[7] = I[11] = I[12] = I[13] = I[14] = 0;
                I[15] = 1;
            }
        }
        return true;
    }

    /**
     * Computes the device's orientation based on the rotation matrix.
     * <p/>
     * When it returns, the array values are as follows:
     * <ul>
     * <li>values[0]: <i>Azimuth</i>, angle of rotation about the -z axis.
     * This value represents the angle between the device's y
     * axis and the magnetic north pole. When facing north, this
     * angle is 0, when facing south, this angle is &pi;.
     * Likewise, when facing east, this angle is &pi;/2, and
     * when facing west, this angle is -&pi;/2. The range of
     * values is -&pi; to &pi;.</li>
     * <li>values[1]: <i>Pitch</i>, angle of rotation about the x axis.
     * This value represents the angle between a plane parallel
     * to the device's screen and a plane parallel to the ground.
     * Assuming that the bottom edge of the device faces the
     * user and that the screen is face-up, tilting the top edge
     * of the device toward the ground creates a positive pitch
     * angle. The range of values is -&pi; to &pi;.</li>
     * <li>values[2]: <i>Roll</i>, angle of rotation about the y axis. This
     * value represents the angle between a plane perpendicular
     * to the device's screen and a plane perpendicular to the
     * ground. Assuming that the bottom edge of the device faces
     * the user and that the screen is face-up, tilting the left
     * edge of the device toward the ground creates a positive
     * roll angle. The range of values is -&pi;/2 to &pi;/2.</li>
     * </ul>
     * <p/>
     * Applying these three rotations in the azimuth, pitch, roll order
     * transforms an identity matrix to the rotation matrix passed into this
     * method. Also, note that all three orientation angles are expressed in
     * <b>radians</b>.
     *
     * @param R      rotation matrix see {@link #getRotationMatrix}.
     * @param values an array of 3 floats to hold the result.
     * @return The array values passed as argument.
     * @see #getRotationMatrix(float[], float[], float[], float[])
     * @see GeomagneticField
     */
    public static float[] getOrientation(float[] R, float values[]) {
        /*
         * 4x4 (length=16) case:
         *   /  R[ 0]   R[ 1]   R[ 2]   0  \
         *   |  R[ 4]   R[ 5]   R[ 6]   0  |
         *   |  R[ 8]   R[ 9]   R[10]   0  |
         *   \      0       0       0   1  /
         *
         * 3x3 (length=9) case:
         *   /  R[ 0]   R[ 1]   R[ 2]  \
         *   |  R[ 3]   R[ 4]   R[ 5]  |
         *   \  R[ 6]   R[ 7]   R[ 8]  /
         *
         */
        if (R.length == 9) {
            values[0] = (float) Math.atan2(R[1], R[4]);
            values[1] = (float) Math.asin(-R[7]);
            values[2] = (float) Math.atan2(-R[6], R[8]);
        } else {
            values[0] = (float) Math.atan2(R[1], R[5]);
            values[1] = (float) Math.asin(-R[9]);
            values[2] = (float) Math.atan2(-R[8], R[10]);
        }

        return values;
    }

    public static boolean remapCoordinateSystem(float[] inR, int X, int Y,
                                                float[] outR) {
        if (inR == outR) {
            final float[] temp = mTempMatrix;
            synchronized (temp) {
                // we don't expect to have a lot of contention
                if (remapCoordinateSystemImpl(inR, X, Y, temp)) {
                    final int size = outR.length;
                    for (int i = 0; i < size; i++)
                        outR[i] = temp[i];
                    return true;
                }
            }
        }
        return remapCoordinateSystemImpl(inR, X, Y, outR);
    }

    private static boolean remapCoordinateSystemImpl(float[] inR, int X, int Y,
                                                     float[] outR) {
        /*
         * X and Y define a rotation matrix 'r':
         *
         *  (X==1)?((X&0x80)?-1:1):0    (X==2)?((X&0x80)?-1:1):0    (X==3)?((X&0x80)?-1:1):0
         *  (Y==1)?((Y&0x80)?-1:1):0    (Y==2)?((Y&0x80)?-1:1):0    (Y==3)?((X&0x80)?-1:1):0
         *                              r[0] ^ r[1]
         *
         * where the 3rd line is the vector product of the first 2 lines
         *
         */

        final int length = outR.length;
        if (inR.length != length)
            return false;   // invalid parameter
        if ((X & 0x7C) != 0 || (Y & 0x7C) != 0)
            return false;   // invalid parameter
        if (((X & 0x3) == 0) || ((Y & 0x3) == 0))
            return false;   // no axis specified
        if ((X & 0x3) == (Y & 0x3))
            return false;   // same axis specified

        // Z is "the other" axis, its sign is either +/- sign(X)*sign(Y)
        // this can be calculated by exclusive-or'ing X and Y; except for
        // the sign inversion (+/-) which is calculated below.
        int Z = X ^ Y;

        // extract the axis (remove the sign), offset in the range 0 to 2.
        final int x = (X & 0x3) - 1;
        final int y = (Y & 0x3) - 1;
        final int z = (Z & 0x3) - 1;

        // compute the sign of Z (whether it needs to be inverted)
        final int axis_y = (z + 1) % 3;
        final int axis_z = (z + 2) % 3;
        if (((x ^ axis_y) | (y ^ axis_z)) != 0)
            Z ^= 0x80;

        final boolean sx = (X >= 0x80);
        final boolean sy = (Y >= 0x80);
        final boolean sz = (Z >= 0x80);

        // Perform R * r, in avoiding actual muls and adds.
        final int rowLength = ((length == 16) ? 4 : 3);
        for (int j = 0; j < 3; j++) {
            final int offset = j * rowLength;
            for (int i = 0; i < 3; i++) {
                if (x == i) outR[offset + i] = sx ? -inR[offset + 0] : inR[offset + 0];
                if (y == i) outR[offset + i] = sy ? -inR[offset + 1] : inR[offset + 1];
                if (z == i) outR[offset + i] = sz ? -inR[offset + 2] : inR[offset + 2];
            }
        }
        if (length == 16) {
            outR[3] = outR[7] = outR[11] = outR[12] = outR[13] = outR[14] = 0;
            outR[15] = 1;
        }
        return true;
    }

}
