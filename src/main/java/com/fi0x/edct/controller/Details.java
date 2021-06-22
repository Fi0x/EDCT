package com.fi0x.edct.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.DecimalFormat;

public class Details
{
    @FXML
    private Label lblGalAverage;

    public void setGalacticAverage(long average)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        lblGalAverage.setText("Galactic Average: " + (average > 0 ? df.format(average) + " credits/t" : "UNKNOWN"));
    }
}
