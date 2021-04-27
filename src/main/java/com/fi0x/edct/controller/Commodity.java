package com.fi0x.edct.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

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
    private void nextCommodity()
    {
        resultsController.nextCommodity();
    }
    @FXML
    private void previousCommodity()
    {
        resultsController.previousCommodity();
    }

    public void updateDisplay(boolean hasPrev, boolean hasNext)
    {
        lblCommodity.setText(resultsController.getCurrentTrade().NAME);
        lblProfit.setText(resultsController.getCurrentTrade().profit + " credits");

        btnPrevComm.setVisible(hasPrev);
        btnNextComm.setVisible(hasNext);
    }

    public void setResultsController(Results controller)
    {
        resultsController = controller;
    }
}
