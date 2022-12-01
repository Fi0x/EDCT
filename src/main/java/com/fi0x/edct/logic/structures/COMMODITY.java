package com.fi0x.edct.logic.structures;

import java.util.ArrayList;

public class COMMODITY
{
    public final String NAME;
    public final long GALACTIC_AVERAGE;
    public long profit;
    public final ArrayList<TRADE> IMPORT_PRICES;
    public final ArrayList<TRADE> EXPORT_PRICES;

    public COMMODITY(String name, ArrayList<TRADE> importPrices, ArrayList<TRADE> exportPrices, long galacticAverage)
    {
        NAME = name;
        GALACTIC_AVERAGE = galacticAverage;
        profit = 0;
        IMPORT_PRICES = importPrices;
        EXPORT_PRICES = exportPrices;
    }

    public void sortPrices()
    {
        if(EXPORT_PRICES == null || IMPORT_PRICES == null || EXPORT_PRICES.size() == 0 || IMPORT_PRICES.size() == 0)
        {
            profit = 0;
            return;
        }

        long exporterPrice = EXPORT_PRICES.get(0).EXPORT_PRICE;
        long importerPrice = IMPORT_PRICES.get(0).IMPORT_PRICE;
        profit = importerPrice - exporterPrice;
    }
}
