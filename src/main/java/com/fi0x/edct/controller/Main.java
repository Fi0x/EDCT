package com.fi0x.edct.controller;

import com.fi0x.edct.data.localstorage.db.DBHandler;
import com.fi0x.edct.data.structures.COMMODITY;
import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATION_OLD;

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

    public void updateFilters(int amount, boolean ignoreDemand, boolean noSmall, boolean noCarrier, boolean noSurface, boolean noOdyssey)
    {
        Map<String, ArrayList<STATION_OLD>> filteredSellPrices = applyFilters(ignoreDemand ? 0 : amount, noSmall, noCarrier, noSurface, noOdyssey, interactionController.sellPrices);
        Map<String, ArrayList<STATION_OLD>> filteredBuyPrices = applyFilters(amount, noSmall, noCarrier, noSurface, noOdyssey, interactionController.buyPrices);

        resultsController.setTrades(getTrades(filteredSellPrices, filteredBuyPrices));

        resultsController.displayResults();
    }

    private Map<String, ArrayList<STATION_OLD>> applyFilters(int amount, boolean noSmall, boolean noCarrier, boolean noSurface, boolean noOdyssey, Map<String, ArrayList<STATION_OLD>> inputPrices)
    {
        Map<String, ArrayList<STATION_OLD>> filteredPrices = new HashMap<>();

        for(Map.Entry<String, ArrayList<STATION_OLD>> commodity : inputPrices.entrySet())
        {
            ArrayList<STATION_OLD> filteredStations = new ArrayList<>();
            for(STATION_OLD station : commodity.getValue())
            {
                boolean validStation = (!noSmall && station.PAD != PADSIZE.NONE) || station.PAD == PADSIZE.L;
                switch(station.TYPE)
                {
                    case CARRIER:
                        if(noCarrier) validStation = false;
                        break;
                    case SURFACE:
                        if(noSurface) validStation = false;
                        break;
                    case ODYSSEY:
                        if(noOdyssey) validStation = false;
                        break;
                    case UNKNOWN:
                        validStation = false;
                        break;
                }
                if(amount > station.QUANTITY) validStation = false;

                if(validStation) filteredStations.add(station);
            }
            filteredPrices.put(commodity.getKey(), filteredStations);
        }

        return filteredPrices;
    }

    private static ArrayList<COMMODITY> getTrades(Map<String, ArrayList<STATION_OLD>> sellPrices, Map<String, ArrayList<STATION_OLD>> buyPrices)
    {
        ArrayList<COMMODITY> trades = new ArrayList<>();

        for(Map.Entry<String, ArrayList<STATION_OLD>> commodity : sellPrices.entrySet())
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