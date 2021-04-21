package com.fi0x.edct.controller;

import com.fi0x.edct.datastructures.COMMODITY;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.datastructures.PADSIZE;
import com.fi0x.edct.datastructures.STATIONTYPE;
import com.fi0x.edct.dbconnection.RequestThread;
import com.fi0x.edct.util.Out;
import com.sun.istack.internal.Nullable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;

public class ControllerMain implements Initializable
{
    @Nullable
    public Map<String, String> commodities;
    public Map<String, ArrayList<STATION>> sellPrices = new HashMap<>();
    public Map<String, ArrayList<STATION>> buyPrices = new HashMap<>();

    private ArrayList<COMMODITY> trades;
    private int currentCommodity;
    private int currentSellStation;
    private int currentBuyStation;

    @FXML
    private TextField quantity;
    @FXML
    private CheckBox cbCarrier;
    @FXML
    private CheckBox cbSurface;
    @FXML
    private CheckBox cbLandingPad;
    @FXML
    private CheckBox cbDemand;
    @FXML
    public Button btnStart;

    @FXML
    private Label lblCommodity;
    @FXML
    private Label lblBuyStation;
    @FXML
    private Label lblSellStation;
    @FXML
    private Label lblBuyPrice;
    @FXML
    private Label lblSellPrice;
    @FXML
    private Label lblSupply;
    @FXML
    private Label lblDemand;
    @FXML
    private Label lblProfit;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Thread threadReq = new Thread(new RequestThread(this, 0));
        threadReq.start();

