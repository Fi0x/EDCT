package com.fi0x.edct.logic.threads;

import com.fi0x.edct.logging.LogName;
import io.fi0x.javalogger.logging.Logger;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

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
        Logger.log("Connected to the EDDN relay", LogName.INFO);

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
                                EDDNHandler.addToQueue(outputString);
                            }
                        } catch(DataFormatException e)
                        {
                            Logger.log("Something went wrong when retrieving an EDDN message", LogName.WARNING, e);
                        }
                    }
                }
            }
        }
    }
}