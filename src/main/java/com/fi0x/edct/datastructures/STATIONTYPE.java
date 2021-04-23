package com.fi0x.edct.datastructures;

public enum STATIONTYPE
{
    ORBIT,
    CARRIER,
    SURFACE;

    public static STATIONTYPE getFromString(String name)
    {
        switch(name)
        {
            case "CARRIER":
                return CARRIER;
            case "SURFACE":
                return SURFACE;
            default:
                return ORBIT;
        }
    }
}
