package com.fi0x.edct.controller;

import com.fi0x.edct.data.structures.COMMODITY;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.util.Out;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
    private HBox hbStations;
    @FXML
    private AnchorPane apCommodity;

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
        HBox commodityBox;

        try
        {
            commodityBox = loader.load();
            commodityController = loader.getController();
            commodityController.setResultsController(this);

        } catch(IOException ignored)
        {
            Out.newBuilder("Could not load commodity GUI elements").always().ERROR().print();
            return;
        }

        apCommodity.getChildren().add(commodityBox);
    }
    private void loadStation(boolean isBuying)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/station.fxml"));
        VBox stationBox;

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
        } catch(IOException ignored)
        {
            Out.newBuilder("Could not load station GUI elements").always().ERROR().print();
            return;
        }

        hbStations.getChildren().add(stationBox);
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
        if(currentCommodity < trades.size())
        {
            currentSellStation = Math.min(currentSellStation, trades.get(currentCommodity).SELL_PRICES.size() - 1);
            currentBuyStation = Math.min(currentBuyStation, trades.get(currentCommodity).BUY_PRICES.size() - 1);

            currentSellStation = Math.max(currentSellStation, 0);
            currentBuyStation = Math.max(currentBuyStation, 0);
        } else
        {
            currentCommodity = trades.size();
            currentSellStation = 0;
            currentBuyStation = 0;
        }
    }

    public void displayResults()
    {
        if(trades == null || trades.size() == 0) return;

        int profit = 0;

        if(trades.get(currentCommodity).BUY_PRICES != null && trades.get(currentCommodity).BUY_PRICES.size() > currentBuyStation)
        {
            STATION sellStation = trades.get(currentCommodity).BUY_PRICES.get(currentBuyStation);
            sellController.setStation(sellStation, currentBuyStation > 0, currentBuyStation < trades.get(currentCommodity).BUY_PRICES.size() - 1);

            profit -= sellStation.PRICE;
        }

        if(trades.get(currentCommodity).SELL_PRICES != null && trades.get(currentCommodity).SELL_PRICES.size() > currentSellStation)
        {
            STATION buyStation = trades.get(currentCommodity).SELL_PRICES.get(currentSellStation);
            buyController.setStation(buyStation, currentSellStation > 0, currentSellStation < trades.get(currentCommodity).SELL_PRICES.size() - 1);

            if(profit < 0) profit += buyStation.PRICE;
        }

        trades.get(currentCommodity).profit = profit;

        commodityController.updateDisplay(currentCommodity > 0, currentCommodity < trades.size() - 1);
    }

    public COMMODITY getCurrentTrade()
    {
        return trades.get(currentCommodity);
    }
}
