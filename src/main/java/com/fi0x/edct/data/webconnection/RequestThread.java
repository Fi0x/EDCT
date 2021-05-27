package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.Main;
import com.fi0x.edct.controller.Interaction;
import com.fi0x.edct.data.structures.STATION;
import javafx.application.Platform;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class RequestThread implements Runnable
{
    private final Interaction INT_CONTROLLER;

    @Deprecated
    private final boolean FORCE;
    private Map<String, Map.Entry<String, Integer>> commodities;
    public long oldestFileAge;

    public RequestThread(Interaction intController, boolean force)
    {
        INT_CONTROLLER = intController;
        FORCE = force;
    }

    @Override
    public void run()
    {
        int tries = 3;
        while(tries > 0 && (commodities == null || commodities.size() == 0))
        {
            tries--;
            wait1();
            setCommodities(InaraCalls.getAllCommodities());
        }
        if(commodities == null)
        {
            INT_CONTROLLER.storageController.btnStart.setVisible(true);
            return;
        }

        requestPrices();

        Platform.runLater(() ->
        {
            INT_CONTROLLER.filterController.updateFilters();
            INT_CONTROLLER.storageController.btnStart.setVisible(true);
            INT_CONTROLLER.storageController.setDataAge(oldestFileAge, true);
        });
    }

    private void requestPrices()
    {
        INT_CONTROLLER.sellPrices = new HashMap<>();
        INT_CONTROLLER.buyPrices = new HashMap<>();

        oldestFileAge = 0;

        for(Map.Entry<String, Map.Entry<String, Integer>> entry : commodities.entrySet())
        {
            try
            {
                ArrayList<STATION> tmp = InaraCalls.getCommodityPrices(this, entry.getKey(), true, FORCE);
                if(tmp != null) INT_CONTROLLER.sellPrices.put(entry.getValue().getKey(), tmp);

                tmp = InaraCalls.getCommodityPrices(this, entry.getKey(), false, FORCE);
                if(tmp != null) INT_CONTROLLER.buyPrices.put(entry.getValue().getKey(), tmp);
            } catch(HttpRetryException e)
            {
                if(e.responseCode() == 429)
                {
                    break;
                }
            }
        }
    }

    @Deprecated
    private void setCommodities(Map<String, Map.Entry<String, Integer>> newCommodities)
    {
        commodities = newCommodities;
        if(newCommodities == null || commodities.size() == 0) return;

        try
        {
            FileWriter writer = new FileWriter(Main.commodityList.toString());

            for(Map.Entry<String, Map.Entry<String, Integer>> entry : commodities.entrySet())
            {
                String commodityEntry = entry.getKey() + "___" + entry.getValue().getKey() + "___" + entry.getValue().getValue();
                writer.write(commodityEntry + "\n");
            }

            writer.close();
        } catch(IOException ignored)
        {
        }
    }

    @Deprecated
    private void wait1()
    {
        try
        {
            Thread.sleep(1000);
        } catch(InterruptedException ignored)
        {
        }
    }
}
