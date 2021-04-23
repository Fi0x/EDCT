package com.fi0x.edct.dbconnection;

import com.fi0x.edct.MainWindow;
import com.fi0x.edct.datastructures.ENDPOINTS;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.util.Out;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.net.HttpRetryException;
import java.util.*;

public class InaraCalls
{
    public static Map<String, Map.Entry<String, Integer>> getAllCommodities(boolean fromServer)
    {
        Map<String, String> parameters = new HashMap<>();
        Map<String, Map.Entry<String, Integer>> commodities = new HashMap<>();

        try
        {
            long ageInMillis = System.currentTimeMillis() - MainWindow.commodityList.lastModified();
            Scanner fileReader = new Scanner(MainWindow.commodityList);
            if(fromServer || ageInMillis > 3600000 || !fileReader.hasNextLine())
            {
                String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Commodities.url, ENDPOINTS.Commodities.type, parameters);
                commodities =  HTMLCleanup.getCommodityIDs(html);
                Out.newBuilder("Commodity list loaded from INARA").verbose().SUCCESS().print();
            } else
            {
                while(fileReader.hasNextLine())
                {
                    String line = fileReader.nextLine();
                    String[] parts = line.split("___");
                    if(parts.length != 3) continue;

                    commodities.put(parts[0], new AbstractMap.SimpleEntry<>(parts[1], Integer.parseInt(parts[2])));
                }
                Out.newBuilder("Commodity list loaded from local file").verbose().SUCCESS().print();
            }
        } catch(Exception ignored)
        {
            Out.newBuilder("Could not get commodity-list").always().ERROR().print();
        }

        return commodities;
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