package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.structures.ENDPOINTS;
import com.fi0x.edct.util.Out;

import java.util.HashMap;
import java.util.Map;

public class Inara
{
    public static void updateCommodityIDs()
    {
        Map<String, String> parameters = new HashMap<>();
        Map<String, Integer> commodities;
        try
        {
            String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Commodities.url, ENDPOINTS.Commodities.type, parameters);
            commodities = HTMLCleanup.getCommodityIDs(html);
            Out.newBuilder("Commodity list loaded from INARA").verbose().SUCCESS().print();
        } catch(Exception ignored)
        {
            Out.newBuilder("Could not download commodity-list").always().WARNING().print();
            return;
        }

        for(Map.Entry<String, Integer> entry : commodities.entrySet())
        {
            DBHandler.getInstance().setCommodityData(entry.getKey(), entry.getValue());
        }
    }
}
