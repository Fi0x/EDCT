package com.fi0x.edct.gui.controller;

import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.structures.COMMODITY;
import com.fi0x.edct.logic.structures.TRADE;
import com.fi0x.edct.logic.threads.DistanceHandler;
import io.fi0x.javalogger.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Results implements Initializable
{
    public Main mainController;
    private Station exportStationController;
    private Station importStationController;
    private Commodity commodityController;
    private Details detailsController;

    private ArrayList<COMMODITY> trades;
    private int currentCommodity;
    public int currentImportStation;
    public int currentExportStation;

    private Pane detailsBox;

    @FXML
    private VBox vbResults;
    @FXML
    private GridPane hbStations;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        currentCommodity = 0;
        currentImportStation = 0;
        currentExportStation = 0;

        loadCommodity();
        loadStation(false);
        loadStation(true);
        loadDetails();

        updateDetails(Settings.detailedResults);
    }

    private void loadCommodity()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/commodity.fxml"));
        Pane commodityBox;

        try
        {
            commodityBox = loader.load();
            commodityController = loader.getController();
            commodityController.setResultsController(this);

        } catch(IOException e)
        {
            Logger.log("Could not load Commodity controller", LogName.getError(999), e, 999);
            return;
        }

        vbResults.getChildren().add(1, commodityBox);
    }
    private void loadStation(boolean isImportStation)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/station.fxml"));
        Pane stationBox;

        try
        {
            stationBox = loader.load();
            if(isImportStation)
            {
                importStationController = loader.getController();
                importStationController.setResultsController(this, true);
            } else
            {
                exportStationController = loader.getController();
                exportStationController.setResultsController(this, false);
            }
        } catch(IOException e)
        {
            Logger.log("Could not load Station controller", LogName.getError(999), e, 999);
            return;
        }

        hbStations.add(stationBox, isImportStation ? 2 : 0, 0);
    }

    private void loadDetails()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/details.fxml"));

        try
        {
            detailsBox = loader.load();
            detailsController = loader.getController();

        } catch(IOException e)
        {
            Logger.log("Could not load Details controller", LogName.getError(999), e, 999);
            return;
        }

        vbResults.getChildren().add(3, detailsBox);
    }

    public void nextCommodity()
    {
        if(trades == null) return;

        currentCommodity++;
        if(currentCommodity >= trades.size()) currentCommodity = trades.size() - 1;

        currentImportStation = 0;
        currentExportStation = 0;

        displayResults();
    }
    public void previousCommodity()
    {
        if(trades == null) return;

        currentCommodity--;
        if(currentCommodity < 0) currentCommodity = 0;

        currentImportStation = 0;
        currentExportStation = 0;

        displayResults();
    }

    public void updateResultController(Main controller)
    {
        controller.setResultController(this);
        mainController = controller;
    }

    public void setTrades(ArrayList<COMMODITY> newTrades)
    {
        trades = newTrades;

        currentCommodity = 0;
        currentImportStation = 0;
        currentExportStation = 0;
    }

    public void displayResults()
    {
        if(trades == null || trades.size() == 0)
        {
            vbResults.setVisible(false);
            return;
        }

        int profit = 0;
        TRADE exportStation = null;
        TRADE importStation = null;

        if(trades.get(currentCommodity).EXPORT_PRICES != null && trades.get(currentCommodity).EXPORT_PRICES.size() > currentExportStation)
        {
            exportStation = trades.get(currentCommodity).EXPORT_PRICES.get(currentExportStation);
            exportStationController.setStation(exportStation, currentExportStation > 0, currentExportStation < trades.get(currentCommodity).EXPORT_PRICES.size() - 1);

            profit -= exportStation.EXPORT_PRICE;
        }

        if(trades.get(currentCommodity).IMPORT_PRICES != null && trades.get(currentCommodity).IMPORT_PRICES.size() > currentImportStation)
        {
            importStation = trades.get(currentCommodity).IMPORT_PRICES.get(currentImportStation);
            importStationController.setStation(importStation, currentImportStation > 0, currentImportStation < trades.get(currentCommodity).IMPORT_PRICES.size() - 1);

            if(profit < 0)
                profit += importStation.IMPORT_PRICE;
        }

        trades.get(currentCommodity).profit = profit;
        double distance = 0;
        if(exportStation != null && importStation != null)
        {
            distance = DBHandler.getSystemDistance(exportStation.STATION.SYSTEM, importStation.STATION.SYSTEM);
            if(distance == 0)
            {
                DistanceHandler.addDistanceCheck(exportStation.STATION.SYSTEM, importStation.STATION.SYSTEM);
            }
        }

        commodityController.updateDisplay(currentCommodity > 0, currentCommodity < trades.size() - 1, distance);

        setHiddenDetails();

        vbResults.setVisible(true);
    }

    public void updateDetails(Settings.Details detailed)
    {
        boolean advanced = detailed.equals(Settings.Details.Advanced);
        boolean normal = detailed.equals(Settings.Details.Normal) || advanced;

        commodityController.lblDistance.setVisible(normal);
        commodityController.lblDistance.setManaged(normal);

        importStationController.setDetailsVisibility(detailed);
        exportStationController.setDetailsVisibility(detailed);

        detailsBox.setVisible(advanced);
        detailsBox.setManaged(advanced);

        MainWindow.getInstance().primaryStage.sizeToScene();
    }

    public void updateDistance(String system1, String system2, double distance)
    {
        if(distance == 0) return;
        if(!trades.get(currentCommodity).EXPORT_PRICES.get(currentExportStation).STATION.SYSTEM.equals(system1)) return;
        if(!trades.get(currentCommodity).IMPORT_PRICES.get(currentImportStation).STATION.SYSTEM.equals(system2)) return;

        commodityController.setDistance(distance);
    }

    public COMMODITY getCurrentTrade()
    {
        return trades.get(currentCommodity);
    }
    public void removeCurrentTrade()
    {
        trades.remove(currentCommodity);
        if(currentCommodity >= trades.size()) currentCommodity--;
    }
    public TRADE getCurrentExportStation()
    {
        return trades.get(currentCommodity).EXPORT_PRICES.get(currentExportStation);
    }
    public TRADE getCurrentImportStation()
    {
        return trades.get(currentCommodity).IMPORT_PRICES.get(currentImportStation);
    }
    public void removeStationFromCurrentTrade(TRADE station)
    {
        getCurrentTrade().IMPORT_PRICES.remove(station);
        getCurrentTrade().EXPORT_PRICES.remove(station);
    }

    private void setHiddenDetails()
    {
        detailsController.setGalacticAverage(getCurrentTrade().GALACTIC_AVERAGE);

        long carrierBuyPrice = getCurrentTrade().EXPORT_PRICES.get(currentExportStation).EXPORT_PRICE + Settings.loadingTonProfit;
        carrierBuyPrice = Math.max(carrierBuyPrice, (long) (getCurrentTrade().GALACTIC_AVERAGE * 0.05));
        carrierBuyPrice = Math.min(carrierBuyPrice, getCurrentTrade().GALACTIC_AVERAGE * 10);

        long carrierSellPrice = getCurrentTrade().IMPORT_PRICES.get(currentImportStation).IMPORT_PRICE - Settings.unloadingTonProfit;
        carrierSellPrice = Math.max(carrierSellPrice, (long) (getCurrentTrade().GALACTIC_AVERAGE * 0.05));
        carrierSellPrice = Math.min(carrierSellPrice, getCurrentTrade().GALACTIC_AVERAGE * 10);

        long carrierProfitTon = carrierSellPrice - carrierBuyPrice;
        long carrierProfitTotal = carrierProfitTon * Integer.parseInt(MainWindow.getInstance().interactionController.filterController.txtQuantity.getText());

        long loadProfit = carrierBuyPrice - getCurrentTrade().EXPORT_PRICES.get(currentExportStation).EXPORT_PRICE;
        long unloadProfit = getCurrentTrade().IMPORT_PRICES.get(currentImportStation).IMPORT_PRICE - carrierSellPrice;

        detailsController.setCarrierStats(carrierProfitTon, carrierProfitTotal, carrierBuyPrice, carrierSellPrice, loadProfit, unloadProfit);
    }
}
