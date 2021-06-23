package com.fi0x.edct.controller;

import com.fi0x.edct.util.SettingsHandler;
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
    public TextField txtQuantity;
    @FXML
    private CheckBox cbCarrier;
    @FXML
    private CheckBox cbSurface;
    @FXML
    private CheckBox cbLandingPad;
    @FXML
    private CheckBox cbDemand;
    @FXML
    private CheckBox cbOdyssey;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        loadFilters();

        txtQuantity.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtQuantity.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
            else
            {
                updateFilters();
                SettingsHandler.storeValue("quantity", txtQuantity.getText());
            }

        });
        cbCarrier.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("carrier", cbCarrier.isSelected());
        });
        cbSurface.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("surface", cbSurface.isSelected());
        });
        cbLandingPad.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("pad", cbLandingPad.isSelected());
        });
        cbDemand.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("demand", cbDemand.isSelected());
        });
        cbOdyssey.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("odyssey", cbOdyssey.isSelected());
        });
    }

    public void updateFilters()
    {
        int amount = Integer.parseInt(txtQuantity.getText().length() > 0 ? txtQuantity.getText() : "0");
        mainController.updateFilters(amount, cbDemand.isSelected(), !cbLandingPad.isSelected(), !cbCarrier.isSelected(), !cbSurface.isSelected(), !cbOdyssey.isSelected());
    }

    public void setMainController(Main controller)
    {
        mainController = controller;
    }

    private void loadFilters()
    {
        txtQuantity.setText(String.valueOf(SettingsHandler.loadInt("quantity", 10000)));
        cbCarrier.setSelected(SettingsHandler.loadBoolean("carrier", false));
        cbSurface.setSelected(SettingsHandler.loadBoolean("surface", false));
        cbLandingPad.setSelected(SettingsHandler.loadBoolean("pad", false));
        cbDemand.setSelected(SettingsHandler.loadBoolean("demand", true));
        cbOdyssey.setSelected(SettingsHandler.loadBoolean("odyssey", false));
    }
}