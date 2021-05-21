package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.Main;
import com.fi0x.edct.controller.Interaction;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.util.Out;
import javafx.application.Platform;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestThread implements Runnable
{
    private final Interaction INT_CONTROLLER;

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
            wait(1000);
            setCommodities(InaraCalls.getAllCommodities());
        }
        if(commodities == null)
        {
            INT_CONTROLLER.storageController.btnStart.setVisible(true);
            return;
        }

        requestPrices();
    }

    private void requestPrices()
    {
        INT_CONTROLLER.sellPrices = new HashMap<>();
        INT_CONTROLLER.buyPrices = new HashMap<>();

        oldestFileAge = 0;

        int i = 0;
        for(Map.Entry<String, Map.Entry<String, Integer>> entry : commodities.entrySet())
        {
            i++;
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
                    Out.newBuilder("Received a 429 response code. Please wait a while.").always().WARNING().print();
                    break;
                }
            }
            Out.newBuilder("Updated file " + i + "/" + commodities.size()).veryVerbose().SUCCESS().print();
        }

        Platform.runLater(() ->
        {
            INT_CONTROLLER.filterController.updateFilters();
            INT_CONTROLLER.storageController.btnStart.setVisible(true);
            INT_CONTROLLER.storageController.setDataAge(oldestFileAge);
        });
    }

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
            Out.newBuilder("Successfully wrote commodity-entries to file").verbose().SUCCESS().print();
        } catch(IOException e)
        {
            Out.newBuilder("Something went wrong when writing commodity data to local storage").debug().ERROR().print();
        }
    }

    private void wait(int millis)
    {
        try
        {
            Thread.sleep(millis);
        } catch(InterruptedException ignored)
        {
        }
    }
}