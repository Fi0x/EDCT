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
    private TextField txtQuantity;
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
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtQuantity.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
            else updateFilters();

        });
        cbCarrier.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbSurface.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbLandingPad.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbDemand.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
    }

    public void updateFilters()
    {
        int amount = Integer.parseInt(txtQuantity.getText().length() > 0 ? txtQuantity.getText() : "0");
        mainController.updateFilters(amount, cbDemand.isSelected(), !cbLandingPad.isSelected(), !cbCarrier.isSelected(), !cbSurface.isSelected());
    }

    public void setMainController(Main controller)
    {
        mainController = controller;
    }
}