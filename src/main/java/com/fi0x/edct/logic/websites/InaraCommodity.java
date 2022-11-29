package com.fi0x.edct.logic.websites;

import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logging.exceptions.HtmlConnectionException;
import com.fi0x.edct.logic.cleanup.INARACleanup;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.structures.ENDPOINTS;
import com.fi0x.edct.logic.structures.STATION;
import com.fi0x.edct.logic.structures.TRADE;
import com.fi0x.edct.logic.threads.StationUpdater;
import com.fi0x.edct.logic.webrequests.RequestHandler;
import io.fi0x.javalogger.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InaraCommodity
{
    public static boolean updateCommodityIDs() throws InterruptedException
    {
        Map<String, String> parameters = new HashMap<>();
        Map<String, Integer> commodities;
        String html = null;

        int counter = 0;
        while(counter < 3)
        {
            counter++;
            try
            {
                html = RequestHandler.sendHTTPRequest(ENDPOINTS.Commodities.url, ENDPOINTS.Commodities.type, parameters);
                break;
            } catch(HtmlConnectionException ignored)
            {
            }
        }

        if(html == null) return false;
        commodities = INARACleanup.getCommodityIDs(html);

        Logger.log("Received " + commodities.size() + " commodities from inara", LogName.VERBOSE);
        for(Map.Entry<String, Integer> entry : commodities.entrySet())
        {
            DBHandler.setCommodityData(entry.getKey(), entry.getValue());
        }
        return true;
    }

    public static boolean updateCommodityPrices(int commodityRefID) throws InterruptedException, HtmlConnectionException
    {
        Map<String, String> parameters1 = getRefinedParameters(ENDPOINTS.Prices.parameter, commodityRefID, "buymin");
        Map<String, String> parameters2 = getRefinedParameters(ENDPOINTS.Prices.parameter, commodityRefID, "sellmax");

        String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Prices.url + commodityRefID + "/#tab_ui-id-2", ENDPOINTS.Prices.type, parameters1);
        if(html == null) return false;
        ArrayList<TRADE> sellStations = INARACleanup.getCommodityPrices(commodityRefID, html, true);
        html = RequestHandler.sendHTTPRequest(ENDPOINTS.Prices.url + commodityRefID + "/#tab_ui-id-8", ENDPOINTS.Prices.type, parameters2);
        if(html == null) return false;
        ArrayList<TRADE> buyStations = INARACleanup.getCommodityPrices(commodityRefID, html, false);

        for(TRADE seller : sellStations)
        {
            STATION s = DBHandler.getStation(seller.STATION.SYSTEM, seller.STATION.NAME);
            if(s == null)
            {
                s = new STATION(seller.STATION.SYSTEM, seller.STATION.NAME, seller.STATION.PAD, seller.STATION.TYPE, seller.STATION.DISTANCE_TO_STAR);
                DBHandler.setStationData(s);
                if(DBHandler.getSystemCoords(seller.STATION.SYSTEM) == null)
                {
                    StationUpdater.addSystemToQueue(seller.STATION.SYSTEM);
                }
            }

            TRADE t = new TRADE(s, commodityRefID, seller.AGE, seller.SUPPLY, 0, 0, seller.SELL_PRICE);
            DBHandler.setTradeData(t);
        }
        for(TRADE buyer : buyStations)
        {
            STATION s = DBHandler.getStation(buyer.STATION.SYSTEM, buyer.STATION.NAME);
            if(s == null)
            {
                s = new STATION(buyer.STATION.SYSTEM, buyer.STATION.NAME, buyer.STATION.PAD, buyer.STATION.TYPE, buyer.STATION.DISTANCE_TO_STAR);
                DBHandler.setStationData(s);
                if(DBHandler.getSystemCoords(buyer.STATION.SYSTEM) == null)
                {
                    StationUpdater.addSystemToQueue(buyer.STATION.SYSTEM);
                }
            }

            TRADE t = new TRADE(s, commodityRefID, buyer.AGE, 0, buyer.DEMAND, buyer.BUY_PRICE, 0);
            DBHandler.setStationData(s);
            DBHandler.setTradeData(t);
        }

        DBHandler.updateDownloadTime(commodityRefID);
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
