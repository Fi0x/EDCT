package com.fi0x.edct.gui.controller;

import com.fi0x.edct.logic.helper.ColorSelector;
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
    public Label lblDistance;

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
        lblProfit.setText("Profit: " + profit + " credits/t");
        lblProfit.setTextFill(ColorSelector.getProfitColor(resultsController.getCurrentTrade().profit));

        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(1);
        lblDistance.setText("Distance: " + (distance == 0 ? "Unknown" : df.format(distance) + "Ly"));

        btnPrevComm.setDisable(!hasPrev);
        btnNextComm.setDisable(!hasNext);
    }

    public void setDistance(double distance)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(1);

        lblDistance.setText("Distance: " + df.format(distance) + "Ly");
    }

    public void setResultsController(Results controller)
    {
        resultsController = controller;
    }
}
