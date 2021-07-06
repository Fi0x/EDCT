package com.fi0x.edct.data;

import com.fi0x.edct.MainWindow;
import com.fi0x.edct.data.cleanup.HTMLCleanup;
import com.fi0x.edct.data.cleanup.JSONCleanup;
import com.fi0x.edct.data.localstorage.NameMap;
import com.fi0x.edct.data.localstorage.db.DBHandler;
import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.data.structures.STATIONTYPE;
import com.fi0x.edct.data.structures.TRADE;
import com.fi0x.edct.data.websites.EDSM;
import com.fi0x.edct.data.websites.InaraStation;
import com.fi0x.edct.util.Logger;
import com.sun.javafx.geom.Vec3d;
import javafx.application.Platform;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class EDDN implements Runnable
{
    public static final String SCHEMA_KEY = "\"$schemaRef\": \"https://eddn.edcd.io/schemas/commodity/3\"";
    public static final String RELAY = "tcp://eddn.edcd.io:9500";

    @Override
    public void run()
    {
        NameMap.initializeNames();

        ZContext ctx = new ZContext();
        ZMQ.Socket client = ctx.createSocket(ZMQ.SUB);
        client.subscribe("".getBytes());
        client.setReceiveTimeOut(30000);

        client.connect(RELAY);
        Logger.INFO("Connected to the EDDN relay");

        ZMQ.Poller poller = ctx.createPoller(2);
        poller.register(client, ZMQ.Poller.POLLIN);
        byte[] output = new byte[256 * 1024];

        while(!Thread.interrupted())
        {
            int poll = poller.poll(10);
            if(poll == ZMQ.Poller.POLLIN)
            {
                if(poller.pollin(0))
                {
                    byte[] recv = client.recv(ZMQ.NOBLOCK);
                    if(recv.length > 0)
                    {
                        Inflater inflater = new Inflater();
                        inflater.setInput(recv);
                        try
                        {
                            int outlen = inflater.inflate(output);
                            String outputString = new String(output, 0, outlen, StandardCharsets.UTF_8);

                            if(outputString.contains(SCHEMA_KEY))
                            {
                                Thread t = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        super.run();
                                        retrieveStationInterrupted(outputString);
                                    }
                                };
                                t.start();
                            }
                        } catch(DataFormatException e)
                        {
                            Logger.WARNING("Something went wrong when retrieving an EDDN message", e);
                        }
                    }
                }
            }
        }
    }

    private void retrieveStationInterrupted(String outputString)
    {
        Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(true));
        String stationName = JSONCleanup.getStationName(outputString);
        String systemName = JSONCleanup.getSystemName(outputString);
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

            padsize = HTMLCleanup.getStationPad(html);
            stationtype = HTMLCleanup.getStationType(html);

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

        for(String trade : JSONCleanup.getTrades(outputString))
        {
            int commodityID = getInaraIDForCommodity(trade);
            if(commodityID == -1) continue;


            TRADE station_old = JSONCleanup.getStationTrade(commodityID, systemName, stationName, padsize, stationtype, trade, false);
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

            station_old = JSONCleanup.getStationTrade(commodityID, systemName, stationName, padsize, stationtype, trade, true);
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

        if(!NameMap.isRare(commodityName) && !NameMap.isIgnored(commodityName)) Logger.WARNING("Could not find commodity key that matches: " + commodityName);
        return -1;
    }
}