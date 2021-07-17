package com.fi0x.edct.data.localstorage;

import com.fi0x.edct.MainWindow;
import com.fi0x.edct.controller.Interaction;
import com.fi0x.edct.data.localstorage.db.DBHandler;
import com.fi0x.edct.data.structures.TRADE;
import com.fi0x.edct.telemetry.EVENT;
import com.fi0x.edct.telemetry.MixpanelHandler;
import com.fi0x.edct.util.Logger;
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
        MixpanelHandler.addMessage(EVENT.TRADES_LOADED, null);
    }

    private void updatePrices()
    {
        INT_CONTROLLER.sellPrices = new HashMap<>();
        INT_CONTROLLER.buyPrices = new HashMap<>();

        for(int id : DBHandler.getCommodityIDs(false))
        {
            String commodityName = DBHandler.getCommodityNameByID(id);

            ArrayList<TRADE> trade = DBHandler.getTradeInformation(id, false);
            INT_CONTROLLER.sellPrices.put(commodityName, trade);

            trade = DBHandler.getTradeInformation(id, true);
            INT_CONTROLLER.buyPrices.put(commodityName, trade);
        }
    }
}