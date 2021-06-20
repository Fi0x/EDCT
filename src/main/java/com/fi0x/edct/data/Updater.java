package com.fi0x.edct.data;

import com.fi0x.edct.Main;
import com.fi0x.edct.MainWindow;
import com.fi0x.edct.controller.Datastorage;
import com.fi0x.edct.controller.Settings;
import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.localstorage.TradeReloader;
import com.fi0x.edct.data.websites.EDDB;
import com.fi0x.edct.data.websites.InaraCommodity;
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

        try
        {
            while(!EDDB.updateGalacticAverages())
            {
                if(sleepInterrupted(1000)) return;
            }
        }
        catch(InterruptedException ignored)
        {
            return;
        }

        Logger.INFO("Updated Commodity Average Prices");

        DBHandler.getInstance().removeOldEntries();

        if(loadMissingIDs()) return;

        Logger.INFO("All Commodities loaded");
        Platform.runLater(() ->
        {
            MainWindow.getInstance().setUpdateStatus(-1);
            MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updated", Datastorage.BACKGROUND_STATUS.INITIALIZED);
        });

        Thread threadReq = new Thread(new TradeReloader(MainWindow.getInstance().interactionController));
        threadReq.start();

        if(Thread.interrupted()) return;

        Main.eddn = new Thread(new EDDN());
        Main.eddn.start();

        while(!Thread.interrupted())
        {
            if(sleepInterrupted((long) (Math.random() * 5000) + Settings.inaraDelay - 5000)) return;
            Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updating...", Datastorage.BACKGROUND_STATUS.INITIALIZED));

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
                MainWindow.getInstance().interactionController.storageController.setDataAge(age);
                MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updated", Datastorage.BACKGROUND_STATUS.INITIALIZED);
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
            Platform.runLater(() ->
            {
                MainWindow.getInstance().setUpdateStatus((float)finalCounter / (float)missingIDs.size());
                MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Initializing " + finalCounter + "/" + missingIDs.size(), Datastorage.BACKGROUND_STATUS.INITIALIZING);
            });
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
        if(delay <= 0) return false;
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
