package com.fi0x.edct.controller;

import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.dbconnection.RequestThread;
import com.fi0x.edct.util.Out;
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
    private Main mainController;
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

        Thread threadReq = new Thread(new RequestThread(this, false));
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
        } catch(IOException ignored)
        {
            Out.newBuilder("Could not load datastorage GUI elements").always().ERROR().print();
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
        } catch(IOException ignored)
        {
            Out.newBuilder("Could not load datastorage GUI elements").always().ERROR().print();
            return;
        }

        hbInteraction.getChildren().add(filterBox);
    }

    public void setMainController(Main controller)
    {
        mainController = controller;
        mainController.setInteractionController(this);
        filterController.setMainController(mainController);
    }
}
