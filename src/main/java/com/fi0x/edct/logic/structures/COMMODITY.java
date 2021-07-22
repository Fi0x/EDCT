package com.fi0x.edct.logic.structures;

import java.util.ArrayList;

public class COMMODITY
{
    public final String NAME;
    public final long GALACTIC_AVERAGE;
    public long profit;
    public final ArrayList<TRADE> SELL_PRICES;
    public final ArrayList<TRADE> BUY_PRICES;

    public COMMODITY(String name, ArrayList<TRADE> sellPrices, ArrayList<TRADE> buyPrices, long galacticAverage)
    {
        NAME = name;
        GALACTIC_AVERAGE = galacticAverage;
        profit = 0;
        SELL_PRICES = sellPrices;
        BUY_PRICES = buyPrices;
    }

    public void sortPrices()
    {
        if(BUY_PRICES == null || SELL_PRICES == null || BUY_PRICES.size() == 0 || SELL_PRICES.size() == 0)
        {
            profit = 0;
            return;
        }

        long buy = BUY_PRICES.get(0).SELL_PRICE;
        long sell = SELL_PRICES.get(0).BUY_PRICE;
        profit = sell - buy;
    }
}
