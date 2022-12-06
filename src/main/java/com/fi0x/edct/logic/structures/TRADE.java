package com.fi0x.edct.logic.structures;

public class TRADE
{
    public final STATION STATION;
    public final int INARA_ID;
    public long AGE;
    public long SUPPLY;
    public long DEMAND;
    public long IMPORT_PRICE;
    public long EXPORT_PRICE;

    public TRADE(STATION station, int inaraID, long age, long supply, long demand, long importStationPrice, long exportStationPrice)
    {
        STATION = station;
        INARA_ID = inaraID;
        AGE = age;
        SUPPLY = supply;
        DEMAND = demand;
        IMPORT_PRICE = importStationPrice;
        EXPORT_PRICE = exportStationPrice;
    }

    public String getUpdateAge()
    {
        long age = System.currentTimeMillis() - AGE;
        age /= 1000;
        if(age > 60 * 60 * 24)
            return age / 60 / 60 / 24 + "d";
        else if(age > 60 * 60)
            return age / 60 / 60 + "h";
        else if(age > 60)
            return age / 60 + "min";
        else
            return age + "s";
    }
}
