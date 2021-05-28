package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.structures.ENDPOINTS;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.util.Out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Inara
{
    public static boolean updateCommodityIDs()
    {
        Map<String, String> parameters = new HashMap<>();
        Map<String, Integer> commodities;
        try
        {
            String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Commodities.url, ENDPOINTS.Commodities.type, parameters);
            if(html == null) return false;
            commodities = HTMLCleanup.getCommodityIDs(html);
        } catch(Exception ignored)
        {
            Out.newBuilder("Could not download commodity-list").always().WARNING();
            return false;
        }

        for(Map.Entry<String, Integer> entry : commodities.entrySet())
        {
            DBHandler.getInstance().setCommodityData(entry.getKey(), entry.getValue());
        }
        return true;
    }

    public static boolean updateCommodityPrices(int commodityRefID)
    {
        Map<String, String> parameters1 = getRefinedParameters(ENDPOINTS.Prices.parameter, commodityRefID, "buymin");
        Map<String, String> parameters2 = getRefinedParameters(ENDPOINTS.Prices.parameter, commodityRefID, "sellmax");

        try
        {
            String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Prices.url, ENDPOINTS.Prices.type, parameters1);
            if(html == null) return false;
            ArrayList<STATION> sellStations = HTMLCleanup.getCommodityPrices(html);
            html = RequestHandler.sendHTTPRequest(ENDPOINTS.Prices.url, ENDPOINTS.Prices.type, parameters2);
            if(html == null) return false;
            ArrayList<STATION> buyStations = HTMLCleanup.getCommodityPrices(html);

            for(STATION seller : sellStations)
            {
                DBHandler.getInstance().setStationData(seller, commodityRefID, true);
            }
            for(STATION buyer : buyStations)
            {
                DBHandler.getInstance().setStationData(buyer, commodityRefID, false);
            }

            DBHandler.getInstance().updateDownloadTime(DBHandler.getInstance().getCommodityNameByID(commodityRefID), commodityRefID);
        } catch(Exception e)
        {
            Out.newBuilder("Could not get commodity-prices for " + commodityRefID).always().ERROR();
            return false;
        }
        return true;
    }

    private static Map<String, String> getRefinedParameters(String[] parameter, int commRefID, String sellParam)
    {
        Map<String, String> parameters = new HashMap<>();

        for(String param : parameter)
        {
            switch(param)
            {
                case "act":
                    parameters.put(param, "goodsdata");
                    break;
                case "refname":
                    parameters.put(param, sellParam);
                    break;
                case "refid":
                    parameters.put(param, String.valueOf(commRefID));
                    break;
                case "refid2":
                    parameters.put(param, "1261");
                    break;
                default:
                    break;
            }
        }

        return parameters;
    }
}
