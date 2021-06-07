package com.fi0x.edct.data;

import com.fi0x.edct.Main;
import com.fi0x.edct.MainWindow;
import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.localstorage.TradeReloader;
import com.fi0x.edct.data.webconnection.EDDN;
import com.fi0x.edct.data.webconnection.InaraCommodity;
import com.fi0x.edct.util.Logger;
import javafx.application.Platform;

import java.util.ArrayList;

public class Updater implements Runnable
{
    @Override
    public void run()
    {
        Logger.INFO("Updater Thread started");
        try
        {
            while(!InaraCommodity.updateCommodityIDs())
            {
                if(sleepInterrupted(1000)) return;
            }
        } catch(InterruptedException ignored)
        {
            return;
        }

        DBHandler.getInstance().removeOldEntries();

        if(loadMissingIDs()) return;

        Logger.INFO("All Commodities loaded");
        Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updated"));

        Thread threadReq = new Thread(new TradeReloader(MainWindow.getInstance().interactionController));
        threadReq.start();

        if(Thread.interrupted()) return;

        Main.eddn = new Thread(new EDDN());
        Main.eddn.start();

        while(!Thread.interrupted())
        {
            if(sleepInterrupted((long) (Math.random() * 5000) + 10000)) return;
            Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updating..."));

            int oldestID = DBHandler.getInstance().getOldestCommodityID();
            if(oldestID == 0) continue;

            try
            {
                InaraCommodity.updateCommodityPrices(oldestID);
            } catch(InterruptedException ignored)
            {
                return;
            }
            long age = System.currentTimeMillis() - DBHandler.getInstance().getOldestUpdateAge() * 1000L;

            Platform.runLater(() ->
            {
                MainWindow.getInstance().interactionController.storageController.setDataAge(age, true);
                MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updated");
            });
        }
        Logger.INFO("Updater Thread stopped");
    }

    private boolean loadMissingIDs()
    {
        ArrayList<Integer> missingIDs = DBHandler.getInstance().getCommodityIDs(true);

        int counter = 0;
        for(int id : missingIDs)
        {
            counter++;
            int finalCounter = counter;
            Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Initializing " + finalCounter + "/" + missingIDs.size() + " ..."));
            if(sleepInterrupted(250)) return true;
            try
            {
                while(!InaraCommodity.updateCommodityPrices(id))
                {
                    if(sleepInterrupted(500)) return true;
                }
            } catch(InterruptedException ignored)
            {
                return true;
            }
        }
        return false;
    }

    private boolean sleepInterrupted(long delay)
    {
        try
        {
            Thread.sleep(delay);
        } catch(InterruptedException ignored)
        {
            return true;
        }
        return false;
    }
}
