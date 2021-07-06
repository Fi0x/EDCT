package com.fi0x.edct.controller;

import com.fi0x.edct.data.localstorage.db.DBHandler;
import com.fi0x.edct.data.structures.TRADE;
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
    public Label lblType;
    @FXML
    public Label lblPad;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblAmount;
    @FXML
    public Label lblAge;
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
        TRADE s = isBuying ? resultsController.getCurrentBuyStation() : resultsController.getCurrentSellStation();
        int commodityID = DBHandler.getCommodityIDByName(resultsController.getCurrentTrade().NAME);
        DBHandler.removeStationEntry(commodityID, s.STATION.NAME, s.STATION.SYSTEM, !isBuying);

        resultsController.removeStationFromCurrentTrade(s);

        if(!isBuying && stationID >= resultsController.getCurrentTrade().BUY_PRICES.size()) stationID = resultsController.getCurrentTrade().BUY_PRICES.size() - 1;
        else if(isBuying && stationID >= resultsController.getCurrentTrade().SELL_PRICES.size()) stationID = resultsController.getCurrentTrade().SELL_PRICES.size() - 1;

        if(!isBuying) resultsController.currentBuyStation = stationID;
        else resultsController.currentSellStation = stationID;
        resultsController.displayResults();

        if(resultsController.getCurrentTrade().BUY_PRICES.size() == 0 || resultsController.getCurrentTrade().SELL_PRICES.size() == 0) resultsController.removeCurrentTrade();

        resultsController.displayResults();
    }

    public void setStation(TRADE station, boolean hasPrev, boolean hasNext)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        lblSystem.setText("System:\t " + station.STATION.SYSTEM);
        lblStationName.setText("Station:\t " + station.STATION.NAME);
        lblType.setText("Type:\t " + station.STATION.TYPE);
        lblPad.setText("Pad:\t\t " + station.STATION.PAD);
        lblPrice.setText("Price:\t " + df.format((isBuying ? station.BUY_PRICE : station.SELL_PRICE)) + " credits");
        lblAmount.setText((isBuying ? "Demand:\t " : "Supply:\t ") + df.format((isBuying ? station.DEMAND : station.SUPPLY)) + " tons");
        lblAge.setText("Data age:\t " + station.getUpdateAge());

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
