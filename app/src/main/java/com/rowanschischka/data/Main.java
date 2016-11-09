package com.rowanschischka.data;

import com.rowanschischka.data.Interfaces.implementations.AnglesRaw;
import com.rowanschischka.data.Interfaces.implementations.DiscreteWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        DataRow[] rawData = CsvFile.readFile(filePath);
        System.out.println(rawData.length + " rows of data");
        System.out.println(rawData[rawData.length - 1].getTime() / 1000000000f + " seconds of data");
        //DataReplay.replay(rawData, new DiscreteWindow(25), filePath);
        //DataReplay.replay(rawData, new DiscreteWindow(50), filePath);
        //DataReplay.replay(rawData, new DiscreteWindow(75), filePath);
        DataReplay.replay(rawData, new DiscreteWindow(100), filePath);
        //DataReplay.replay(rawData, new DiscreteWindow(200), filePath);
        DataReplay.replay(rawData, new AnglesRaw(), filePath);
        //create a smoothing window
    }
}
