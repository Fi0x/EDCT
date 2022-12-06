package com.fi0x.edct.logic.threads;

import com.fi0x.edct.gui.controller.Filters;
import com.fi0x.edct.gui.controller.Interaction;
import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logging.exceptions.MixpanelEvents;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.structures.TRADE;
import io.fi0x.javalogger.logging.Logger;
import io.fi0x.javalogger.mixpanel.MixpanelHandler;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;

public class TradeReloader implements Runnable
{
    private final Interaction INT_CONTROLLER;

    public TradeReloader(Interaction interaction)
    {
        INT_CONTROLLER = interaction;
    }

    @Override
    public void run()
    {
        Logger.log("Trade Reloader Thread started", LogName.VERBOSE);
        updatePrices();

        long oldestFileAge = DBHandler.getOldestUpdateAge();
        Platform.runLater(() ->
        {
            INT_CONTROLLER.mainController.updateFilters();
            INT_CONTROLLER.storageController.setDataAge(oldestFileAge);
        });

        MainWindow.getInstance().interactionController.storageController.btnStart.setVisible(true);
        MainWindow.getInstance().interactionController.storageController.lblReloadStatus.setVisible(false);
        Logger.log("Trade Reloader Thread finished", LogName.VERBOSE);
        MixpanelHandler.addMessage(MixpanelEvents.TRADES_LOADED.name(), null);
    }

    private void updatePrices()
    {
        INT_CONTROLLER.importPrices = new HashMap<>();
        INT_CONTROLLER.exportPrices = new HashMap<>();
        Filters filters = Filters.getInstance();
        long minAvg = filters == null ? 0 : filters.getFilterSettings().average;

        ArrayList<Integer> ids = DBHandler.getCommodityIDs(false, minAvg);
        Logger.log("Starting price updates for " + ids.size() + " commodities", LogName.VERBOSE);
        long time = System.currentTimeMillis();
        for(int idx = 0; idx < ids.size(); idx++)
        {
            int id = ids.get(idx);
            String commodityName = DBHandler.getCommodityNameByID(id);

            ArrayList<TRADE> trade = DBHandler.getTradeInformation(id, false);
            INT_CONTROLLER.importPrices.put(commodityName, trade);

            trade = DBHandler.getTradeInformation(id, true);
            INT_CONTROLLER.exportPrices.put(commodityName, trade);

            float percentage = (float) idx / ids.size();
            Platform.runLater(() ->
            {
                INT_CONTROLLER.storageController.setTradeReloaderProgress(percentage);
                MainWindow.getInstance().setUpdateStatus(percentage);
            });
        }
        Platform.runLater(() -> MainWindow.getInstance().setUpdateStatus(-1));
        Logger.log("Price updates finished after " + (System.currentTimeMillis() - time) + " milliseconds", LogName.TIME);
    }
}