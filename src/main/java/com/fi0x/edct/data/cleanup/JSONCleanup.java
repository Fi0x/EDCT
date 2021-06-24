package com.fi0x.edct.data.cleanup;

import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.data.structures.STATIONTYPE;
import com.fi0x.edct.util.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JSONCleanup
{
    public static Map<String, String> getReleases(String jsonString)
    {
        Map<String, String> releaseDates = new HashMap<>();

        try
        {
            JSONArray jsonReleases = (JSONArray) new JSONParser().parse(jsonString);
            for(Object release : jsonReleases)
            {
                if((boolean) ((JSONObject) release).get("prerelease")) continue;
                if((boolean) ((JSONObject) release).get("draft")) continue;

                String tag = ((JSONObject) release).get("tag_name").toString();
                String url = ((JSONObject) release).get("html_url").toString();
                releaseDates.put(tag, url);
            }
        } catch(ParseException e)
        {
            Logger.WARNING("Could not convert release-json: " + jsonString, e);
        }

        return releaseDates;
    }

    @Nullable
    public static String getStationName(String jsonString)
    {
        try
        {
            JSONObject trade = (JSONObject) new JSONParser().parse(jsonString);
            JSONObject message = (JSONObject) trade.get("message");
            return (String) message.get("stationName");
        } catch(ParseException e)
        {
            Logger.WARNING("Could not retrieve a station name from a JSON string");
        }
        return null;
    }
    @Nullable
    public static String getSystemName(String jsonString)
    {
        try
        {
            JSONObject trade = (JSONObject) new JSONParser().parse(jsonString);
            JSONObject message = (JSONObject) trade.get("message");
            return (String) message.get("systemName");
        } catch(ParseException e)
        {
            Logger.WARNING("Could not retrieve a system name from a JSON string");
        }
        return null;
    }
    public static ArrayList<String> getTrades(String jsonString)
    {
        ArrayList<String> trades = new ArrayList<>();

        try
        {
            JSONObject all = (JSONObject) new JSONParser().parse(jsonString);
            JSONObject message = (JSONObject) all.get("message");
            JSONArray commodities = (JSONArray) message.get("commodities");
            for(Object o : commodities)
            {
                trades.add(o.toString());
            }
        } catch(ParseException e)
        {
            Logger.WARNING("Something went wrong when receiving trade data from EDDN json");
        }

        return trades;
    }
    @Nullable
    public static STATION getStationTrade(String system, String station, PADSIZE pad, STATIONTYPE type, String jsonTrade, boolean isSelling)
    {
        long quantity = 0;
        long price = 0;

        try
        {
            JSONObject json = (JSONObject) new JSONParser().parse(jsonTrade);
            if(isSelling)
            {
                quantity = (long) json.get("stock");
                price = (long) json.get("buyPrice");
            }
            else
            {
                quantity = (long) json.get("demand");
                price = (long) json.get("sellPrice");
            }
        } catch(ParseException e)
        {
            Logger.WARNING("Could not get trade data from an EDDN json");
        }

        if(price == 0) return null;
        return new STATION(system, station, pad, quantity, price, type, System.currentTimeMillis());
    }
}
