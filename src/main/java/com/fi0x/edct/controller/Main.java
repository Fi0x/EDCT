package com.fi0x.edct.controller;

import com.fi0x.edct.data.localstorage.db.DBHandler;
import com.fi0x.edct.data.structures.COMMODITY;
import com.fi0x.edct.data.structures.FILTEROPTIONS;
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
    public Interaction interactionController;

    public void setResultController(Results controller)
    {
        resultsController = controller;
    }
    public void setInteractionController(Interaction controller)
    {
        interactionController = controller;
    }

    public void updateFilters()
    {
        ArrayList<String> blacklist = BlacklistHandler.getBlacklistSystems();
        Filters filters = Filters.getInstance();
        if(filters == null) return;
        FILTEROPTIONS filteroptions = filters.getFilterSettings();

        Map<String, ArrayList<TRADE>> filteredBuyPrices = applyFilters(filteroptions, blacklist, interactionController.buyPrices);
        if(filteroptions.demand) filteroptions.amount = 0;
        Map<String, ArrayList<TRADE>> filteredSellPrices = applyFilters(filteroptions, blacklist, interactionController.sellPrices);

        resultsController.setTrades(getTrades(filteredSellPrices, filteredBuyPrices));

        resultsController.displayResults();
    }

    private Map<String, ArrayList<TRADE>> applyFilters(FILTEROPTIONS filteroptions, ArrayList<String> blacklist, Map<String, ArrayList<TRADE>> inputPrices)
    {
        Map<String, ArrayList<TRADE>> filteredPrices = new HashMap<>();

        for(Map.Entry<String, ArrayList<TRADE>> trades : inputPrices.entrySet())
        {
            if(DBHandler.getCommodityAverage(trades.getKey()) < filteroptions.average) continue;

            ArrayList<TRADE> filteredStations = new ArrayList<>();
            for(TRADE trade : trades.getValue())
            {
                if(filteroptions.blacklist && blacklist.contains(trade.STATION.SYSTEM)) continue;
                switch(trade.STATION.TYPE)
                {
                    case CARRIER:
                        if(!filteroptions.carrier) continue;
                        break;
                    case SURFACE:
                        if(!filteroptions.surface) continue;
                        break;
                    case ODYSSEY:
                        if(!filteroptions.odyssey) continue;
                        break;
                    case UNKNOWN:
                        continue;
                }
                if(filteroptions.amount > Math.max(trade.SUPPLY, trade.DEMAND)) continue;

                if((filteroptions.landingPad && trade.STATION.PAD != PADSIZE.NONE) || trade.STATION.PAD == PADSIZE.L) filteredStations.add(trade);
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