package com.fi0x.edct.controller;

import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.structures.STATION;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.text.DecimalFormat;

public class Station
{
    private Results resultsController;

    public int stationID;
    private boolean isBuying;

    @FXML
    private Label lblAction;
    @FXML
    private Label lblSystem;
    @FXML
    private Label lblStationName;
    @FXML
    private Label lblType;
    @FXML
    private Label lblPad;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblAmount;
    @FXML
    private Label lblAge;
    @FXML
    private Button btnPrevStation;
    @FXML
    private Button btnNextStation;

    @FXML
    private void nextStation()
    {
        stationID++;
        if(!isBuying && stationID >= resultsController.getCurrentTrade().BUY_PRICES.size())
        {
            stationID = resultsController.getCurrentTrade().BUY_PRICES.size() - 1;
        } else if(isBuying && stationID >= resultsController.getCurrentTrade().SELL_PRICES.size())
        {
            stationID = resultsController.getCurrentTrade().SELL_PRICES.size() - 1;
        }

        if(!isBuying) resultsController.currentBuyStation = stationID;
        else resultsController.currentSellStation = stationID;
        resultsController.displayResults();
    }
    @FXML
    private void previousStation()
    {
        stationID--;
        if(stationID < 0) stationID = 0;

        if(!isBuying) resultsController.currentBuyStation = stationID;
        else resultsController.currentSellStation = stationID;
        resultsController.displayResults();
    }
    @FXML
    private void removeStation()
    {
        STATION s = isBuying ? resultsController.getCurrentBuyStation() : resultsController.getCurrentSellStation();
        int commodityID = DBHandler.getInstance().getCommodityIDByName(resultsController.getCurrentTrade().NAME);
        DBHandler.getInstance().removeStationEntry(commodityID, s.NAME, s.SYSTEM, isBuying);

        resultsController.removeStationFromCurrentTrade(s);

        if(!isBuying && stationID >= resultsController.getCurrentTrade().BUY_PRICES.size()) stationID = resultsController.getCurrentTrade().BUY_PRICES.size() - 1;
        else if(isBuying && stationID >= resultsController.getCurrentTrade().SELL_PRICES.size()) stationID = resultsController.getCurrentTrade().SELL_PRICES.size() - 1;

        if(!isBuying) resultsController.currentBuyStation = stationID;
        else resultsController.currentSellStation = stationID;
        resultsController.displayResults();

        if(resultsController.getCurrentTrade().BUY_PRICES.size() == 0 || resultsController.getCurrentTrade().SELL_PRICES.size() == 0) resultsController.removeCurrentTrade();

        resultsController.displayResults();
    }

    public void setStation(STATION station, boolean hasPrev, boolean hasNext)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        lblSystem.setText("System: " + station.SYSTEM);
        lblStationName.setText("Station: " + station.NAME);
        lblType.setText("Type: " + station.TYPE);
        lblPad.setText("Pad: " + station.PAD);
        lblPrice.setText("Price: " + df.format(station.PRICE) + " credits");
        lblAmount.setText((isBuying ? "Demand: " : "Supply: ") + df.format(station.QUANTITY) + " tons");
        lblAge.setText("Data age: " + station.getUpdateAge());

        btnPrevStation.setDisable(!hasPrev);
        btnNextStation.setDisable(!hasNext);
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
