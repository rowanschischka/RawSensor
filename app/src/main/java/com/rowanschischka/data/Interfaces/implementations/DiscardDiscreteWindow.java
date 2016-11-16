package com.rowanschischka.data.Interfaces.implementations;

import com.rowanschischka.data.DataRow;
import com.rowanschischka.data.Interfaces.DataFunction;
import com.rowanschischka.data.SensorMath;

import java.util.ArrayList;

/**
 * Created by rowanschischka on 27/10/16.
 */

public class DiscardDiscreteWindow implements DataFunction {
    ArrayList<float[]> angleWindow = new ArrayList<>();
    float lastGravity = 9.8f;
    //window size 50 @ 20ms = 1s window
    int windowSize;

    public DiscardDiscreteWindow(int windowSize) {
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
            angleWindow.add(dataRow.getXYZ());
        }
        if (angleWindow.size() >= windowSize) {
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
