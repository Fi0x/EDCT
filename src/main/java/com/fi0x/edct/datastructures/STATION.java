package com.fi0x.edct.datastructures;

public class STATION
{
    public final String SYSTEM;
    public final String NAME;
    public final PADSIZE PAD;
    public final int QUANTITY;
    public final int PRICE;
    public final STATIONTYPE TYPE;
    public final int STAR_DISTANCE;

    public STATION(String systemName, String name, PADSIZE padsize, int quantity, int price, STATIONTYPE stationtype, int distanceToStar)
    {
        SYSTEM = systemName;
        NAME = name;
        PAD = padsize;
        QUANTITY = quantity;
        PRICE = price;
        TYPE = stationtype;
        STAR_DISTANCE = distanceToStar;
    }

    public static STATION getStationFromParts(String[] parts)
    {
        return new STATION(parts[0], parts[1], PADSIZE.getFromString(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), STATIONTYPE.getFromString(parts[5]), Integer.parseInt(parts[6]));
    }
}