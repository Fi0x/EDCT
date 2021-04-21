package com.fi0x.edct.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Results
{
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
}
