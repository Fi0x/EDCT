package com.fi0x.edct.dbconnection;

import com.fi0x.edct.controller.ControllerMain;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.util.Out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestThread implements Runnable
{
    private final int TYPE;
    private final ControllerMain CONTROLLER;

    public RequestThread(ControllerMain controller, int type)
    {
        CONTROLLER = controller;
        TYPE = type;
    }

    @Override
    public void run()
    {
        switch(TYPE)
        {
            case 0:
                CONTROLLER.commodities = InaraCalls.getAllCommodities();
                CONTROLLER.btnStart.setVisible(true);
                break;
            case 1:
                int tries = 3;
                while(tries > 0 && (CONTROLLER.commodities == null || CONTROLLER.commodities.size() == 0))
                {
                    tries--;
                    wait(1000);
                    CONTROLLER.commodities = InaraCalls.getAllCommodities();
                }
                if(CONTROLLER.commodities == null)
                {
                    CONTROLLER.btnStart.setVisible(true);
                    break;
                }

                requestPrices();
                break;
        }
    }

    private void requestPrices()
    {
        CONTROLLER.sellPrices = new HashMap<>();
        CONTROLLER.buyPrices = new HashMap<>();

        int i = 0;
        for(Map.Entry<String, String> entry : CONTROLLER.commodities.entrySet())
        {
            ArrayList<STATION> tmp = InaraCalls.getCommodityPrices(entry.getKey(), true);
            if(tmp != null) CONTROLLER.sellPrices.put(entry.getValue(), tmp);

            tmp = InaraCalls.getCommodityPrices(entry.getKey(), false);
            if(tmp != null) CONTROLLER.buyPrices.put(entry.getValue(), tmp);

            wait(500);
            i++;
            Out.newBuilder("Downloaded data for " + i + "/" + CONTROLLER.commodities.size() + " commodities").always().print();
        }

        CONTROLLER.updateFilters();
        CONTROLLER.btnStart.setVisible(true);
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
