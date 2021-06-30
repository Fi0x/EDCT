package com.fi0x.edct.data.structures;

public class TRADE
{
    public final STATION STATION;
    public final int INARA_ID;
    public final long AGE;
    public final long SUPPLY;
    public final long DEMAND;
    public final long BUY_PRICE;
    public final long SELL_PRICE;

    public TRADE(STATION station, int inaraID, long age, long supply, long demand, long buyPrice, long sellPrice)
    {
        STATION = station;
        INARA_ID = inaraID;
        AGE = age;
        SUPPLY = supply;
        DEMAND = demand;
        BUY_PRICE = buyPrice;
        SELL_PRICE = sellPrice;
    }
}
