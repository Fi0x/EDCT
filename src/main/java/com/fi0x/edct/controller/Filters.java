package com.fi0x.edct.controller;

import com.fi0x.edct.telemetry.EVENT;
import com.fi0x.edct.telemetry.MixpanelHandler;
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
    @FXML
    private CheckBox cbBlacklist;
    @FXML
    private TextField txtGalacticAverage;

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
                MixpanelHandler.addMessage(EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
            }
        });
        cbCarrier.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("carrier", cbCarrier.isSelected());
            MixpanelHandler.addMessage(EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        cbSurface.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("surface", cbSurface.isSelected());
            MixpanelHandler.addMessage(EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        cbLandingPad.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("pad", cbLandingPad.isSelected());
            MixpanelHandler.addMessage(EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        cbDemand.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("demand", cbDemand.isSelected());
            MixpanelHandler.addMessage(EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        cbOdyssey.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("odyssey", cbOdyssey.isSelected());
            MixpanelHandler.addMessage(EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        cbBlacklist.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            updateFilters();
            SettingsHandler.storeValue("blacklist", cbBlacklist.isSelected());
            MixpanelHandler.addMessage(EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        txtGalacticAverage.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtGalacticAverage.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtGalacticAverage.setText(newValue.replaceAll("[^\\d]", ""));
            else
            {
                updateFilters();
                SettingsHandler.storeValue("quantity", txtGalacticAverage.getText());
                MixpanelHandler.addMessage(EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
            }
        });
    }

    public void updateFilters()
    {
        int amount = Integer.parseInt(txtQuantity.getText().length() > 0 ? txtQuantity.getText() : "0");
        long avg = Integer.parseInt(txtGalacticAverage.getText().length() > 0 ? txtGalacticAverage.getText() : "0");
        mainController.updateFilters(avg, amount, cbDemand.isSelected(), !cbLandingPad.isSelected(), !cbCarrier.isSelected(), !cbSurface.isSelected(), !cbOdyssey.isSelected(), cbBlacklist.isSelected());
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
        cbBlacklist.setSelected(SettingsHandler.loadBoolean("blacklist", true));
        txtGalacticAverage.setText(String.valueOf(SettingsHandler.loadInt("average", 2000)));
    }
}