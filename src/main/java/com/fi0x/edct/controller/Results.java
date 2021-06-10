package com.fi0x.edct.controller;

import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.structures.COMMODITY;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.data.websites.Hozbase;
import com.fi0x.edct.util.Logger;
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
    private Station sellController;
    private Station buyController;
    private Commodity commodityController;

    private ArrayList<COMMODITY> trades;
    private int currentCommodity;
    public int currentSellStation;
    public int currentBuyStation;

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
            Logger.ERROR(999, "Could not load Commodity controller");
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
            Logger.ERROR(999, "Could not load Station controller");
            return;
        }

        hbStations.add(stationBox, isBuying ? 2 : 0, 0);
    }

    public void nextCommodity()
    {
        if(trades == null) return;

        currentCommodity++;
        if(currentCommodity >= trades.size()) currentCommodity = trades.size() - 1;
        else
        {
            currentSellStation = 0;
            currentBuyStation = 0;
        }

        displayResults();
    }
    public void previousCommodity()
    {
        if(trades == null) return;

        currentCommodity--;
        if(currentCommodity < 0) currentCommodity = 0;
        else
        {
            currentSellStation = 0;
            currentBuyStation = 0;
        }

        displayResults();
    }

    public void updateResultController(Main controller)
    {
        controller.setResultController(this);
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
        STATION sellStation = null;
        STATION buyStation = null;

        if(trades.get(currentCommodity).BUY_PRICES != null && trades.get(currentCommodity).BUY_PRICES.size() > currentBuyStation)
        {
            sellStation = trades.get(currentCommodity).BUY_PRICES.get(currentBuyStation);
            sellController.setStation(sellStation, currentBuyStation > 0, currentBuyStation < trades.get(currentCommodity).BUY_PRICES.size() - 1);

            profit -= sellStation.PRICE;
        }

        if(trades.get(currentCommodity).SELL_PRICES != null && trades.get(currentCommodity).SELL_PRICES.size() > currentSellStation)
        {
            buyStation = trades.get(currentCommodity).SELL_PRICES.get(currentSellStation);
            buyController.setStation(buyStation, currentSellStation > 0, currentSellStation < trades.get(currentCommodity).SELL_PRICES.size() - 1);

            if(profit < 0) profit += buyStation.PRICE;
        }

        trades.get(currentCommodity).profit = profit;
        double distance = 0;
        if(sellStation != null && buyStation != null)
        {
            distance = DBHandler.getInstance().getSystemDistance(sellStation.SYSTEM, buyStation.SYSTEM);
            if(distance == 0) new Thread(new Hozbase(sellStation.SYSTEM, buyStation.SYSTEM)).start();
        }

        commodityController.updateDisplay(currentCommodity > 0, currentCommodity < trades.size() - 1, distance);

        vbResults.setVisible(true);
    }

    public void updateDistance(String system1, String system2, double distance)
    {
        if(distance == 0) return;
        if(!trades.get(currentCommodity).BUY_PRICES.get(currentBuyStation).SYSTEM.equals(system1)) return;
        if(!trades.get(currentCommodity).SELL_PRICES.get(currentSellStation).SYSTEM.equals(system2)) return;

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
    public STATION getCurrentSellStation()
    {
        return trades.get(currentCommodity).BUY_PRICES.get(currentBuyStation);
    }
    public STATION getCurrentBuyStation()
    {
        return trades.get(currentCommodity).SELL_PRICES.get(currentSellStation);
    }
    public void removeStationFromCurrentTrade(STATION station)
    {
        getCurrentTrade().SELL_PRICES.remove(station);
        getCurrentTrade().BUY_PRICES.remove(station);
    }
}
