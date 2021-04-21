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
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class Main implements Initializable
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
    private VBox vbMain;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Thread threadReq = new Thread(new RequestThread(this, 0));
        threadReq.start();
    }

    public void updateFilters(int amount, boolean ignoreDemand, boolean noSmall, boolean noCarrier, boolean noSurface)
    {
        amount = Integer.parseInt(quantity.getText());
        noSmall = !cbLandingPad.isSelected();
        noCarrier = !cbCarrier.isSelected();
        noSurface = !cbSurface.isSelected();
        ignoreDemand = cbDemand.isSelected;

        Map<String, ArrayList<STATION>> filteredSellPrices = applyFilters(ignoreDemand ? 0 : amount, noSmall, noCarrier, noSurface, sellPrices);
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