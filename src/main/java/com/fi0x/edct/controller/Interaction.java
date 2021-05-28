package com.fi0x.edct.controller;

import com.fi0x.edct.data.localstorage.TradeReloader;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.util.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Interaction implements Initializable
{
    public Datastorage storageController;
    public Filters filterController;

    public Map<String, ArrayList<STATION>> sellPrices = new HashMap<>();
    public Map<String, ArrayList<STATION>> buyPrices = new HashMap<>();

    @FXML
    private HBox hbInteraction;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        loadFilters();
        loadDatastorage();

        Thread threadReq = new Thread(new TradeReloader(this));
        threadReq.start();
    }

    private void loadDatastorage()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/datastorage.fxml"));
        VBox storageBox;

        try
        {
            storageBox = loader.load();
            storageController = loader.getController();
            storageController.setInteractionController(this);
        } catch(IOException e)
        {
            Logger.ERROR(999, "Could not load Datastorage controller");
            return;
        }

        hbInteraction.getChildren().add(storageBox);
    }
    private void loadFilters()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/filters.fxml"));
        VBox filterBox;

        try
        {
            filterBox = loader.load();
            filterController = loader.getController();
        } catch(IOException e)
        {
            Logger.ERROR(999, "Could not load Filters controller");
            return;
        }

        hbInteraction.getChildren().add(filterBox);
    }

    public void setMainController(Main controller)
    {
        controller.setInteractionController(this);
        filterController.setMainController(controller);
    }
}
