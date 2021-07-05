package com.fi0x.edct.data.websites;

import com.fi0x.edct.data.RequestHandler;
import com.fi0x.edct.data.cleanup.HTMLCleanup;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class InaraStation
{
    @Nullable
    public static String getInaraStationID(String stationName, String systemName) throws InterruptedException
    {
        Map<String, String> parameters = getRefinedParameters(ENDPOINTS.StationSearch.parameter, stationName);

        String html = RequestHandler.sendHTTPRequest(ENDPOINTS.StationSearch.url, ENDPOINTS.StationSearch.type, parameters);

        if(html == null) return null;

        return HTMLCleanup.getStationID(html, stationName, systemName);
    }

    public static String getStationHtml(String stationID) throws InterruptedException
    {
        Map<String, String> parameters = new HashMap<>();

        return RequestHandler.sendHTTPRequest(ENDPOINTS.StationSearch.url + stationID, ENDPOINTS.StationSearch.type, parameters);
    }

    private static Map<String, String> getRefinedParameters(String[] parameter, String stationName)
    {
        Map<String, String> parameters = new HashMap<>();

        if(parameter.length > 0 && parameter[0].equals("search"))
        {
            parameters.put(parameter[0], stationName);
        }

        return parameters;
    }
}