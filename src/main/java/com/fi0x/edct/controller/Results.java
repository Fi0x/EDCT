package com.fi0x.edct.controller;

import com.fi0x.edct.datastructures.COMMODITY;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.util.Out;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;

public class Results
{
    private Main mainController;

    private ArrayList<COMMODITY> trades;
    private int currentCommodity;
    private int currentSellStation;
    private int currentBuyStation;

    @FXML
    private Label lblCommodity;
    @FXML
    private Label lblBuyStation;
    @FXML
    private Label lblSellStation;
    @FXML
    private Label lblBuyPrice;
    @FXML
    private Label lblSellPrice;
    @FXML
    private Label lblSupply;
    @FXML
    private Label lblDemand;
    @FXML
    private Label lblProfit;

    @FXML
    private void nextCommodity()
    {
        currentCommodity++;
        if(currentCommodity >= trades.size()) currentCommodity = trades.size() - 1;
        displayResults();
    }
    @FXML
    private void previousCommodity()
    {
        currentCommodity--;
        if(currentCommodity < 0) currentCommodity = 0;
        displayResults();
    }
    @FXML
    private void nextSellStation()
    {
        currentSellStation++;
        if(currentSellStation >= trades.get(currentCommodity).SELL_PRICES.size()) currentSellStation = trades.get(currentCommodity).SELL_PRICES.size() - 1;
        displayResults();
    }
    @FXML
    private void previousSellStation()
    {
        currentSellStation--;
        if(currentSellStation < 0) currentSellStation = 0;
        displayResults();
    }
    @FXML
    private void nextBuyStation()
    {
        currentBuyStation++;
        if(currentBuyStation >= trades.get(currentCommodity).BUY_PRICES.size()) currentBuyStation = trades.get(currentCommodity).BUY_PRICES.size() - 1;
        displayResults();
    }
    @FXML
    private void previousBuyStation()
    {
        currentBuyStation--;
        if(currentBuyStation < 0) currentBuyStation = 0;
        displayResults();
    }

    public void setMainController(Main controller)
    {
        mainController = controller;
        mainController.setResultController(this);
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
        if(trades.size() == 0) return;
        lblCommodity.setText(trades.get(currentCommodity).NAME);
        lblProfit.setText(trades.get(currentCommodity).profit + " credits");

        Out.newBuilder("COMMODITY: \t" + trades.get(currentCommodity).NAME).veryVerbose().print();
        Out.newBuilder("PROFIT: \t" + trades.get(currentCommodity).profit).veryVerbose().print();

        if(trades.get(currentCommodity).BUY_PRICES != null && trades.get(currentCommodity).BUY_PRICES.size() > currentBuyStation)
        {
            STATION buyStation = trades.get(currentCommodity).BUY_PRICES.get(currentBuyStation);
            lblBuyStation.setText(buyStation.NAME);
            lblBuyPrice.setText(buyStation.PRICE + " credits");
            lblSupply.setText(buyStation.QUANTITY + " tons");

            Out.newBuilder("BUY AT: \t" + buyStation.NAME).veryVerbose().print();
            Out.newBuilder("\tPRICE: \t" + buyStation.PRICE).veryVerbose().print();
            Out.newBuilder("\tSUPPLY:\t" + buyStation.QUANTITY).veryVerbose().print();
        }

        if(trades.get(currentCommodity).SELL_PRICES != null && trades.get(currentCommodity).SELL_PRICES.size() > currentSellStation)
        {
            STATION sellStation = trades.get(currentCommodity).SELL_PRICES.get(currentSellStation);
            lblSellStation.setText(sellStation.NAME);
            lblSellPrice.setText(sellStation.PRICE + " credits");
            lblDemand.setText(sellStation.QUANTITY + " tons");

            Out.newBuilder("SELL AT: \t" + sellStation.NAME).veryVerbose().print();
            Out.newBuilder("\tPRICE: \t" + sellStation.PRICE).veryVerbose().print();
            Out.newBuilder("\tDEMAND: \t" + sellStation.QUANTITY).veryVerbose().print();
        }
    }
}
