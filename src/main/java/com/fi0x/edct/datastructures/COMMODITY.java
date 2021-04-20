package com.fi0x.edct.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class COMMODITY
{
    public final String NAME;
    public int profit;
    public final ArrayList<STATION> SELL_PRICES;
    public final ArrayList<STATION> BUY_PRICES;

    public COMMODITY(String name, ArrayList<STATION> sellPrices, ArrayList<STATION> buyPrices)
    {
        NAME = name;
        profit = 0;
        SELL_PRICES = sellPrices;
        BUY_PRICES = buyPrices;
    }

    public void sortPrices()
    {
        for(int i = 1; i < SELL_PRICES.size(); i++)
        {
            int j = i - 1;
            while(SELL_PRICES.get(j + 1).PRICE > SELL_PRICES.get(j).PRICE)
            {
                Collections.swap(SELL_PRICES, j + 1, j);

                if(j == 0) break;
                j--;
            }
        }

        for(int i = 1; i < BUY_PRICES.size(); i++)
        {
            int j = i - 1;
            while(BUY_PRICES.get(j + 1).PRICE < BUY_PRICES.get(j).PRICE)
            {
                Collections.swap(BUY_PRICES, j + 1, j);

                if(j == 0) break;
                j--;
            }
        }

        calculateBestProfit();
    }

    private void calculateBestProfit()
    {
        int buy = BUY_PRICES.get(0).PRICE;
        int sell = SELL_PRICES.get(0).PRICE;
        profit = sell - buy;
    }
}
