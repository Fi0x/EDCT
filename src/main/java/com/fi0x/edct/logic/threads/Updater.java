package com.fi0x.edct.logic.threads;

import com.fi0x.edct.Main;
import com.fi0x.edct.gui.controller.Datastorage;
import com.fi0x.edct.gui.controller.Settings;
import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logging.exceptions.HtmlConnectionException;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.websites.EDDB;
import com.fi0x.edct.logic.websites.InaraCommodity;
import io.fi0x.javalogger.logging.Logger;
import javafx.application.Platform;

import java.util.ArrayList;

public class Updater implements Runnable
{
    @Override
    public void run()
    {
        Logger.log("Updater Thread started", LogName.INFO);
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

        Logger.log("Updated Commodity Average Prices", LogName.VERBOSE);

        DBHandler.removeOldEntries();

        if(loadMissingIDs()) return;

        Logger.log("All Commodities loaded", LogName.VERBOSE);
        Platform.runLater(() ->
        {
            MainWindow.getInstance().setUpdateStatus(-1);
            MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updated", Datastorage.BACKGROUND_STATUS.INITIALIZED);
        });

        if(Main.reloader != null) Main.reloader.interrupt();
        Main.reloader = new Thread(new TradeReloader(MainWindow.getInstance().interactionController));
        Main.reloader.start();

        if(Thread.interrupted()) return;

        Main.eddn = new Thread(new EDDN());
        Main.eddn.start();

        while(!Thread.interrupted())
        {
            if(sleepInterrupted((long) (Math.random() * 5000) + Settings.inaraDelay - 5000)) return;
            Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updating...", Datastorage.BACKGROUND_STATUS.INITIALIZED));

            int oldestID = DBHandler.getOldestCommodityID();
            if(oldestID == 0) continue;

            int counter = 0;
            while(counter < 3)
            {
                counter++;
                try
                {
                    InaraCommodity.updateCommodityPrices(oldestID);
                    break;
                } catch(InterruptedException ignored)
                {
                    return;
                } catch(HtmlConnectionException ignored)
                {
                }
            }
            long age = System.currentTimeMillis() - DBHandler.getOldestUpdateAge() * 1000L;

            Platform.runLater(() ->
            {
                MainWindow.getInstance().interactionController.storageController.setDataAge(age);
                MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updated", Datastorage.BACKGROUND_STATUS.INITIALIZED);
            });
        }
        Logger.log("Updater Thread stopped", LogName.INFO);
    }

    private boolean loadMissingIDs()
    {
        ArrayList<Integer> missingIDs = DBHandler.getCommodityIDs(true, 0);

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

            int counter2 = 0;
            while(counter2 < 2)
            {
                counter2++;
                try
                {
                    while(!InaraCommodity.updateCommodityPrices(id))
                    {
                        if(sleepInterrupted(500)) return true;
                    }
                    break;
                } catch(InterruptedException ignored)
                {
                    return true;
                } catch(HtmlConnectionException ignored)
                {
                }
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
