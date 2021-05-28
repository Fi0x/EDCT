package com.fi0x.edct.data;

import com.fi0x.edct.MainWindow;
import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.webconnection.Inara;
import com.fi0x.edct.util.Out;
import javafx.application.Platform;

import java.util.ArrayList;

public class Updater implements Runnable
{
    @Override
    public void run()
    {
        Out.newBuilder("Updater thread started").verbose().INFO();

        while(!Inara.updateCommodityIDs())
        {
            if(sleepInterrupted(1000)) return;
        }
        Out.newBuilder("Updated Commodity ID-list").verbose().INFO();

        ArrayList<Integer> missingIDs = DBHandler.getInstance().getCommodityIDs(true);

        int counter = 0;
        for(int id : missingIDs)
        {
            counter++;
            int finalCounter = counter;
            Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Initializing " + finalCounter + "/" + missingIDs.size() + " ..."));
            if(sleepInterrupted(250)) return;
            while(!Inara.updateCommodityPrices(id))
            {
                if(sleepInterrupted(500)) return;
            }
        }

        Out.newBuilder("All missing ids downloaded").verbose().INFO();
        Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updated"));

        while(!Thread.interrupted())
        {
            if(sleepInterrupted((long) (Math.random() * 4500) + 250)) return;
            Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updating..."));

            int oldestID = DBHandler.getInstance().getOldestCommodityID();
            if(oldestID == 0) continue;

            Inara.updateCommodityPrices(oldestID);
            long age = System.currentTimeMillis() - DBHandler.getInstance().getOldestUpdateAge() * 1000L;

            Platform.runLater(() ->
            {
                MainWindow.getInstance().interactionController.storageController.setDataAge(age, true);
                MainWindow.getInstance().interactionController.storageController.setUpdateStatus("Updated");
            });
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
