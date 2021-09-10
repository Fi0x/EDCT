package com.fi0x.edct.logic.cleanup;

import com.fi0x.edct.logging.Logger;
import com.fi0x.edct.logic.structures.PADSIZE;
import com.fi0x.edct.logic.structures.STATION;
import com.fi0x.edct.logic.structures.STATIONTYPE;
import com.fi0x.edct.logic.structures.TRADE;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Element;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;

public class EDDNCleanup
{
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
    public static TRADE getStationTrade(int commodityID, String system, String station, PADSIZE pad, STATIONTYPE type, double starDistance, String jsonTrade, boolean isSelling)
    {
        long supply = 0;
        long demand = 0;
        long buyPrice = 0;
        long sellPrice = 0;

        try
        {
            JSONObject json = (JSONObject) new JSONParser().parse(jsonTrade);
            if(isSelling)
            {
                supply = (long) json.get("stock");
                sellPrice = (long) json.get("buyPrice");
            }
            else
            {
                demand = (long) json.get("demand");
                buyPrice = (long) json.get("sellPrice");
            }
        } catch(ParseException e)
        {
            Logger.WARNING("Could not get trade data from an EDDN json");
        }

        if(sellPrice == 0 && buyPrice == 0) return null;

        STATION s = new STATION(system, station, pad, type, starDistance);
        return new TRADE(s, commodityID, System.currentTimeMillis(), supply, demand, buyPrice, sellPrice);
    }

    @Nullable
    public static PADSIZE getStationPad(String inputHTML)
    {
        PADSIZE padsize = PADSIZE.NONE;

        Element details = HTMLCleanup.getStationDetails(inputHTML);
        if(details == null) return null;

        for(Element pair : details.getElementsByClass("itempaircontainer"))
        {
            String pairText = pair.toString().toLowerCase();
            if(pairText.contains("landing pad"))
            {
                if(pairText.contains("large")) padsize = PADSIZE.L;
                else if(pairText.contains("medium")) padsize = PADSIZE.M;
                else if(pairText.contains("small")) padsize = PADSIZE.S;
                break;
            }
        }

        return padsize;
    }

    @Nullable
    public static STATIONTYPE getStationType(String inputHTML)
    {
        STATIONTYPE type = STATIONTYPE.UNKNOWN;

        Element details = HTMLCleanup.getStationDetails(inputHTML);
        if(details == null) return null;

        for(Element pair : details.getElementsByClass("itempaircontainer"))
        {
            if(pair.toString().toLowerCase().contains("station type"))
            {
                String typeName = Objects.requireNonNull(pair.getElementsByClass("itempairvalue").first()).ownText().toLowerCase();
                if(typeName.contains("odyssey")) type = STATIONTYPE.ODYSSEY;
                else if(typeName.contains("fleet") || typeName.contains("carrier")) type = STATIONTYPE.CARRIER;
                else if(typeName.contains("surface")) type = STATIONTYPE.SURFACE;
                else if(typeName.contains("starport") ||
                        typeName.contains("outpost") ||
                        typeName.contains("asteroid") ||
                        typeName.contains("megaship")) type = STATIONTYPE.ORBIT;
            }
        }

        return type;
    }

    public static double getStarDistance(String inputHTML)
    {
        double starDistance = -1;

        Element details = HTMLCleanup.getStationDetails(inputHTML);
        if(details == null) return -1;

        for(Element pair : details.getElementsByClass("itempaircontainer"))
        {
            if(pair.toString().toLowerCase().contains("station distance"))
            {
                String distanceText = Objects.requireNonNull(pair.getElementsByClass("itempairvalue").first()).ownText();
                starDistance = Double.parseDouble(distanceText.replace(",", "").replace(" Ls", "").replace("-", ""));
            }
        }

        return starDistance;
    }
}
