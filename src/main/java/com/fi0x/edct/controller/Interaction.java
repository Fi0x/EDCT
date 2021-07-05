package com.fi0x.edct.controller;

import com.fi0x.edct.data.structures.STATION_OLD;
import com.fi0x.edct.data.structures.TRADE;
import com.fi0x.edct.util.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

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

    public Map<String, ArrayList<TRADE>> sellPrices = new HashMap<>();
    public Map<String, ArrayList<TRADE>> buyPrices = new HashMap<>();
    @Deprecated
    public Map<String, ArrayList<STATION_OLD>> sellPrices_OLD = new HashMap<>();
    @Deprecated
    public Map<String, ArrayList<STATION_OLD>> buyPrices_OLD = new HashMap<>();

    @FXML
    private GridPane hbInteraction;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        loadFilters();
        loadDatastorage();
    }

    private void loadDatastorage()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/datastorage.fxml"));
        Pane storageBox;

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

        hbInteraction.add(storageBox, 3, 0);
    }
    private void loadFilters()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/filters.fxml"));
        Pane filterBox;

        try
        {
            filterBox = loader.load();
            filterController = loader.getController();
        } catch(IOException e)
        {
            Logger.ERROR(999, "Could not load Filters controller");
            return;
        }

        hbInteraction.add(filterBox, 1, 0);
    }

    public void setMainController(Main controller)
    {
        controller.setInteractionController(this);
        filterController.setMainController(controller);
    }
}
