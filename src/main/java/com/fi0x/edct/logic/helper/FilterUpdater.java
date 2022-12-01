package com.fi0x.edct.logic.helper;

import com.fi0x.edct.gui.controller.Filters;
import com.fi0x.edct.gui.controller.Interaction;
import com.fi0x.edct.gui.controller.Results;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.filesystem.BlacklistHandler;
import com.fi0x.edct.logic.structures.COMMODITY;
import com.fi0x.edct.logic.structures.FILTEROPTIONS;
import com.fi0x.edct.logic.structures.PADSIZE;
import com.fi0x.edct.logic.structures.TRADE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FilterUpdater
{

    public static void updateFilters(Interaction interactionController, Results resultsController)
    {
        ArrayList<String> blacklist = BlacklistHandler.getBlacklistSystems();
        Filters filters = Filters.getInstance();
        if(filters == null) return;
        FILTEROPTIONS filteroptions = filters.getFilterSettings();

        Map<String, ArrayList<TRADE>> filteredExportPrices = applyFilters(filteroptions, blacklist, interactionController.exportPrices);
        if(filteroptions.demand) filteroptions.amount = 0;
        Map<String, ArrayList<TRADE>> filteredImportPrices = applyFilters(filteroptions, blacklist, interactionController.importPrices);

        resultsController.setTrades(getTrades(filteredImportPrices, filteredExportPrices));

        resultsController.displayResults();
    }

    private static Map<String, ArrayList<TRADE>> applyFilters(FILTEROPTIONS filteroptions, ArrayList<String> blacklist, Map<String, ArrayList<TRADE>> inputPrices)
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

    private static ArrayList<COMMODITY> getTrades(Map<String, ArrayList<TRADE>> importPrices, Map<String, ArrayList<TRADE>> exportPrices)
    {
        ArrayList<COMMODITY> trades = new ArrayList<>();

        for(Map.Entry<String, ArrayList<TRADE>> commodity : importPrices.entrySet())
        {
            long galAvg = DBHandler.getCommodityAverage(commodity.getKey());
            COMMODITY commodityTrade = new COMMODITY(commodity.getKey(), commodity.getValue(), exportPrices.get(commodity.getKey()), galAvg);
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
