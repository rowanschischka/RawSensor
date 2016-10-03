package com.rowanschischka.data.main.executable;

import com.rowanschischka.data.CsvFile;
import com.rowanschischka.data.DataRow;
import com.rowanschischka.data.SensorMathFunctions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by rowanschischka on 29/09/16.
 */
public class DataAnalysis {
    public static void main(String[] args) {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String filePath = " no file";
        try {
            filePath = inputReader.readLine();
            if (filePath.isEmpty()) {
                //default value
                filePath = "/Users/rowanschischka/sensorData/rotationTest.csv";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("getting file\n" + filePath);
        DataRow[] rawData = CsvFile.readFile(filePath);
        DataRow[] smoothedData = new DataRow[rawData.length];
        System.out.println(rawData.length + " rows of data");

        float smoothingFactor = 60f;
        float[] previousAccelerometer = null;
        float[] previousMagnetometer = null;
        DataRow current;
        //run through data in time order
        for (int i = 0; i < rawData.length; i++) {
            current = rawData[i];
            //smooth data
            smoothedData[i] = current;
            if (current.getType().equals(DataRow.TYPE_MAGNETOMETER_RAW) || current.getType().equals(DataRow.TYPE_ACCELEROMETER)) {
                float[] prev = null;
                if (current.getType().equals(DataRow.TYPE_ACCELEROMETER)) {
                    if (previousAccelerometer != null) {
                        prev = previousAccelerometer;
                    }
                    previousAccelerometer = new float[]{current.getX(), current.getY(), current.getZ()};
                } else if (current.getType().equals(DataRow.TYPE_MAGNETOMETER_RAW)) {
                    if (previousMagnetometer != null) {
                        prev = previousMagnetometer;
                    }
                    previousMagnetometer = new float[]{current.getX(), current.getY(), current.getZ()};
                }
                if (prev != null) {
                    float[] smoothed = SensorMathFunctions.smoothingFilter(
                            current.getX(),
                            current.getY(),
                            current.getZ(),
                            prev[0],
                            prev[1],
                            prev[2],
                            smoothingFactor);
                    smoothedData[i].setX(smoothed[0]);
                    smoothedData[i].setY(smoothed[1]);
                    smoothedData[i].setZ(smoothed[2]);
                }
            } else if (current.getType().equals(DataRow.TYPE_GPS)) {
                //smoothedData[i] = current;
            }
            //create angle table
        }
        CsvFile.writeFile(smoothedData, filePath + ".smoothedData.csv");
        calculateAngles(rawData, filePath + ".raw");
        calculateAngles(smoothedData, filePath + ".smoothed");
    }

    private static void calculateAngles(DataRow[] data, String filePath) {
        //rotation data
        ArrayList<DataRow> rotationList = new ArrayList<>();
        float[] gravity = null;
        DataRow current;
        for (int i = 0; i < data.length; i++) {
            current = data[i];
            if (current.getType().equals(DataRow.TYPE_MAGNETOMETER_RAW) || current.getType().equals(DataRow.TYPE_ACCELEROMETER)) {
                if (current.getType().equals(DataRow.TYPE_ACCELEROMETER)) {
                    gravity = new float[]{current.getX(), current.getY(), current.getZ()};
                } else if (current.getType().equals(DataRow.TYPE_MAGNETOMETER_RAW)) {
                    if (gravity != null) {
                        float[] magnet = new float[]{current.getX(), current.getY(), current.getZ()};
                        float[] angles = SensorMathFunctions.calculateAngleRadians(gravity, magnet);
                        DataRow dr = new DataRow(current.getTime(), angles);
                        rotationList.add(dr);
                    }
                }
            }
        }
        DataRow[] rotationData = new DataRow[rotationList.size()];
        rotationList.toArray(rotationData);
        CsvFile.writeFile(rotationData, filePath + ".rotation.csv");
    }
}
