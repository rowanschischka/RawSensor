package com.rowanschischka.data;

import com.rowanschischka.data.Interfaces.implementations.AngleFromGPS;
import com.rowanschischka.data.Interfaces.implementations.AnglesRaw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.rowanschischka.data.FileIO.readFile;

/**
 * Created by rowanschischka on 29/09/16.
 */
public class Main {
    private static String filePath = "/Users/rowanschischka/sensorData/";

    public static void main(String[] args) {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Enter filepath of data file: ");
            filePath += inputReader.readLine() + ".csv";
            if (filePath.isEmpty()) {
                //default value
                //   filePath = "/home/rowan/sensordata/raw.csv";
                filePath += "samsung.csv";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("getting file\n" + filePath);
        DataRow[] rawData = readFile(filePath);
        System.out.println(rawData.length + " rows of data");
        System.out.println(rawData[rawData.length - 1].getTimeStamp() / 1000000000f + " seconds of data");

        long time = rawData[rawData.length - 1].getTimeStamp();
        WriteR rWriter = new WriteR(filePath, time);
        process(rWriter, rawData);
        rWriter.close();
    }

    public static void process(WriteR rWriter, DataRow[] rawData) {
        String filepath = DataReplay.replay(rawData, new AnglesRaw(), filePath);
        rWriter.write(filepath, 0, 0, 0, "SensorAngle");
        filepath = DataReplay.replay(rawData, new AngleFromGPS(), filePath);
        rWriter.write(filepath, 0, 0, 0, "GpsAngle");
    }
}
