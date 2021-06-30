package com.fi0x.edct.data.structures;

public class STATION
{
    public final String SYSTEM;
    public final String NAME;
    public final PADSIZE PAD;
    public final STATIONTYPE TYPE;

    public STATION(String systemName, String name, PADSIZE padsize, STATIONTYPE stationtype)
    {
        SYSTEM = systemName;
        NAME = name;
        PAD = padsize;
        TYPE = stationtype;
    }
}