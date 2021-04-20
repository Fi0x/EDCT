package com.fi0x.edct.controller;

import com.fi0x.edct.datastructures.COMMODITY;
import com.fi0x.edct.dbconnection.InaraCalls;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.datastructures.PADSIZE;
import com.fi0x.edct.datastructures.STATIONTYPE;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;

public class ControllerMain implements Initializable
{
    private final Map<String, String> commodities = InaraCalls.getAllCommodities();
    private Map<String, ArrayList<STATION>> sellPrices = new HashMap<>();
    private Map<String, ArrayList<STATION>> buyPrices = new HashMap<>();

    private ArrayList<COMMODITY> trades;
    private int currentCommodity;
    private int sellStation;
    private int buyStation;

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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        quantity.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(!newValue.matches("\\d*")) quantity.setText(newValue.replaceAll("[^\\d]", ""));
            else displayResults();
        });
    }

    @FXML
    private void calculate()
    {
        sellPrices = new HashMap<>();
        buyPrices = new HashMap<>();

        for(Map.Entry<String, String> entry : commodities.entrySet())
        {
            sellPrices.put(entry.getValue(), InaraCalls.getCommodityPrices(entry.getKey(), true));
            buyPrices.put(entry.getValue(), InaraCalls.getCommodityPrices(entry.getKey(), false));
        }

        displayResults();
    }
    @FXML
    private void nextCommodity()
    {
    }
    @FXML
    private void nextSellStation()
    {
    }
    @FXML
    private void nextBuyStation()
    {
    }

    private void displayResults()
    {
        int amount = Integer.parseInt(quantity.getText());
        boolean noSmall = !cbLandingPad.isSelected();
        boolean noCarrier = !cbCarrier.isSelected();
        boolean noSurface = !cbSurface.isSelected();

        Map<String, ArrayList<STATION>> filteredSellPrices = applyFilters(cbDemand.isSelected() ? 0 : amount, noSmall, noCarrier, noSurface, sellPrices);
        Map<String, ArrayList<STATION>> filteredBuyPrices = applyFilters(amount, noSmall, noCarrier, noSurface, buyPrices);

        trades = getTrades(filteredSellPrices, filteredBuyPrices);
        currentCommodity = 0;
        sellStation = 0;
        buyStation = 0;

        //TODO: Trade-list is already sorted and only needs to be displayed in a nice way
    }

    private Map<String, ArrayList<STATION>> applyFilters(int amount, boolean noSmall, boolean noCarrier, boolean noSurface, Map<String, ArrayList<STATION>> inputPrices)
    {
        Map<String, ArrayList<STATION>> filteredPrices = new HashMap<>();

        for(Map.Entry<String, ArrayList<STATION>> commodity : inputPrices.entrySet())
        {
            ArrayList<STATION> filteredStations = new ArrayList<>();
            for(STATION station : commodity.getValue())
            {
                boolean validStation = true;
                if(noSmall && station.PAD != PADSIZE.L) validStation = false;
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