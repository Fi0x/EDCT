package com.fi0x.edct.controller;

import com.fi0x.edct.dbconnection.InaraCalls;
import com.fi0x.edct.dbconnection.STATION;
import com.fi0x.edct.enums.PADSIZE;
import com.fi0x.edct.enums.STATIONTYPE;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ControllerMain implements Initializable
{
    private Map<String, String> commodities = InaraCalls.getAllCommodities();
    private Map<String, ArrayList<STATION>> sellPrices = new HashMap<>();
    private Map<String, ArrayList<STATION>> buyPrices = new HashMap<>();

    @FXML
    private TextField quantity;
    @FXML
    private CheckBox cbCarrier;
    @FXML
    private CheckBox cbSurface;
    @FXML
    private CheckBox cbLandingPad;

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

    private void displayResults()
    {
        int amount = Integer.parseInt(quantity.getText());
        boolean noSmall = !cbLandingPad.isSelected();
        boolean noCarrier = !cbCarrier.isSelected();
        boolean noSurface = !cbSurface.isSelected();

        Map<String, ArrayList<STATION>> filteredSellPrices = applyFilters(amount, noSmall, noCarrier, noSurface, buyPrices);
        Map<String, ArrayList<STATION>> filteredBuyPrices = applyFilters(amount, noSmall, noCarrier, noSurface, buyPrices);

        for(Map.Entry<String, ArrayList<STATION>> commodity : filteredSellPrices.entrySet())
        {
            ArrayList<STATION> sellingStations = commodity.getValue();
            ArrayList<STATION> buyingStations = filteredBuyPrices.get(commodity.getKey());
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
}