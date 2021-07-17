package com.fi0x.edct.controller;

import com.fi0x.edct.data.localstorage.db.DBHandler;
import com.fi0x.edct.data.structures.COMMODITY;
import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.TRADE;
import com.fi0x.edct.util.BlacklistHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main
{
    private Results resultsController;
    private Interaction interactionController;

    public void setResultController(Results controller)
    {
        resultsController = controller;
    }
    public void setInteractionController(Interaction controller)
    {
        interactionController = controller;
    }

    public void updateFilters(long galacticAverage, int amount, boolean ignoreDemand, boolean noSmall, boolean noCarrier, boolean noSurface, boolean noOdyssey, boolean useBlacklist)
    {
        ArrayList<String> blacklist = BlacklistHandler.getBlacklistSystems();

        Map<String, ArrayList<TRADE>> filteredSellPrices = applyFilters(galacticAverage, ignoreDemand ? 0 : amount, noSmall, noCarrier, noSurface, noOdyssey, useBlacklist, blacklist, interactionController.sellPrices);
        Map<String, ArrayList<TRADE>> filteredBuyPrices = applyFilters(galacticAverage, amount, noSmall, noCarrier, noSurface, noOdyssey, useBlacklist, blacklist, interactionController.buyPrices);

        resultsController.setTrades(getTrades(filteredSellPrices, filteredBuyPrices));

        resultsController.displayResults();
    }

    private Map<String, ArrayList<TRADE>> applyFilters(long average, int amount, boolean noSmall, boolean noCarrier, boolean noSurface, boolean noOdyssey, boolean useBlacklist, ArrayList<String> blacklist, Map<String, ArrayList<TRADE>> inputPrices)
    {
        Map<String, ArrayList<TRADE>> filteredPrices = new HashMap<>();

        for(Map.Entry<String, ArrayList<TRADE>> trades : inputPrices.entrySet())
        {
            if(DBHandler.getCommodityAverage(trades.getKey()) < average) continue;

            ArrayList<TRADE> filteredStations = new ArrayList<>();
            for(TRADE trade : trades.getValue())
            {
                if(useBlacklist && blacklist.contains(trade.STATION.SYSTEM)) continue;
                switch(trade.STATION.TYPE)
                {
                    case CARRIER:
                        if(noCarrier) continue;
                        break;
                    case SURFACE:
                        if(noSurface) continue;
                        break;
                    case ODYSSEY:
                        if(noOdyssey) continue;
                        break;
                    case UNKNOWN:
                        continue;
                }
                if(amount > Math.max(trade.SUPPLY, trade.DEMAND)) continue;

                if((!noSmall && trade.STATION.PAD != PADSIZE.NONE) || trade.STATION.PAD == PADSIZE.L) filteredStations.add(trade);
            }
            filteredPrices.put(trades.getKey(), filteredStations);
        }

        return filteredPrices;
    }

    private static ArrayList<COMMODITY> getTrades(Map<String, ArrayList<TRADE>> sellPrices, Map<String, ArrayList<TRADE>> buyPrices)
    {
        ArrayList<COMMODITY> trades = new ArrayList<>();

        for(Map.Entry<String, ArrayList<TRADE>> commodity : sellPrices.entrySet())
        {
            long galAvg = DBHandler.getCommodityAverage(commodity.getKey());
            COMMODITY commodityTrade = new COMMODITY(commodity.getKey(), commodity.getValue(), buyPrices.get(commodity.getKey()), galAvg);
            commodityTrade.sortPrices();

            if(commodityTrade.profit > 0) trades.add(commodityTrade);
        }

        return sortTrades(trades);
    }

    private static ArrayList<COMMODITY> sortTrades(ArrayList<COMMODITY> trades)
    {
        for(int i = 1; i < trades.size(); i++)
        {
            int j = i - 1;
            while(trades.get(j + 1).profit > trades.get(j).profit)
            {
                Collections.swap(trades, j + 1, j);

                if(j == 0) break;
                j--;
            }
        }

        return trades;
    }
}