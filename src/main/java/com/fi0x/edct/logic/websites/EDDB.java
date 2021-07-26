package com.fi0x.edct.logic.websites;

import com.fi0x.edct.logging.exceptions.HtmlConnectionException;
import com.fi0x.edct.logic.cleanup.EDDBCleanup;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.structures.ENDPOINTS;
import com.fi0x.edct.logic.webrequests.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class EDDB
{
    public static boolean updateGalacticAverages() throws InterruptedException
    {
        Map<String, String> parameters = new HashMap<>();
        String html = null;
        int counter = 0;
        while(counter < 3)
        {
            counter++;
            try
            {
                html = RequestHandler.sendHTTPRequest(ENDPOINTS.EDDNPrices.url, ENDPOINTS.EDDNPrices.type, parameters);
                break;
            } catch(HtmlConnectionException ignored)
            {
            }
        }

        if(html == null) return false;

        Map<String, Integer> averages = EDDBCleanup.getCommodityAveragePrice(html);

        for(Map.Entry<String, Integer> entry : averages.entrySet())
        {
            DBHandler.setGalacticAverage(entry.getKey(), entry.getValue());
        }
        return true;
    }
}