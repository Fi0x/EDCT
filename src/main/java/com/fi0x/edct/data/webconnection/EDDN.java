package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.MainWindow;
import com.fi0x.edct.data.cleanup.HTMLCleanup;
import com.fi0x.edct.data.cleanup.JSONCleanup;
import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.data.structures.STATIONTYPE;
import com.fi0x.edct.util.Logger;
import javafx.application.Platform;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
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
                                Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus("Receiving Data"));
                                String stationName = JSONCleanup.getStationName(outputString);
                                String systemName = JSONCleanup.getSystemName(outputString);
                                if(stationName == null || systemName == null)
                                {
                                    Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(""));
                                    continue;
                                }

                                String html;
                                try
                                {
                                    String stationID = InaraStation.getInaraStationID(stationName, systemName);
                                    if(stationID == null)
                                    {
                                        Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(""));
                                        continue;
                                    }

                                    html = InaraStation.getStationHtml(stationID);
                                    if(html == null)
                                    {
                                        Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(""));
                                        continue;
                                    }
                                } catch(InterruptedException ignored)
                                {
                                    return;
                                }

                                PADSIZE padsize = HTMLCleanup.getStationPad(html);
                                STATIONTYPE stationtype = HTMLCleanup.getStationType(html);

                                for(String trade : JSONCleanup.getTrades(outputString))
                                {
                                    int commodityID = getInaraIDForCommodity(trade);
                                    if(commodityID == -1) continue;

                                    STATION station = JSONCleanup.getStationTrade(systemName, stationName, padsize, stationtype, trade, false);
                                    if(station != null) DBHandler.getInstance().setStationData(station, commodityID, false);

                                    station = JSONCleanup.getStationTrade(systemName, stationName, padsize, stationtype, trade, false);
                                    if(station != null) DBHandler.getInstance().setStationData(station, commodityID, true);
                                }
                                Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setEDDNStatus(""));
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

    private int getInaraIDForCommodity(String commodityJSON)
    {
        String commodityName;
        try
        {
            JSONObject json = (JSONObject) new JSONParser().parse(commodityJSON);
            commodityName = (String) json.get("name");
        } catch(ParseException e)
        {
            Logger.WARNING("Could not parse an EDDN json for a commodity");
            return -1;
        }

        Map<String, Integer> pairs = DBHandler.getInstance().getCommodityNameIDPairs();
        for(Map.Entry<String, Integer> pair : pairs.entrySet())
        {
            String dbName = pair.getKey()
                    .replace("Low Temperature Diamonds", "lowtemperaturediamond")
                    .replace("Micro-weave Cooling Hoses","coolinghoses")
                    .replace("Hardware Diagnostic Sensor", "diagnosticsensor")
                    .replace("Marine Equipment", "marinesupplies");

            dbName = dbName
                    .replace(" ", "")
                    .replace("-", "");

            if(!commodityName.equalsIgnoreCase(dbName))
            {
                return pair.getValue();
            }
        }

        Logger.WARNING("Could not find commodity key that matches: " + commodityName);
        return -1;
    }
}