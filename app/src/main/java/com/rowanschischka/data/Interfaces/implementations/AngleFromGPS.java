package com.rowanschischka.data.Interfaces.implementations;

import com.rowanschischka.data.DataRow;
import com.rowanschischka.data.Interfaces.DataFunction;

/**
 * Created by rowanschischka on 16/11/16.
 */

public class AngleFromGPS implements DataFunction {
    float prevSpeed = 0f;
    float prevAlt = 0f;
    float prevTime = 0f;

    @Override
    public String getType() {
        return "ANGLE_FROM_GPS";
    }

    @Override
    public String getFileName() {
        return getType();
    }

    @Override
    public DataRow processEvent(DataRow dataRow) {
        if (dataRow.getType().equals(DataRow.TYPE_GPS)) {
            float currentSpeed = dataRow.getGpsSpeed();
            float currentAlt = dataRow.getGpsAltitude();
            long currentTime = dataRow.getTimeStamp() / 1000000000;
            if (prevSpeed == 0f) {
                prevSpeed = currentSpeed;
                prevAlt = currentAlt;
                prevTime = currentTime;
                return null;
            }
            float abs = (currentAlt - prevAlt) * (currentAlt - prevAlt) / 2;
            if (abs < 5f) {
                //return null;
            }
            float diffAlt = currentAlt - prevAlt;
            float diffTime = currentTime - prevTime;
            float distance = currentSpeed * diffTime;
            //opposite over adjacent
            double oOverA = (double) (distance / diffAlt);
            float angleOpposite = (float) Math.toDegrees(Math.atan(oOverA));
            double aOverH = (double) (diffAlt / distance);
            float angleHypotenuse = (float) Math.toDegrees(Math.acos(aOverH));

            float t = 90f - (float) Math.toDegrees(Math.atan2(distance, diffAlt));

            float[] values = new float[]{angleOpposite, angleHypotenuse, t};

            //System.out.println("ALT:" + currentAlt + " SPEED:" + currentSpeed + " DIFF_ALT" + diffAlt + " DIFF_TIME:" + diffTime + " DISTANCE:" + distance);

            prevSpeed = currentSpeed;
            prevAlt = currentAlt;
            prevTime = currentTime;

            return new DataRow(getType(), dataRow.getTimeStamp(), values, dataRow.getUpTime());
        }
        return null;
    }
}
