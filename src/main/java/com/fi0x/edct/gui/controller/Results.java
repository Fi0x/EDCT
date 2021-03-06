package com.fi0x.edct.gui.controller;

import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logging.Logger;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.structures.COMMODITY;
import com.fi0x.edct.logic.structures.TRADE;
import com.fi0x.edct.logic.threads.DistanceHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Results implements Initializable
{
    public Main mainController;
    private Station sellController;
    private Station buyController;
    private Commodity commodityController;
    private Details detailsController;

    private ArrayList<COMMODITY> trades;
    private int currentCommodity;
    public int currentSellStation;
    public int currentBuyStation;

    private Pane detailsBox;

    @FXML
    private VBox vbResults;
    @FXML
    private GridPane hbStations;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        currentCommodity = 0;
        currentSellStation = 0;
        currentBuyStation = 0;

        loadCommodity();
        loadStation(false);
        loadStation(true);
        loadDetails();

        updateDetails(Settings.detailedResults);
    }

    private void loadCommodity()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/commodity.fxml"));
        Pane commodityBox;

        try
        {
            commodityBox = loader.load();
            commodityController = loader.getController();
            commodityController.setResultsController(this);

        } catch(IOException e)
        {
            Logger.ERROR(999, "Could not load Commodity controller", e);
            return;
        }

        vbResults.getChildren().add(1, commodityBox);
    }
    private void loadStation(boolean isBuying)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/station.fxml"));
        Pane stationBox;

        try
        {
            stationBox = loader.load();
            if(isBuying)
            {
                buyController = loader.getController();
                buyController.setResultsController(this, true);
            } else
            {
                sellController = loader.getController();
                sellController.setResultsController(this, false);
            }
        } catch(IOException e)
        {
            Logger.ERROR(999, "Could not load Station controller", e);
            return;
        }

        hbStations.add(stationBox, isBuying ? 2 : 0, 0);
    }

    private void loadDetails()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/details.fxml"));

        try
        {
            detailsBox = loader.load();
            detailsController = loader.getController();

        } catch(IOException e)
        {
            Logger.ERROR(999, "Could not load Details controller", e);
            return;
        }

        vbResults.getChildren().add(3, detailsBox);
    }

    public void nextCommodity()
    {
        if(trades == null) return;

        currentCommodity++;
        if(currentCommodity >= trades.size()) currentCommodity = trades.size() - 1;

        currentSellStation = 0;
        currentBuyStation = 0;

        displayResults();
    }
    public void previousCommodity()
    {
        if(trades == null) return;

        currentCommodity--;
        if(currentCommodity < 0) currentCommodity = 0;

        currentSellStation = 0;
        currentBuyStation = 0;

        displayResults();
    }

    public void updateResultController(Main controller)
    {
        controller.setResultController(this);
        mainController = controller;
    }

    public void setTrades(ArrayList<COMMODITY> newTrades)
    {
        trades = newTrades;

        currentCommodity = 0;
        currentSellStation = 0;
        currentBuyStation = 0;
    }

    public void displayResults()
    {
        if(trades == null || trades.size() == 0)
        {
            vbResults.setVisible(false);
            return;
        }

        int profit = 0;
        TRADE sellStation = null;
        TRADE buyStation = null;

        if(trades.get(currentCommodity).BUY_PRICES != null && trades.get(currentCommodity).BUY_PRICES.size() > currentBuyStation)
        {
            sellStation = trades.get(currentCommodity).BUY_PRICES.get(currentBuyStation);
            sellController.setStation(sellStation, currentBuyStation > 0, currentBuyStation < trades.get(currentCommodity).BUY_PRICES.size() - 1);

            profit -= sellStation.SELL_PRICE;
        }

        if(trades.get(currentCommodity).SELL_PRICES != null && trades.get(currentCommodity).SELL_PRICES.size() > currentSellStation)
        {
            buyStation = trades.get(currentCommodity).SELL_PRICES.get(currentSellStation);
            buyController.setStation(buyStation, currentSellStation > 0, currentSellStation < trades.get(currentCommodity).SELL_PRICES.size() - 1);

            if(profit < 0) profit += buyStation.BUY_PRICE;
        }

        trades.get(currentCommodity).profit = profit;
        double distance = 0;
        if(sellStation != null && buyStation != null)
        {
            distance = DBHandler.getSystemDistance(sellStation.STATION.SYSTEM, buyStation.STATION.SYSTEM);
            if(distance == 0)
            {
                DistanceHandler.addDistanceCheck(sellStation.STATION.SYSTEM, buyStation.STATION.SYSTEM);
            }
        }

        commodityController.updateDisplay(currentCommodity > 0, currentCommodity < trades.size() - 1, distance);

        setHiddenDetails();

        vbResults.setVisible(true);
    }

    public void updateDetails(Settings.Details detailed)
    {
        boolean advanced = detailed.equals(Settings.Details.Advanced);
        boolean normal = detailed.equals(Settings.Details.Normal) || advanced;

        commodityController.lblDistance.setVisible(normal);
        commodityController.lblDistance.setManaged(normal);

        buyController.setDetailsVisibility(detailed);
        sellController.setDetailsVisibility(detailed);

        detailsBox.setVisible(advanced);
        detailsBox.setManaged(advanced);

        MainWindow.getInstance().primaryStage.sizeToScene();
    }

    public void updateDistance(String system1, String system2, double distance)
    {
        if(distance == 0) return;
        if(!trades.get(currentCommodity).BUY_PRICES.get(currentBuyStation).STATION.SYSTEM.equals(system1)) return;
        if(!trades.get(currentCommodity).SELL_PRICES.get(currentSellStation).STATION.SYSTEM.equals(system2)) return;

        commodityController.setDistance(distance);
    }

    public COMMODITY getCurrentTrade()
    {
        return trades.get(currentCommodity);
    }
    public void removeCurrentTrade()
    {
        trades.remove(currentCommodity);
        if(currentCommodity >= trades.size()) currentCommodity--;
    }
    public TRADE getCurrentSellStation()
    {
        return trades.get(currentCommodity).BUY_PRICES.get(currentBuyStation);
    }
    public TRADE getCurrentBuyStation()
    {
        return trades.get(currentCommodity).SELL_PRICES.get(currentSellStation);
    }
    public void removeStationFromCurrentTrade(TRADE station)
    {
        getCurrentTrade().SELL_PRICES.remove(station);
        getCurrentTrade().BUY_PRICES.remove(station);
    }

    private void setHiddenDetails()
    {
        detailsController.setGalacticAverage(getCurrentTrade().GALACTIC_AVERAGE);

        long buyPrice = getCurrentTrade().BUY_PRICES.get(currentBuyStation).SELL_PRICE + Settings.loadingTonProfit;
        buyPrice = Math.max(buyPrice, (long) (getCurrentTrade().GALACTIC_AVERAGE * 0.05));
        buyPrice = Math.min(buyPrice, getCurrentTrade().GALACTIC_AVERAGE * 10);

        long sellPrice = getCurrentTrade().SELL_PRICES.get(currentSellStation).BUY_PRICE - Settings.unloadingTonProfit;
        sellPrice = Math.max(sellPrice, (long) (getCurrentTrade().GALACTIC_AVERAGE * 0.05));
        sellPrice = Math.min(sellPrice, getCurrentTrade().GALACTIC_AVERAGE * 10);

        long carrierProfitTon = sellPrice - buyPrice;
        long carrierProfitTotal = carrierProfitTon * Integer.parseInt(MainWindow.getInstance().interactionController.filterController.txtQuantity.getText());

        long loadProfit = buyPrice - getCurrentTrade().BUY_PRICES.get(currentBuyStation).SELL_PRICE;
        long unloadProfit = getCurrentTrade().SELL_PRICES.get(currentSellStation).BUY_PRICE - sellPrice;

        detailsController.setCarrierStats(carrierProfitTon, carrierProfitTotal, buyPrice, sellPrice, loadProfit, unloadProfit);
    }
}
