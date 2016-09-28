package com.rowanschischka.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
                filePath = "/Users/rowanschischka/NetBeansProjects/EVSS/samsung.csv";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("getting file\n" + filePath);
        DataRow[] rawData = CsvFile.readFile(filePath);
    }
}
