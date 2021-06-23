package com.fi0x.edct.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.DecimalFormat;

public class Details
{
    @FXML
    private Label lblGalAverage;
    @FXML
    private Label lblLoadProfitTon;
    @FXML
    private Label lblLoadProfitTotal;
    @FXML
    private Label lblCarrierProfitTon;
    @FXML
    private Label lblCarrierProfitTotal;
    @FXML
    private Label lblCarrierBuy;
    @FXML
    private Label lblCarrierSell;
    @FXML
    private Label lblUnloadProfitTon;
    @FXML
    private Label lblUnloadProfitTotal;

    public void setGalacticAverage(long average)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        lblGalAverage.setText("Galactic Average: " + (average > 0 ? df.format(average) + " credits/t" : "UNKNOWN"));
    }

    public void setCarrierStats()
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        //TODO: Set correct buy order price
        //TODO: Set correct sell order price
        //TODO: Set correct profit/t and total profit

        //TODO: Update trader profit related to carrier prices
        lblUnloadProfitTon.setText(df.format(Settings.unloadingTonProfit) + " credits/t");
        lblUnloadProfitTotal.setText(df.format((long) Settings.unloadingTonProfit * Settings.shipCargoSpace / 1000) + "k credits");

        lblLoadProfitTon.setText(df.format(Settings.loadingTonProfit) + " credits/t");
        lblLoadProfitTotal.setText(df.format((long) Settings.loadingTonProfit * Settings.shipCargoSpace / 1000) + "k credits");
    }
}