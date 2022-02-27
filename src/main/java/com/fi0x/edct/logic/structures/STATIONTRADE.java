package com.fi0x.edct.logic.structures;

public class STATIONTRADE
{
    public final String COMMODITY_NAME;
    public final int COMMODITY_INARA_ID;
    public final int STATION_BUYS_AT;
    public final int STATION_SELLS_AT;
    public final int STATION_DEMAND;
    public final int STATION_SUPPLY;

    public STATIONTRADE(String commodityNAME, int inaraID, int importPrice, int exportPrice, int demand, int supply)
    {
        COMMODITY_NAME = commodityNAME;
        COMMODITY_INARA_ID = inaraID;
        STATION_BUYS_AT = importPrice;
        STATION_SELLS_AT = exportPrice;
        STATION_DEMAND = demand;
        STATION_SUPPLY = supply;
    }
}
