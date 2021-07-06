package com.fi0x.edct.data.structures;

import java.util.ArrayList;
import java.util.Collections;

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
        if(SELL_PRICES != null)
        {
            for(int i = 1; i < SELL_PRICES.size(); i++)
            {
                int j = i - 1;
                while(SELL_PRICES.get(j + 1).SELL_PRICE > SELL_PRICES.get(j).SELL_PRICE) //TODO: Check if prices are not swapped
                {
                    Collections.swap(SELL_PRICES, j + 1, j);

                    if(j == 0) break;
                    j--;
                }
            }
        }

        if(BUY_PRICES != null)
        {
            for(int i = 1; i < BUY_PRICES.size(); i++)
            {
                int j = i - 1;
                while(BUY_PRICES.get(j + 1).BUY_PRICE < BUY_PRICES.get(j).BUY_PRICE) //TODO: Check if prices are not swapped
                {
                    Collections.swap(BUY_PRICES, j + 1, j);

                    if(j == 0) break;
                    j--;
                }
            }
        }

        calculateBestProfit();
    }

    private void calculateBestProfit()
    {
        if(BUY_PRICES == null || SELL_PRICES == null || BUY_PRICES.size() == 0 || SELL_PRICES.size() == 0)
        {
            profit = 0;
            return;
        }

        //TODO: Check if prices are not swapped
        long buy = BUY_PRICES.get(0).BUY_PRICE;
        long sell = SELL_PRICES.get(0).SELL_PRICE;
        profit = sell - buy;
    }
}