        quantity.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(!newValue.matches("\\d*")) quantity.setText(newValue.replaceAll("[^\\d]", ""));
            else updateFilters();

        });
        cbCarrier.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbSurface.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbLandingPad.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbDemand.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
    }

    @FXML
    private void calculate()
    {
        btnStart.setVisible(false);

        Thread threadReq = new Thread(new RequestThread(this, 1));
        threadReq.start();
    }
    @FXML
    private void nextCommodity()
    {
        currentCommodity++;
        if(currentCommodity >= trades.size()) currentCommodity = trades.size() - 1;
        displayResults();
    }
    @FXML
    private void previousCommodity()
    {
        currentCommodity--;
        if(currentCommodity < 0) currentCommodity = 0;
        displayResults();
    }
    @FXML
    private void nextSellStation()
    {
        currentSellStation++;
        if(currentSellStation >= trades.get(currentCommodity).SELL_PRICES.size()) currentSellStation = trades.get(currentCommodity).SELL_PRICES.size() - 1;
        displayResults();
    }
    @FXML
    private void previousSellStation()
    {
        currentSellStation--;
        if(currentSellStation < 0) currentSellStation = 0;
        displayResults();
    }
    @FXML
    private void nextBuyStation()
    {
        currentBuyStation++;
        if(currentBuyStation >= trades.get(currentCommodity).BUY_PRICES.size()) currentBuyStation = trades.get(currentCommodity).BUY_PRICES.size() - 1;
        displayResults();
    }
    @FXML
    private void previousBuyStation()
    {
        currentBuyStation--;
        if(currentBuyStation < 0) currentBuyStation = 0;
        displayResults();
    }

    public void updateFilters()
    {
        int amount = Integer.parseInt(quantity.getText());
        boolean noSmall = !cbLandingPad.isSelected();
        boolean noCarrier = !cbCarrier.isSelected();
        boolean noSurface = !cbSurface.isSelected();

        Map<String, ArrayList<STATION>> filteredSellPrices = applyFilters(cbDemand.isSelected() ? 0 : amount, noSmall, noCarrier, noSurface, sellPrices);
        Map<String, ArrayList<STATION>> filteredBuyPrices = applyFilters(amount, noSmall, noCarrier, noSurface, buyPrices);

        trades = getTrades(filteredSellPrices, filteredBuyPrices);
        currentCommodity = 0;
        currentSellStation = 0;
        currentBuyStation = 0;

        displayResults();
    }

    private void displayResults()
    {
        if(trades.size() == 0) return;
        lblCommodity.setText(trades.get(currentCommodity).NAME);
        lblProfit.setText(trades.get(currentCommodity).profit + " credits");

        Out.newBuilder("COMMODITY: \t" + trades.get(currentCommodity).NAME).veryVerbose().print();
        Out.newBuilder("PROFIT: \t" + trades.get(currentCommodity).profit).veryVerbose().print();

        if(trades.get(currentCommodity).BUY_PRICES != null && trades.get(currentCommodity).BUY_PRICES.size() > currentBuyStation)
        {
            STATION buyStation = trades.get(currentCommodity).BUY_PRICES.get(currentBuyStation);
            lblBuyStation.setText(buyStation.NAME);
            lblBuyPrice.setText(buyStation.PRICE + " credits");
            lblSupply.setText(buyStation.QUANTITY + " tons");

            Out.newBuilder("BUY AT: \t" + buyStation.NAME).veryVerbose().print();
            Out.newBuilder("\tPRICE: \t" + buyStation.PRICE).veryVerbose().print();
            Out.newBuilder("\tSUPPLY:\t" + buyStation.QUANTITY).veryVerbose().print();
        }

        if(trades.get(currentCommodity).SELL_PRICES != null && trades.get(currentCommodity).SELL_PRICES.size() > currentSellStation)
        {
            STATION sellStation = trades.get(currentCommodity).SELL_PRICES.get(currentSellStation);
            lblSellStation.setText(sellStation.NAME);
            lblSellPrice.setText(sellStation.PRICE + " credits");
            lblDemand.setText(sellStation.QUANTITY + " tons");

            Out.newBuilder("SELL AT: \t" + sellStation.NAME).veryVerbose().print();
            Out.newBuilder("\tPRICE: \t" + sellStation.PRICE).veryVerbose().print();
            Out.newBuilder("\tDEMAND: \t" + sellStation.QUANTITY).veryVerbose().print();
        }
    }

    private Map<String, ArrayList<STATION>> applyFilters(int amount, boolean noSmall, boolean noCarrier, boolean noSurface, Map<String, ArrayList<STATION>> inputPrices)
    {
        Map<String, ArrayList<STATION>> filteredPrices = new HashMap<>();

        for(Map.Entry<String, ArrayList<STATION>> commodity : inputPrices.entrySet())
        {
            ArrayList<STATION> filteredStations = new ArrayList<>();
            for(STATION station : commodity.getValue())
            {
                boolean validStation = !noSmall || station.PAD == PADSIZE.L;
                if(noCarrier && station.TYPE == STATIONTYPE.CARRIER) validStation = false;
                if(noSurface && station.TYPE == STATIONTYPE.SURFACE) validStation = false;
                if(amount > station.QUANTITY) validStation = false;

                if(validStation) filteredStations.add(station);
            }
            filteredPrices.put(commodity.getKey(), filteredStations);
        }

        return filteredPrices;
    }

    private static ArrayList<COMMODITY> getTrades(Map<String, ArrayList<STATION>> sellPrices, Map<String, ArrayList<STATION>> buyPrices)
    {
        ArrayList<COMMODITY> trades = new ArrayList<>();

        for(Map.Entry<String, ArrayList<STATION>> commodity : sellPrices.entrySet())
        {
            COMMODITY commodityTrade = new COMMODITY(commodity.getKey(), commodity.getValue(), buyPrices.get(commodity.getKey()));
            commodityTrade.sortPrices();
            trades.add(commodityTrade);
        }

        return sortTrades(trades);
    }

    private static ArrayList<COMMODITY> sortTrades(ArrayList<COMMODITY> trades)
    {
        for(int i = 1; i < trades.size(); i++)
        {
            int j = i - 1;
            while(trades.get(j + 1).profit > trades.get(j).profit)
            {
                Collections.swap(trades, j + 1, j);

                if(j == 0) break;
                j--;
            }
        }

        return trades;
    }
}