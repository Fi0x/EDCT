package com.fi0x.edct.dbconnection;

import com.fi0x.edct.MainWindow;
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

    private final int TYPE;
    private Map<String, Map.Entry<String, Integer>> commodities;

    public RequestThread(Interaction intController, int type)
    {
        INT_CONTROLLER = intController;
        TYPE = type;
    }

    @Override
    public void run()
    {
        switch(TYPE)
        {
            case 0:
                setCommodities(InaraCalls.getAllCommodities(false));
                INT_CONTROLLER.storageController.btnStart.setVisible(true);
                break;
            case 1:
                int tries = 3;
                while(tries > 0 && (commodities == null || commodities.size() == 0))
                {
                    tries--;
                    wait(1000);
                    setCommodities(InaraCalls.getAllCommodities(false));
                }
                if(commodities == null)
                {
                    INT_CONTROLLER.storageController.btnStart.setVisible(true);
                    break;
                }

                requestPrices();
                //TODO: Add updater thread that iterates through stored files and makes HTTP requests if it finds a file that is older than 1h (waits 10s before each request)
                break;
        }
    }

    private void requestPrices()
    {
        INT_CONTROLLER.sellPrices = new HashMap<>();
        INT_CONTROLLER.buyPrices = new HashMap<>();

        int i = 0;
        for(Map.Entry<String, Map.Entry<String, Integer>> entry : commodities.entrySet())
        {
            i++;
            if(entry.getValue().getValue() >= INT_CONTROLLER.filterController.getMinProfit())
            {
                try
                {
                    ArrayList<STATION> tmp = InaraCalls.getCommodityPrices(entry.getKey(), true, false);
                    if(tmp != null) INT_CONTROLLER.sellPrices.put(entry.getValue().getKey(), tmp);

                    tmp = InaraCalls.getCommodityPrices(entry.getKey(), false, false);
                    if(tmp != null) INT_CONTROLLER.buyPrices.put(entry.getValue().getKey(), tmp);
                } catch(HttpRetryException e)
                {
                    if(e.responseCode() == 429)
                    {
                        Out.newBuilder("Received a 429 response code. Please wait a while.").always().WARNING().print();
                        break;
                    }
                }

            } else Out.newBuilder("Skipped commodity " + i + " because of too low profit").verbose().print();
        }

        Platform.runLater(() ->
        {
            INT_CONTROLLER.filterController.updateFilters();
            INT_CONTROLLER.storageController.btnStart.setVisible(true);
        });
    }

    private void setCommodities(Map<String, Map.Entry<String, Integer>> newCommodities)
    {
        commodities = newCommodities;
        if(newCommodities == null || commodities.size() == 0) return;

        try
        {
            FileWriter writer = new FileWriter(MainWindow.commodityList.toString());

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
