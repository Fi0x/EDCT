package com.fi0x.edct.data.localstorage;

import com.fi0x.edct.controller.Interaction;
import com.fi0x.edct.data.structures.STATION;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;

public class TradeReloader implements Runnable
{
    private final Interaction INT_CONTROLLER;

    public TradeReloader(Interaction interaction)
    {
        INT_CONTROLLER = interaction;
    }

    @Override
    public void run()
    {
        updatePrices();

        long oldestFileAge = DBHandler.getInstance().getOldestUpdateAge();
        Platform.runLater(() ->
        {
            INT_CONTROLLER.filterController.updateFilters();
            INT_CONTROLLER.storageController.setDataAge(oldestFileAge, false);
        });
    }

    private void updatePrices()
    {
        INT_CONTROLLER.sellPrices = new HashMap<>();
        INT_CONTROLLER.buyPrices = new HashMap<>();

        for(int id : DBHandler.getInstance().getCommodityIDs(false))
        {
            String commodityName = DBHandler.getInstance().getCommodityNameByID(id);

            ArrayList<STATION> tmp = DBHandler.getInstance().getCommodityInformation(id, true);
            INT_CONTROLLER.sellPrices.put(commodityName, tmp);

            tmp = DBHandler.getInstance().getCommodityInformation(id, false);
            INT_CONTROLLER.buyPrices.put(commodityName, tmp);
        }
    }
}
