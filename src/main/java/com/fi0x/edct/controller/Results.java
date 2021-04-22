package com.fi0x.edct.controller;

import com.fi0x.edct.datastructures.COMMODITY;
import com.fi0x.edct.datastructures.STATION;
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
    private int currentSellStation;
    private int currentBuyStation;

    @FXML
    private HBox hbStations;
    @FXML
    private AnchorPane apCommodity;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
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
        currentCommodity++;
        if(currentCommodity >= trades.size()) currentCommodity = trades.size() - 1;
        displayResults();
    }
    public void previousCommodity()
    {
        currentCommodity--;
        if(currentCommodity < 0) currentCommodity = 0;
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
        if(trades == null || trades.size() == 0) return;
        commodityController.updateDisplay();

        Out.newBuilder("COMMODITY: \t" + trades.get(currentCommodity).NAME).veryVerbose().print();
        Out.newBuilder("PROFIT: \t" + trades.get(currentCommodity).profit).veryVerbose().print();

        if(trades.get(currentCommodity).BUY_PRICES != null && trades.get(currentCommodity).BUY_PRICES.size() > currentBuyStation)
        {
            STATION buyStation = trades.get(currentCommodity).BUY_PRICES.get(currentBuyStation);
            buyController.setStation(buyStation);

            Out.newBuilder("BUY AT: \t" + buyStation.NAME).veryVerbose().print();
            Out.newBuilder("\tPRICE: \t" + buyStation.PRICE).veryVerbose().print();
            Out.newBuilder("\tSUPPLY:\t" + buyStation.QUANTITY).veryVerbose().print();
        }

        if(trades.get(currentCommodity).SELL_PRICES != null && trades.get(currentCommodity).SELL_PRICES.size() > currentSellStation)
        {
            STATION sellStation = trades.get(currentCommodity).SELL_PRICES.get(currentSellStation);
            sellController.setStation(sellStation);

            Out.newBuilder("SELL AT: \t" + sellStation.NAME).veryVerbose().print();
            Out.newBuilder("\tPRICE: \t" + sellStation.PRICE).veryVerbose().print();
            Out.newBuilder("\tDEMAND: \t" + sellStation.QUANTITY).veryVerbose().print();
        }
    }

    public COMMODITY getCurrentTrade()
    {
        return trades.get(currentCommodity);
    }
}
