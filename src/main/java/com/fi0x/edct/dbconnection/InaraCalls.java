package com.fi0x.edct.dbconnection;

import com.fi0x.edct.datastructures.ENDPOINTS;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.util.Out;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InaraCalls
{
    @Nullable
    public static Map<String, Map.Entry<String, Integer>> getAllCommodities()
    {
        Map<String, String> parameters = new HashMap<>();

        try
        {
            if(true)//TODO: Only request from server if local file is older than 1h (add GUI option to update local file (Button))
            {
                String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Commodities.url, ENDPOINTS.Commodities.type, parameters);
                return HTMLCleanup.getCommodityIDs(html);
            } else
            {
                //TODO: Read commodities from file
            }
        } catch(Exception ignored)
        {
            Out.newBuilder("Could not get commodity-list").always().ERROR().print();
        }
        return null;
    }

    @Nullable
    public static ArrayList<STATION> getCommodityPrices(String commodityRefID, boolean sell) throws HttpRetryException
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
            return HTMLCleanup.getCommodityPrices(html);
        } catch(HttpRetryException e)
        {
            throw e;
        } catch(IOException ignored)
        {
            Out.newBuilder("Could not get commodity-prices for " + commodityRefID).always().ERROR().print();
        }
        return null;
    }
}