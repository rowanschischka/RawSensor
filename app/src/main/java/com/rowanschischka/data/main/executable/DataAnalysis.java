package com.rowanschischka.data.main.executable;

import com.rowanschischka.data.CsvFile;
import com.rowanschischka.data.DataRow;
import com.rowanschischka.data.SensorMathFunctions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by rowanschischka on 29/09/16.
 */
public class DataAnalysis {
    private static String filePath = " no file";
    private static float alpha = 0.9f;

    public static void main(String[] args) {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            filePath = inputReader.readLine();
            if (filePath.isEmpty()) {
                //default value
                filePath = "/Users/rowanschischka/sensorData/gordonton.road/raw.csv";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("getting file\n" + filePath);
        DataRow[] rawData = CsvFile.readFile(filePath);
        System.out.println(rawData.length + " rows of data");

        //data to be collected
        DataRow[] gravityDataSet = new DataRow[rawData.length];
        DataRow[] linearAccelerationDataSet = new DataRow[rawData.length];
        DataRow[] magnetLowPassDataSet = new DataRow[rawData.length];
        DataRow[] magnetHighPassDataSet = new DataRow[rawData.length];
        DataRow[] anglesDataSet = new DataRow[rawData.length];

        float[] prevGravity = new float[3];
        float[] prevMagnetLowPass = new float[3];
        //float[] linearAcceleration;
        //float[] magnetHighPass;
        float[] prevAngle = new float[]{0f, 0f, 0f};
        long maxTime = 0;
        DataRow current;
        //run through data in time order
        for (int i = 0; i < rawData.length; i++) {
            current = rawData[i];
            //for stats to STD out
            if (current.getTime() > maxTime) {
                maxTime = current.getTime();
            }
            if (current.getType().equals(DataRow.TYPE_MAGNETOMETER_RAW) || current.getType().equals(DataRow.TYPE_ACCELEROMETER_RAW)) {
                if (current.getType().equals(DataRow.TYPE_ACCELEROMETER_RAW)) {
                    //accelerometer filtered
                    float[] filteredResult = SensorMathFunctions.lowHighPassFilter(current.getVector(), alpha, prevGravity);
                    prevGravity = SensorMathFunctions.getLowPass(filteredResult);
                    //check length
                    //float length = SensorMathFunctions.getVectorLength(prevGravity);
                    //if (length > 9.7f && length < 9.9f) {
                    //gravityDataSet[i] = new DataRow(DataRow.TYPE_GRAVITY, current.getTime(), prevGravity);
                    // } else {

                    //}
                    float[] linearAcceleration = SensorMathFunctions.getHighPass(filteredResult);
                    linearAccelerationDataSet[i] = new DataRow(DataRow.TYPE_LINEAR_ACCELERATION, current.getTime(), linearAcceleration);
                } else if (current.getType().equals(DataRow.TYPE_MAGNETOMETER_RAW)) {
                    //filter magnetometer
                    float[] filteredResult = SensorMathFunctions.lowHighPassFilter(current.getVector(), alpha, prevMagnetLowPass);
                    prevMagnetLowPass = SensorMathFunctions.getLowPass(filteredResult);
                    magnetLowPassDataSet[i] = new DataRow(DataRow.TYPE_MAGNETOMETER_LOWPASS, current.getTime(), prevMagnetLowPass);
                    float[] magnetHighPass = SensorMathFunctions.getHighPass(filteredResult);
                    magnetHighPassDataSet[i] = new DataRow(DataRow.TYPE_MAGNETOMETER_HIGHPASS, current.getTime(), magnetHighPass);
                    float[] angles = SensorMathFunctions.calculateAngleDegrees(prevGravity, prevMagnetLowPass);
                    //ADJUSTMENT
                    if (angles != null) {
                        angles[1] += 80f;
                        float[] filteredAngle = SensorMathFunctions.lowHighPassFilter(angles, alpha, prevAngle);
                        prevAngle = SensorMathFunctions.getLowPass(filteredAngle);
                        anglesDataSet[i] = new DataRow(DataRow.TYPE_ROTATION, current.getTime(), prevAngle);
                    }
                }
            }
        }
        int count;
        //System.out.println("Writing " + fileWriteName(DataRow.TYPE_GRAVITY));
        count = CsvFile.writeFile(gravityDataSet, fileWriteName(DataRow.TYPE_GRAVITY));
        // System.out.println("No. data = " + count);

        //System.out.println("Writing " + fileWriteName(DataRow.TYPE_LINEAR_ACCELERATION));
        count = CsvFile.writeFile(linearAccelerationDataSet, fileWriteName(DataRow.TYPE_LINEAR_ACCELERATION));
        //System.out.println("No. data = " + count);

        //System.out.println("Writing " + fileWriteName(DataRow.TYPE_MAGNETOMETER_LOWPASS));
        count = CsvFile.writeFile(magnetLowPassDataSet, fileWriteName(DataRow.TYPE_MAGNETOMETER_LOWPASS));
        //System.out.println("No. data = " + count);

        //System.out.println("Writing " + fileWriteName(DataRow.TYPE_MAGNETOMETER_HIGHPASS));
        count = CsvFile.writeFile(magnetHighPassDataSet, fileWriteName(DataRow.TYPE_MAGNETOMETER_HIGHPASS));
        //System.out.println("No. data = " + count);

        //System.out.println("Writing " + fileWriteName(DataRow.TYPE_ROTATION));
        count = CsvFile.writeFile(anglesDataSet, fileWriteName(DataRow.TYPE_ROTATION));
        //System.out.println("No. data = " + count);

        System.out.println("Time in nanoseconds = " + maxTime);
        System.out.println("Time in seconds = " + (maxTime / 1000000000l));
        System.out.println("Alpha = " + alpha);
    }

    private static String fileWriteName(String type) {
        int a = (int) Math.floor((double) (alpha * 10));
        return filePath + "." + type + ".csv";
    }
}
