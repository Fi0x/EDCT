package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.data.cleanup.JSONCleanup;
import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.data.structures.STATIONTYPE;
import com.fi0x.edct.util.Logger;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
                                String stationName = JSONCleanup.getStationName(outputString);
                                String systemName = JSONCleanup.getSystemName(outputString);
                                if(stationName == null || systemName == null) continue;

                                PADSIZE padsize = null; //TODO: Get landing pad size from inara
                                STATIONTYPE stationtype = null; //TODO: Get station type from inara

                                for(String trade : JSONCleanup.getTrades(outputString))
                                {
                                    int commodityID = -1; //TODO: Get commodity id from inara

                                    STATION station = JSONCleanup.getStationTrade(systemName, stationName, padsize, stationtype, trade, false);
                                    if(station != null) DBHandler.getInstance().setStationData(station, commodityID, false);

                                    station = JSONCleanup.getStationTrade(systemName, stationName, padsize, stationtype, trade, false);
                                    if(station != null) DBHandler.getInstance().setStationData(station, commodityID, true);
                                }
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
}