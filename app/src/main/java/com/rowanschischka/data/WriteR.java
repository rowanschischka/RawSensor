package com.rowanschischka.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteR {
    BufferedWriter bufferedWriter;

    public WriteR(String filepath, long time) {
        File outputFile = new File(filepath);
        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter(filepath + "_R.txt"));
            bufferedWriter.write(readCsv(filepath, "raw"));
            bufferedWriter.write(getPlot(time));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String filePath, float r, float g, float b, String name) {
        try {
            bufferedWriter.write(readCsv(filePath, name));
            bufferedWriter.write(getLine(name, r, g, b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readCsv(String fileName, String name) {
        return name + "<-read.csv(\"" + fileName + "\")\n";
    }

    public void close() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPlot(long time) {
        return "\nplot(c(0000000000, " + time + "), c(-19, 19), main = \"main\", xlab = \"Time\", type = \"n\", ylab = \"\");\n";
    }

    private String getLine(String name, float r, float g, float b) {
        return "\npoints(" + name + "$TIME, " + name + "$Z, cex=0.1, col=rgb(" + r + "," + g + "," + b + "));\n";
    }
}
