package com.fi0x.edct.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Commodity
{
    private Results resultsController;

    @FXML
    private Label lblCommodity;
    @FXML
    private Label lblProfit;

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

    public void updateDisplay()
    {
        lblCommodity.setText(resultsController.getCurrentTrade().NAME);
        lblProfit.setText(resultsController.getCurrentTrade().profit + " credits");
    }

    public void setResultsController(Results controller)
    {
        resultsController = controller;
    }
}
