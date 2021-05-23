package com.fi0x.edct.controller;

import com.fi0x.edct.data.structures.COMMODITY;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATIONTYPE;

import java.util.*;

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

    public void updateFilters(int amount, int profit, boolean ignoreDemand, boolean noSmall, boolean noCarrier, boolean noSurface)
    {
        Map<String, ArrayList<STATION>> filteredSellPrices = applyFilters(ignoreDemand ? 0 : amount, noSmall, noCarrier, noSurface, interactionController.sellPrices);
        Map<String, ArrayList<STATION>> filteredBuyPrices = applyFilters(amount, noSmall, noCarrier, noSurface, interactionController.buyPrices);

        resultsController.setTrades(getTrades(filteredSellPrices, filteredBuyPrices, profit));

        resultsController.displayResults();
    }

    private Map<String, ArrayList<STATION>> applyFilters(int amount, boolean noSmall, boolean noCarrier, boolean noSurface, Map<String, ArrayList<STATION>> inputPrices)
    {
        Map<String, ArrayList<STATION>> filteredPrices = new HashMap<>();

        for(Map.Entry<String, ArrayList<STATION>> commodity : inputPrices.entrySet())
        {
            ArrayList<STATION> filteredStations = new ArrayList<>();
            for(STATION station : commodity.getValue())
            {
                boolean validStation = !noSmall || station.PAD == PADSIZE.L;
                if(noCarrier && station.TYPE == STATIONTYPE.CARRIER) validStation = false;
                if(noSurface && station.TYPE == STATIONTYPE.SURFACE) validStation = false;
                if(amount > station.QUANTITY) validStation = false;

                if(validStation) filteredStations.add(station);
            }
            filteredPrices.put(commodity.getKey(), filteredStations);
        }

        return filteredPrices;
    }

    private static ArrayList<COMMODITY> getTrades(Map<String, ArrayList<STATION>> sellPrices, Map<String, ArrayList<STATION>> buyPrices, int minProfit)
    {
        ArrayList<COMMODITY> trades = new ArrayList<>();

        for(Map.Entry<String, ArrayList<STATION>> commodity : sellPrices.entrySet())
        {
            COMMODITY commodityTrade = new COMMODITY(commodity.getKey(), commodity.getValue(), buyPrices.get(commodity.getKey()));
            commodityTrade.sortPrices();

            if(commodityTrade.profit >= minProfit) trades.add(commodityTrade);
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