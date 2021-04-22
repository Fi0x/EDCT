package com.fi0x.edct.dbconnection;

import com.fi0x.edct.controller.Interaction;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.util.Out;
import javafx.application.Platform;

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
                commodities = InaraCalls.getAllCommodities();
                INT_CONTROLLER.storageController.btnStart.setVisible(true);
                break;
            case 1:
                int tries = 3;
                while(tries > 0 && (commodities == null || commodities.size() == 0))
                {
                    tries--;
                    wait(1000);
                    commodities = InaraCalls.getAllCommodities();
                }
                if(commodities == null)
                {
                    INT_CONTROLLER.storageController.btnStart.setVisible(true);
                    break;
                }

                requestPrices();
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
            if(entry.getValue().getValue() > INT_CONTROLLER.filterController.getMinProfit())
            {
                try
                {
                    ArrayList<STATION> tmp = InaraCalls.getCommodityPrices(entry.getKey(), true);
                    if(tmp != null) INT_CONTROLLER.sellPrices.put(entry.getValue().getKey(), tmp);

                    tmp = InaraCalls.getCommodityPrices(entry.getKey(), false);
                    if(tmp != null) INT_CONTROLLER.buyPrices.put(entry.getValue().getKey(), tmp);
                } catch(HttpRetryException e)
                {
                    if(e.responseCode() == 429)
                    {
                        Out.newBuilder("Received a 429 response code. Please wait a while.").always().WARNING().print();
                        break;
                    }
                }

                Out.newBuilder("Downloaded data for " + i + "/" + commodities.size() + " commodities").verbose().print();
                wait(500);
            } else Out.newBuilder("Skipped commodity " + i + " because of too low profit").verbose().print();
        }

        Platform.runLater(() ->
        {
            INT_CONTROLLER.filterController.updateFilters();
            INT_CONTROLLER.storageController.btnStart.setVisible(true);
        });
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
