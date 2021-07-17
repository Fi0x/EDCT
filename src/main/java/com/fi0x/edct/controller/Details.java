package com.fi0x.edct.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

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

    public void setCarrierStats(long profitTon, long profitTotal, long buyPrice, long sellPrice, long loadProfit, long unloadProfit)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        lblCarrierBuy.setText(df.format(buyPrice) + " credits");
        lblCarrierSell.setText(df.format(sellPrice) + " credits");
        lblCarrierProfitTon.setText(df.format(profitTon) + " credits/t");
        lblCarrierProfitTotal.setText(df.format(profitTotal / 1000000) + "mil credits");

        lblLoadProfitTon.setText(df.format(loadProfit) + " credits/t");
        lblLoadProfitTotal.setText(df.format( loadProfit * Settings.shipCargoSpace / 1000) + "k credits/trip");
        lblUnloadProfitTon.setText(df.format(unloadProfit) + " credits/t");
        lblUnloadProfitTotal.setText(df.format(unloadProfit * Settings.shipCargoSpace / 1000) + "k credits/trip");

        Color color;

        if(profitTon <= 0) color = new Color(238d/255d, 50d/255d, 50d/255d, 1);
        else color = new Color(50d/255d, 238d/255d, 50d/255d, 1);

        lblCarrierProfitTon.setTextFill(color);
        lblCarrierProfitTotal.setTextFill(color);
    }
}