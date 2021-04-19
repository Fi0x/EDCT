package com.fi0x.edct.controller;

import com.fi0x.edct.dbconnection.InaraCalls;
import com.fi0x.edct.dbconnection.STATION;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.Map;

public class ControllerMain
{
    private int minCargo = 1000;

    @FXML
    private void calculate()
    {
        Map<String, String> commodities = InaraCalls.getAllCommodities();

        for(Map.Entry<String, String> entry : commodities.entrySet())
        {
            ArrayList<STATION> sellPrices = InaraCalls.getCommodityPrices(entry.getKey(), true);
            ArrayList<STATION> buyPrices = InaraCalls.getCommodityPrices(entry.getKey(), false);

            //TODO: Filter out stations that do not match search criteria (pad-size, quantity)
        }
    }

    @FXML
    private void test()
    {
        InaraCalls.getCommodityPrices("10268", true);
    }
}