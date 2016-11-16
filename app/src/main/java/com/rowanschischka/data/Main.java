package com.rowanschischka.data;

import com.rowanschischka.data.Interfaces.implementations.AngleFromGPS;
import com.rowanschischka.data.Interfaces.implementations.AnglesChavFiltered;
import com.rowanschischka.data.Interfaces.implementations.AnglesRaw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.rowanschischka.data.FileIO.readFile;

/**
 * Created by rowanschischka on 29/09/16.
 */
public class Main {
    private static String filePath = " no file";

    public static void main(String[] args) {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Enter filepath of data file: ");
            filePath = inputReader.readLine();
            if (filePath.isEmpty()) {
                //default value
                //   filePath = "/home/rowan/sensordata/raw.csv";
                filePath = "/Users/rowanschischka/sensorData/raw.csv";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("getting file\n" + filePath);
        DataRow[] rawData = readFile(filePath);
        System.out.println(rawData.length + " rows of data");
        System.out.println(rawData[rawData.length - 1].getTime() / 1000000000f + " seconds of data");

        long time = rawData[rawData.length - 1].getTime();
        WriteR rWriter = new WriteR(filePath, time);
        String filepath = DataReplay.replay(rawData, new AnglesRaw(), filePath);
        rWriter.write(filepath, 0, 0, 0, "s0min0max0");
        float r = 0f;
        float g = 0f;
        float b = 0f;
        /*for (int speed = 0; speed < 25f; speed++) {
            r += 0.1f;
            for (int min = 94; min < 99; min++) {
                g += 0.1f;
                b = 0f;
                for (int max = 95; max < 99; max++) {
                    b += 0.1f;
                    if (min < max) {
                        filepath = DataReplay.replay(rawData, new AnglesChavFiltered(100, (float) speed, min / 10f, max / 10f), filePath);
                        rWriter.write(filepath, r, g, b, "s" + speed + "min" + min + "max" + max);
                    }
                }
            }
        }*/
        String fp = DataReplay.replay(rawData, new AnglesChavFiltered(100, 10f, 9.5f, 9.8f), filePath);
        rWriter.write(fp, r, g, b, "s25min9.5max9.9");
        fp = DataReplay.replay(rawData, new AngleFromGPS(), filePath);
        rWriter.write(fp, 1, g, b, "gps.angle");
        rWriter.close();
    }
}
