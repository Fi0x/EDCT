package com.fi0x.edct.dbconnection;

import com.fi0x.edct.enums.PADSIZE;
import com.fi0x.edct.enums.STATIONTYPE;

public class STATION
{
    ;

    public final String NAME;
    public final PADSIZE PAD;
    public final int QUANTITY;
    public final int PRICE;
    public final STATIONTYPE TYPE;

    public STATION(String name, PADSIZE padsize, int quantity, int price, STATIONTYPE stationtype)
    {
        NAME = name;
        PAD = padsize;
        QUANTITY = quantity;
        PRICE = price;
        TYPE = stationtype;
    }
}