package com.fi0x.edct.util;

import com.fi0x.edct.MainWindow;
import com.fi0x.edct.data.cleanup.EDDNCleanup;
import com.fi0x.edct.data.localstorage.NameMap;
import com.fi0x.edct.data.localstorage.db.DBHandler;
import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.data.structures.STATIONTYPE;
import com.fi0x.edct.data.structures.TRADE;
import com.fi0x.edct.data.websites.EDSM;
import com.fi0x.edct.data.websites.InaraStation;
import com.sun.javafx.geom.Vec3d;
import javafx.application.Platform;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Map;

public class EDDNHandler implements Runnable
{
    private static EDDNHandler instance;
    private static final ArrayList<String> QUEUE = new ArrayList<>();

    private EDDNHandler()
    {
    }
    public static EDDNHandler getInstance()
    {
        if(instance == null) instance = new EDDNHandler();
        return instance;
    }

    @Override
    public void run()
    {
        while(!Thread.interrupted())
        {
            if(QUEUE.size() > 0)
            {
                retrieveStationInterrupted(QUEUE.get(0));
                QUEUE.remove(0);
            }
        }
    }

    public static void addToQueue(String s)
    {
        QUEUE.add(s);
    }

    private void retrieveStationInterrupted(String outputString)
    {
        Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(true));
        String stationName = EDDNCleanup.getStationName(outputString);
        String systemName = EDDNCleanup.getSystemName(outputString);
        if(stationName == null || systemName == null)
        {
            Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(false));
            return;
        }

        PADSIZE padsize;
        STATIONTYPE stationtype;

        STATION station = DBHandler.getStation(systemName, stationName);

        if(station == null)
        {
            String html;
            try
            {
                if(DBHandler.getSystemCoords(systemName) == null)
                {
                    Vec3d coordinates = EDSM.getSystemCoordinates(systemName);
                    if(coordinates != null) DBHandler.setSystemCoordinates(systemName, coordinates);
                }

                String stationID = InaraStation.getInaraStationID(stationName, systemName);
                if(stationID == null)
                {
                    Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(false));
                    return;
                }

                html = InaraStation.getStationHtml(stationID);
                if(html == null)
                {
                    Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(false));
                    return;
                }
            } catch(InterruptedException ignored)
            {
                return;
            }

            padsize = EDDNCleanup.getStationPad(html);
            stationtype = EDDNCleanup.getStationType(html);

            if(stationtype == null || padsize == null)
            {
                Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(false));
                Logger.WARNING("Aborted station update for " + stationName + " type=" + stationtype + " pad=" + padsize + " - html: " + html);
                return;
            }
        } else
        {
            padsize = station.PAD;
            stationtype = station.TYPE;
        }

        for(String trade : EDDNCleanup.getTrades(outputString))
        {
            int commodityID = getInaraIDForCommodity(trade);
            if(commodityID == -1) continue;


            TRADE station_old = EDDNCleanup.getStationTrade(commodityID, systemName, stationName, padsize, stationtype, trade, false);
            if(station_old != null)
            {
                STATION s = DBHandler.getStation(systemName, stationName);
                if(s == null)
                {
                    s = new STATION(systemName, stationName, padsize, stationtype);
                    DBHandler.setStationData(s);
                }
                TRADE t = new TRADE(s, commodityID, station_old.AGE, 0, station_old.DEMAND, station_old.BUY_PRICE, 0);
                DBHandler.setTradeData(t);
            }

            station_old = EDDNCleanup.getStationTrade(commodityID, systemName, stationName, padsize, stationtype, trade, true);
            if(station_old != null)
            {
                STATION s = DBHandler.getStation(systemName, stationName);
                if(s == null)
                {
                    s = new STATION(systemName, stationName, padsize, stationtype);
                    DBHandler.setStationData(s);
                }
                TRADE t = new TRADE(s, commodityID, station_old.AGE, station_old.SUPPLY, 0, 0, station_old.SELL_PRICE);
                DBHandler.setTradeData(t);
            }
        }
        Platform.runLater(() ->
        {
            MainWindow.getInstance().interactionController.storageController.setDataAge(-1);
            MainWindow.getInstance().interactionController.storageController.setEDDNStatus(false);
        });
    }

    private int getInaraIDForCommodity(String commodityJSON)
    {
        String commodityName;
        try
        {
            JSONObject json = (JSONObject) new JSONParser().parse(commodityJSON);
            commodityName = ((String) json.get("name")).toLowerCase();
        } catch(ParseException e)
        {
            Logger.WARNING("Could not parse an EDDN json for a commodity");
            return -1;
        }

        Map<String, Integer> pairs = DBHandler.getCommodityNameIDPairs();
        for(Map.Entry<String, Integer> pair : pairs.entrySet())
        {
            String dbName = NameMap.convertDBToEDDN(pair.getKey())
                    .replace(" ", "")
                    .replace("-", "")
                    .toLowerCase();

            if(commodityName.equals(dbName)) return pair.getValue();
        }

        if(!NameMap.isRare(commodityName) && !NameMap.isIgnored(commodityName)) Logger.WARNING(991, "Could not find commodity key that matches: " + commodityName);
        return -1;
    }
}
