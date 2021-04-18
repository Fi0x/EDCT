package com.fi0x.edct.dbconnection;

import com.fi0x.edct.util.Out;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InaraCalls
{
    @Nullable
    public static Map<String, String> getAllCommodities()
    {
        Map<String, String> parameters = new HashMap<>();

        try
        {
            String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Commodities.url, ENDPOINTS.Commodities.type, parameters);
            return HTMLCleanup.getCommodityIDs(html);
        } catch(IOException ignored)
        {
            Out.newBuilder("Could not get commodity-list").always().ERROR().print();
        }
        return null;
    }

    public static ArrayList<String[]> getCommodityPrices(String commodityRefID, boolean sell)
    {
        Map<String, String> parameters = new HashMap<>();
        for(String param : ENDPOINTS.Prices.parameter)
        {
            switch(param)
            {
                case "act":
                    parameters.put(param, "goodsdata");
                    break;
                case "refname":
                    parameters.put(param, sell ? "sellmax" : "buymin");
                    break;
                case "refid":
                    parameters.put(param, commodityRefID);
                    break;
                case "refid2":
                    parameters.put(param, "1261");
                    break;
                default:
                    break;
            }
        }

        try
        {
            String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Prices.url, ENDPOINTS.Prices.type, parameters);
            System.out.println(html);
        } catch(IOException ignored)
        {
        }

        //TODO: Return array with commodity information (station, pad-size, quantity, sell/buy-price)
        return null;
    }
}