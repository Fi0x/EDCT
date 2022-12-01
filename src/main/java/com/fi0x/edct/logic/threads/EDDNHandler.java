package com.fi0x.edct.logic.threads;

import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logging.exceptions.HtmlConnectionException;
import com.fi0x.edct.logic.NameMap;
import com.fi0x.edct.logic.cleanup.EDDNCleanup;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.structures.PADSIZE;
import com.fi0x.edct.logic.structures.STATION;
import com.fi0x.edct.logic.structures.STATIONTYPE;
import com.fi0x.edct.logic.structures.TRADE;
import com.fi0x.edct.logic.websites.EDSM;
import com.fi0x.edct.logic.websites.InaraStation;
import com.sun.javafx.geom.Vec3d;
import io.fi0x.javalogger.logging.Logger;
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
        double starDistance;

        STATION station = DBHandler.getStation(systemName, stationName);

        if(station == null)
        {
            String html = null;
            try
            {
                if(DBHandler.getSystemCoords(systemName) == null)
                {
                    Vec3d coordinates = EDSM.getSystemCoordinates(systemName);
                    if(coordinates != null) DBHandler.setSystemCoordinates(systemName, coordinates);
                }

                String stationID = null;
                int counter = 0;
                while(counter < 2)
                {
                    counter++;
                    try
                    {
                        stationID = InaraStation.getInaraStationID(stationName, systemName);
                        break;
                    } catch(HtmlConnectionException ignored)
                    {
                    }
                }
                if(stationID == null)
                {
                    Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(false));
                    return;
                }

                counter = 0;
                while(counter < 2)
                {
                    counter++;
                    try
                    {
                        html = InaraStation.getStationInfoHtml(stationID);
                        break;
                    } catch(HtmlConnectionException ignored)
                    {
                    }
                }
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
            starDistance = EDDNCleanup.getStarDistance(html);

            if(stationtype == null || padsize == null)
            {
                Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(false));
                Logger.log("Aborted station update for " + stationName + " type=" + stationtype + " pad=" + padsize + " starDistance=" + starDistance + " - html: " + html, LogName.WARNING);
                return;
            }
        } else
        {
            padsize = station.PAD;
            stationtype = station.TYPE;
            starDistance = station.DISTANCE_TO_STAR;
        }

        for(String trade : EDDNCleanup.getTrades(outputString))
        {
            int commodityID = getInaraIDForCommodity(trade);
            if(commodityID == -1) continue;


            TRADE station_old = EDDNCleanup.getStationTrade(commodityID, systemName, stationName, padsize, stationtype, starDistance, trade, false);
            if(station_old != null)
            {
                STATION s = DBHandler.getStation(systemName, stationName);
                if(s == null)
                {
                    s = new STATION(systemName, stationName, padsize, stationtype, starDistance);
                    DBHandler.setStationData(s);
                }
                TRADE t = new TRADE(s, commodityID, station_old.AGE, 0, station_old.DEMAND, station_old.IMPORT_PRICE, 0);
                DBHandler.setTradeData(t);
            }

            station_old = EDDNCleanup.getStationTrade(commodityID, systemName, stationName, padsize, stationtype, starDistance, trade, true);
            if(station_old != null)
            {
                STATION s = DBHandler.getStation(systemName, stationName);
                if(s == null)
                {
                    s = new STATION(systemName, stationName, padsize, stationtype, starDistance);
                    DBHandler.setStationData(s);
                }
                TRADE t = new TRADE(s, commodityID, station_old.AGE, station_old.SUPPLY, 0, 0, station_old.EXPORT_PRICE);
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

            Logger.log("Could not parse an EDDN json for a commodity", LogName.WARNING, e);
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

        if(!NameMap.isRare(commodityName) && !NameMap.isIgnored(commodityName))
        {
            Logger.log("Could not find commodity key that matches: " + commodityName, LogName.WARNING, null, 991);
        }
        return -1;
    }
}
