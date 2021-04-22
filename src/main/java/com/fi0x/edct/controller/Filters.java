package com.fi0x.edct.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Filters implements Initializable
{
    private Main mainController;

    @FXML
    private TextField quantity;
    @FXML
    private TextField profit;
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
            else updateFilters();

        });
        profit.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(!newValue.matches("\\d*")) profit.setText(newValue.replaceAll("[^\\d]", ""));
            else updateFilters();

        });
        cbCarrier.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbSurface.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbLandingPad.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbDemand.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
    }

    public void updateFilters()
    {
        mainController.updateFilters(Integer.parseInt(quantity.getText()), Integer.parseInt(profit.getText()), cbDemand.isSelected(), !cbLandingPad.isSelected(), !cbCarrier.isSelected(), !cbSurface.isSelected());
    }

    public int getMinProfit()
    {
        return Integer.parseInt(profit.getText());
    }

    public void setMainController(Main controller)
    {
        mainController = controller;
    }
}