package com.fi0x.edct.data;

import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.webconnection.Inara;
import com.fi0x.edct.util.Out;

import java.util.ArrayList;

public class Updater implements Runnable
{
    @Override
    public void run()
    {
        Out.newBuilder("Updater thread started").verbose();

        while(!Inara.updateCommodityIDs())
        {
            if(sleepInterrupted(1000)) return;
        }
        Out.newBuilder("Updated Commodity ID-list").veryVerbose().SUCCESS();

        ArrayList<Integer> missingIDs = DBHandler.getInstance().getMissingCommodityIDs();

        for(int id : missingIDs)
        {
            if(sleepInterrupted(500)) return;
            while(!Inara.UpdateCommodityPrices(id))
            {
                if(sleepInterrupted(1000)) return;
            }
        }

        while(!Thread.interrupted())
        {
            if(sleepInterrupted(5000)) return;

            //TODO: Get commodity ID of oldest commodity download
            //TODO: Download buy and sell data for that commodity
            //TODO: Update the last download time for that commodity
            //TODO: Send oldest-file information to GUI
        }
    }

    private boolean sleepInterrupted(long delay)
    {
        try
        {
            Thread.sleep(delay);
        } catch(InterruptedException e)
        {
            return true;
        }
        return false;
    }
}
