package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.util.Logger;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


/**
 * Subscribe to zmq relay from EDDN
 */
public class EDDN implements Runnable
{
    public static final String SCHEMA_KEY = "https://eddn.edcd.io/schemas/commodity/3";
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
                ZMQ.PollItem item = poller.getItem(poll);

                if(poller.pollin(0))
                {
                    byte[] recv = client.recv(ZMQ.NOBLOCK);
                    if(recv.length > 0)
                    {
                        // decompress
                        Inflater inflater = new Inflater();
                        inflater.setInput(recv);
                        try
                        {
                            int outlen = inflater.inflate(output);
                            String outputString = new String(output, 0, outlen, "UTF-8");
                            // outputString contains a json message

                            if(outputString.contains(SCHEMA_KEY))
                            {
                                System.out.println(outputString);
                            }

                        } catch(DataFormatException | IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}