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
        Map<String, String> exportStationParameters = getRefinedParameters(ENDPOINTS.Prices.parameter, commodityRefID, "1");
        Map<String, String> importStationParameters = getRefinedParameters(ENDPOINTS.Prices.parameter, commodityRefID, "2");

        String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Prices.url, ENDPOINTS.Prices.type, exportStationParameters);
        if(html == null) return false;
        ArrayList<TRADE> exportStations = INARACleanup.getCommodityPrices(commodityRefID, html, true);

        html = RequestHandler.sendHTTPRequest(ENDPOINTS.Prices.url, ENDPOINTS.Prices.type, importStationParameters);
        if(html == null) return false;
        ArrayList<TRADE> importStations = INARACleanup.getCommodityPrices(commodityRefID, html, false);

        for(TRADE exporter : exportStations)
        {
            STATION s = DBHandler.getStation(exporter.STATION.SYSTEM, exporter.STATION.NAME);
            if(s == null)
            {
                s = new STATION(exporter.STATION.SYSTEM, exporter.STATION.NAME, exporter.STATION.PAD, exporter.STATION.TYPE, exporter.STATION.DISTANCE_TO_STAR);
                DBHandler.setStationData(s);
                if(DBHandler.getSystemCoords(exporter.STATION.SYSTEM) == null)
                {
                    StationUpdater.addSystemToQueue(exporter.STATION.SYSTEM);
                }
            }

            TRADE t = new TRADE(s, commodityRefID, exporter.AGE, exporter.SUPPLY, 0, 0, exporter.EXPORT_PRICE);
            DBHandler.setTradeData(t);
        }
        for(TRADE importer : importStations)
        {
            STATION s = DBHandler.getStation(importer.STATION.SYSTEM, importer.STATION.NAME);
            if(s == null)
            {
                s = new STATION(importer.STATION.SYSTEM, importer.STATION.NAME, importer.STATION.PAD, importer.STATION.TYPE, importer.STATION.DISTANCE_TO_STAR);
                DBHandler.setStationData(s);
                if(DBHandler.getSystemCoords(importer.STATION.SYSTEM) == null)
                {
                    StationUpdater.addSystemToQueue(importer.STATION.SYSTEM);
                }
            }

            TRADE t = new TRADE(s, commodityRefID, importer.AGE, 0, importer.DEMAND, importer.IMPORT_PRICE, 0);
            DBHandler.setStationData(s);
            DBHandler.setTradeData(t);
        }

        DBHandler.updateDownloadTime(commodityRefID);
        return true;
    }

    private static Map<String, String> getRefinedParameters(String[] parameter, int commRefID, String importExportParam)
    {
        Map<String, String> parameters = new HashMap<>();

        for(String param : parameter)
        {
            switch(param)
            {
                case "pi1":
                    parameters.put(param, importExportParam);
                    break;
                case "pi2":
                    parameters.put(param, String.valueOf(commRefID));
                    break;
                case "pi3":
                case "pi4":
                case "pi8":
                case "pi10":
                    parameters.put(param, "1");
                    break;
                case "pi7":
                case "pi12":
                    parameters.put(param, "0");
                    break;
                case "pi5":
                    parameters.put(param, "168");
                    break;
                case "pi9":
                    parameters.put(param, "100000");
                    break;
                case "pi11":
                    parameters.put(param, "5000");
                    break;
                default:
                    break;
            }
        }

        return parameters;
    }
}
