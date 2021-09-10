package com.fi0x.edct.logic.structures;

public class STATION
{
    public final String SYSTEM;
    public final String NAME;
    public final PADSIZE PAD;
    public final STATIONTYPE TYPE;
    public final double DISTANCE_TO_STAR;

    public STATION(String systemName, String name, PADSIZE padsize, STATIONTYPE stationtype, double distanceToStar)
    {
        SYSTEM = systemName;
        NAME = name;
        PAD = padsize;
        TYPE = stationtype;
        DISTANCE_TO_STAR = distanceToStar;
    }
}