package com.rowanschischka.data;

import com.rowanschischka.data.Interfaces.DataFunction;

import java.util.ArrayList;

/**
 * Created by rowanschischka on 27/10/16.
 */

public class DataReplay {
    public static String replay(DataRow[] inputData, DataFunction function, String filePath) {
        ArrayList<DataRow> outputData = new ArrayList<>();
        for (int i = 0; i < inputData.length; i++) {
            DataRow result = function.processEvent(inputData[i]);
            if (result != null)
                outputData.add(result);
        }
        DataRow[] outArray = new DataRow[outputData.size()];
        outputData.toArray(outArray);
        String filepath = filePath + function.getFileName() + ".csv";
        FileIO.writeFile(outArray, filepath);
        return filepath;
    }
}