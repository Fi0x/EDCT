package com.fi0x.edct.data.websites;

import com.fi0x.edct.data.RequestHandler;
import com.fi0x.edct.data.cleanup.HTMLCleanup;
import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.structures.ENDPOINTS;

import java.util.HashMap;
import java.util.Map;

public class EDDB
{
    public static boolean updateGalacticAverages() throws InterruptedException
    {
        Map<String, String> parameters = new HashMap<>();
        String html = RequestHandler.sendHTTPRequest(ENDPOINTS.EDDNPrices.url, ENDPOINTS.EDDNPrices.type, parameters);

        if(html == null) return false;

        Map<String, Integer> averages = HTMLCleanup.getCommodityAveragePrice(html);

        for(Map.Entry<String, Integer> entry : averages.entrySet())
        {
            DBHandler.getInstance().setGalacticAverage(entry.getKey(), entry.getValue());
        }
        return true;
    }
}