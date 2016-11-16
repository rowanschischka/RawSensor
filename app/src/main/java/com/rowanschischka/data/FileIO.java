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
public class FileIO {
    private static final String TAG = "FileIO";

    public static int writeFile(DataRow[] data, String filePath) {
        System.out.println("Writing " + filePath);
        BufferedWriter bufferedWriter = null;
        int count = 0;
        try {
            File outputFile = new File(filePath);
            bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
            //write table header
            bufferedWriter.write(DataRow.getTableHeader() + "\n");
            for (DataRow dr : data) {
                if (dr != null && !dr.getType().isEmpty()) {
                    bufferedWriter.write(dr.toString() + "\n");
                    //System.out.println(dr.toString());
                    count++;
                }
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
        return count;
    }

    public static DataRow[] readFile(String filePath) {
        File inputFile = new File(filePath);
        BufferedReader bufferedReader = null;
        ArrayList<DataRow> data = new ArrayList<>();
        try {
            bufferedReader = new BufferedReader(new FileReader(inputFile));
            String line;
            //discard header
            line = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
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
