package com.fi0x.edct.logic.threads;

import com.fi0x.edct.gui.controller.Filters;
import com.fi0x.edct.gui.controller.Interaction;
import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logging.Logger;
import com.fi0x.edct.logging.MixpanelHandler;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.structures.TRADE;
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
        Logger.INFO("Trade Reloader Thread started");
        updatePrices();

        long oldestFileAge = DBHandler.getOldestUpdateAge();
        Platform.runLater(() ->
        {
            INT_CONTROLLER.mainController.updateFilters();
            INT_CONTROLLER.storageController.setDataAge(oldestFileAge);
        });

        MainWindow.getInstance().interactionController.storageController.btnStart.setVisible(true);
        MainWindow.getInstance().interactionController.storageController.lblReloadStatus.setVisible(false);
        Logger.INFO("Trade Reloader Thread finished");
        MixpanelHandler.addMessage(MixpanelHandler.EVENT.TRADES_LOADED, null);
    }

    private void updatePrices()
    {
        INT_CONTROLLER.sellPrices = new HashMap<>();
        INT_CONTROLLER.buyPrices = new HashMap<>();
        Filters filters = Filters.getInstance();
        long minAvg = filters == null ? 0 : filters.getFilterSettings().average;

        for(int id : DBHandler.getCommodityIDs(false, minAvg))
        {
            String commodityName = DBHandler.getCommodityNameByID(id);

            ArrayList<TRADE> trade = DBHandler.getTradeInformation(id, false);
            INT_CONTROLLER.sellPrices.put(commodityName, trade);

            trade = DBHandler.getTradeInformation(id, true);
            INT_CONTROLLER.buyPrices.put(commodityName, trade);
        }
    }
}