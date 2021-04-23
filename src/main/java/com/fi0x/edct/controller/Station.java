package com.fi0x.edct.controller;

import com.fi0x.edct.datastructures.STATION;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Station
{
    private Results resultsController;

    public int stationID;
    private boolean isBuying;

    @FXML
    private Label lblAction;
    @FXML
    private Label lblStationName;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblAmount;

    @FXML
    private void nextStation()
    {
        stationID++;
        if(isBuying && stationID >= resultsController.getCurrentTrade().BUY_PRICES.size())
        {
            stationID = resultsController.getCurrentTrade().BUY_PRICES.size() - 1;
        } else if(!isBuying && stationID >= resultsController.getCurrentTrade().SELL_PRICES.size())
        {
            stationID = resultsController.getCurrentTrade().SELL_PRICES.size() - 1;
        }
        resultsController.displayResults();
    }
    @FXML
    private void previousStation()
    {
        stationID--;
        if(stationID < 0) stationID = 0;
        resultsController.displayResults();
    }

    public void setStation(STATION station)
    {
        lblStationName.setText("Station: " + station.NAME);
        lblPrice.setText("Price: " + station.PRICE + " credits");
        lblAmount.setText((isBuying ? "Demand: " : "Supply: ") + station.QUANTITY + " tons");
    }

    public void setResultsController(Results controller, boolean isBuying)
    {
        resultsController = controller;
        this.isBuying = isBuying;
        if(isBuying)
        {
            lblAction.setText("Sell at");
            lblAmount.setText("Demand: ---");
        }
    }
}
