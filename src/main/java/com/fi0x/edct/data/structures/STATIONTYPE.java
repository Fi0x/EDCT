package com.fi0x.edct.data.structures;

public enum STATIONTYPE
{
    ORBIT,
    CARRIER,
    SURFACE,
    ODYSSEY;

    public static STATIONTYPE getFromString(String name)
    {
        switch(name)
        {
            case "CARRIER":
                return CARRIER;
            case "SURFACE":
                return SURFACE;
            case "ODYSSEY":
                return ODYSSEY;
            default:
                return ORBIT;
        }
    }
}
