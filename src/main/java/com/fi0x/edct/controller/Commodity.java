package com.fi0x.edct.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.text.DecimalFormat;

public class Commodity
{
    private Results resultsController;

    @FXML
    private Button btnPrevComm;
    @FXML
    private Button btnNextComm;
    @FXML
    private Label lblCommodity;
    @FXML
    private Label lblProfit;
    @FXML
    private Label lblDistance;

    @FXML
    private void nextCommodity()
    {
        resultsController.nextCommodity();
    }
    @FXML
    private void previousCommodity()
    {
        resultsController.previousCommodity();
    }

    public void updateDisplay(boolean hasPrev, boolean hasNext, double distance)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        lblCommodity.setText(resultsController.getCurrentTrade().NAME);
        String profit = df.format(resultsController.getCurrentTrade().profit);
        lblProfit.setText(profit + " credits");

        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(1);
        lblDistance.setText("Distance: " + (distance == 0 ? "Unknown" : df.format(distance) + "Ly"));

        btnPrevComm.setDisable(!hasPrev);
        btnNextComm.setDisable(!hasNext);
    }

    public void setResultsController(Results controller)
    {
        resultsController = controller;
    }
}
