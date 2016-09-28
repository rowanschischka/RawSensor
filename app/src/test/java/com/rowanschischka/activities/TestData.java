package com.rowanschischka.activities;

import com.rowanschischka.data.DataRow;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

/**
 * test new algorithms
 */
public class TestData {
    @Test
    public void test_sort() throws Exception {
        ArrayList<DataRow> data = new ArrayList<>();
        data.add(new DataRow(0L));
        data.add(new DataRow(1L));
        data.add(new DataRow(3L));
        data.add(new DataRow(5L));
        data.add(new DataRow(4L));
        data.add(new DataRow(2L));
        data.add(new DataRow(6L));
        Collections.sort(data);
        for (int i = 1; i < data.size(); i++) {
            assert (data.get(i - 1).getTime() < data.get(i).getTime());
        }
    }
}