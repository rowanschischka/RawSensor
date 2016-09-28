package com.rowanschischka.data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rowanschischka on 26/09/16.
 */
public class CsvFile {
    //File file = new File("samsung.csv");
    private static final String TAG = "CsvFile";

    public static void writeFile(DataRow[] data, String filePath) {
        BufferedWriter bufferedWriter = null;
        try {
            File outputFile = new File(filePath);
            bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
            //write table header
            bufferedWriter.write(DataRow.getTableHeader());
            for (DataRow dr : data) {
                bufferedWriter.write(dr.toString());
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public static DataRow[] readFile(String filePath) {
        File inputFile = new File(filePath);
        BufferedReader bufferedReader = null;
        ArrayList<DataRow> data = new ArrayList<>();
        try {
            bufferedReader = new BufferedReader(new FileReader(inputFile));
            String line;
            Log.i(TAG, bufferedReader.readLine());
            while ((line = bufferedReader.readLine()) != null) {
                DataRow dr = new DataRow(line);
                data.add(dr);
            }
            // Sorting
            Collections.sort(data);
            DataRow[] dataArray = new DataRow[data.size()];
            data.toArray(dataArray);
            return dataArray;
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return null;
    }
}
