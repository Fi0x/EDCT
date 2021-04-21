package com.fi0x.edct.dbconnection;

import com.fi0x.edct.controller.Interaction;
import com.fi0x.edct.controller.Main;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.util.Out;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestThread implements Runnable
{
    private final int TYPE;
    private final Interaction INT_CONTROLLER;

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
                INT_CONTROLLER.commodities = InaraCalls.getAllCommodities();
                INT_CONTROLLER.btnStart.setVisible(true);
                break;
            case 1:
                int tries = 3;
                while(tries > 0 && (INT_CONTROLLER.commodities == null || INT_CONTROLLER.commodities.size() == 0))
                {
                    tries--;
                    wait(1000);
                    INT_CONTROLLER.commodities = InaraCalls.getAllCommodities();
                }
                if(INT_CONTROLLER.commodities == null)
                {
                    INT_CONTROLLER.btnStart.setVisible(true);
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
        for(Map.Entry<String, String> entry : INT_CONTROLLER.commodities.entrySet())
        {
            ArrayList<STATION> tmp = InaraCalls.getCommodityPrices(entry.getKey(), true);
            if(tmp != null) INT_CONTROLLER.sellPrices.put(entry.getValue(), tmp);

            tmp = InaraCalls.getCommodityPrices(entry.getKey(), false);
            if(tmp != null) INT_CONTROLLER.buyPrices.put(entry.getValue(), tmp);

            wait(500);
            i++;
            Out.newBuilder("Downloaded data for " + i + "/" + INT_CONTROLLER.commodities.size() + " commodities").always().print();
        }

        Platform.runLater(() ->
        {
            INT_CONTROLLER.updateFilters();
            INT_CONTROLLER.btnStart.setVisible(true);
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
