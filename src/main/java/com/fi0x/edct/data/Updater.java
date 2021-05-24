package com.fi0x.edct.data;

import com.fi0x.edct.data.webconnection.Inara;
import com.fi0x.edct.util.Out;

public class Updater implements Runnable
{
    @Override
    public void run()
    {
        Out.newBuilder("Updater thread started").verbose().print();

        Inara.updateCommodityIDs();
        Out.newBuilder("Updated Commodity ID-list").veryVerbose().SUCCESS().print();

        //TODO: Download data from all commodities that are not yet added to the local db

        while(!Thread.interrupted())
        {
            try
            {
                Thread.sleep(5000);
            } catch(InterruptedException e)
            {
                return;
            }

            //TODO: Get commodity ID of oldest commodity download
            //TODO: Download buy and sell data for that commodity
            //TODO: Update the last download time for that commodity
            //TODO: Send oldest-file information to GUI
        }
    }
}
