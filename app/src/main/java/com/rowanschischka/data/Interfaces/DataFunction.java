package com.rowanschischka.data.Interfaces;

import com.rowanschischka.data.DataRow;

/**
 * Created by rowanschischka on 27/10/16.
 */

public interface DataFunction {
    String getType();

    String getFileName();

    DataRow processEvent(DataRow dataRow);
}
