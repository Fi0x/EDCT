package com.fi0x.edct.gui.controller;

import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logic.structures.TRADE;
import io.fi0x.javalogger.logging.Logger;
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
    public Main mainController;
    public Datastorage storageController;
    public Filters filterController;

    public Map<String, ArrayList<TRADE>> importPrices = new HashMap<>();
    public Map<String, ArrayList<TRADE>> exportPrices = new HashMap<>();

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
            Logger.log("Could not load Datastorage controller", LogName.getError(999), e, 999);
            return;
        }

        hbInteraction.add(storageBox, 2, 0);
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
            Logger.log("Could not load Filters controller", LogName.getError(999), e, 999);
            return;
        }

        hbInteraction.add(filterBox, 0, 0);
    }

    public void setMainController(Main controller)
    {
        controller.setInteractionController(this);
        mainController = controller;
        filterController.setMainController(controller);
    }
}
