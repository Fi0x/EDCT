package com.fi0x.edct.gui.controller;

import com.fi0x.edct.logging.MixpanelHandler;
import com.fi0x.edct.logic.filesystem.SettingsHandler;
import com.fi0x.edct.logic.structures.FILTEROPTIONS;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.ResourceBundle;

public class Filters implements Initializable
{
    private Main mainController;
    private static Filters instance;

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
        instance = this;
        loadFilters();

        txtQuantity.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtQuantity.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
            else
            {
                mainController.updateFilters();
                SettingsHandler.storeValue("quantity", txtQuantity.getText());
                MixpanelHandler.addMessage(MixpanelHandler.EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
            }
        });
        cbCarrier.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            SettingsHandler.storeValue("carrier", cbCarrier.isSelected());
            MixpanelHandler.addMessage(MixpanelHandler.EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        cbSurface.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            SettingsHandler.storeValue("surface", cbSurface.isSelected());
            MixpanelHandler.addMessage(MixpanelHandler.EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        cbLandingPad.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            SettingsHandler.storeValue("pad", cbLandingPad.isSelected());
            MixpanelHandler.addMessage(MixpanelHandler.EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        cbDemand.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            SettingsHandler.storeValue("demand", cbDemand.isSelected());
            MixpanelHandler.addMessage(MixpanelHandler.EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        cbOdyssey.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            SettingsHandler.storeValue("odyssey", cbOdyssey.isSelected());
            MixpanelHandler.addMessage(MixpanelHandler.EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        cbBlacklist.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            SettingsHandler.storeValue("blacklist", cbBlacklist.isSelected());
            MixpanelHandler.addMessage(MixpanelHandler.EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
        });
        txtGalacticAverage.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtGalacticAverage.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtGalacticAverage.setText(newValue.replaceAll("[^\\d]", ""));
            else
            {
                mainController.updateFilters();
                SettingsHandler.storeValue("quantity", txtGalacticAverage.getText());
                MixpanelHandler.addMessage(MixpanelHandler.EVENT.FILTERS_CHANGE, MixpanelHandler.getProgramState());
            }
        });
    }

    public void setMainController(Main controller)
    {
        mainController = controller;
    }

    @Nullable
    public static Filters getInstance()
    {
        return instance;
    }
    public FILTEROPTIONS getFilterSettings()
    {
        FILTEROPTIONS fo = new FILTEROPTIONS();

        fo.average = Integer.parseInt(txtGalacticAverage.getText().length() > 0 ? txtGalacticAverage.getText() : "0");
        fo.amount = Integer.parseInt(txtQuantity.getText().length() > 0 ? txtQuantity.getText() : "0");
        fo.demand = cbDemand.isSelected();
        fo.landingPad = cbLandingPad.isSelected();
        fo.carrier = cbCarrier.isSelected();
        fo.surface = cbSurface.isSelected();
        fo.odyssey = cbOdyssey.isSelected();
        fo.blacklist = cbBlacklist.isSelected();

        return fo;
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