package com.fi0x.edct.data.structures;

public class STATION
{
    public final String SYSTEM;
    public final String NAME;
    public final PADSIZE PAD;
    public final long QUANTITY;
    public final long PRICE;
    public final long UPDATE_TIME;
    public final STATIONTYPE TYPE;

    public STATION(String systemName, String name, PADSIZE padsize, long quantity, long price, STATIONTYPE stationtype, long updateTime)
    {
        SYSTEM = systemName;
        NAME = name;
        PAD = padsize;
        QUANTITY = quantity;
        PRICE = price;
        TYPE = stationtype;
        UPDATE_TIME = updateTime;
    }

    public String getUpdateAge()
    {
        long age = System.currentTimeMillis() - UPDATE_TIME;
        age /= 1000;
        if(age > 60 * 60 * 24) return age / 60 / 60 / 24 + "d";
        else if(age > 60 * 60) return age / 60 / 60 + "h";
        else if(age > 60) return age / 60 / 60 + "min";
        else return age + "s";
    }
}