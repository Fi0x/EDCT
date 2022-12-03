package com.fi0x.edct.gui.controller;

import com.fi0x.edct.logging.exceptions.MixpanelEvents;
import com.fi0x.edct.logic.registry.RegistryWrapper;
import com.fi0x.edct.logic.structures.FILTEROPTIONS;
import io.fi0x.javalogger.mixpanel.MixpanelHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

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
            if(newValue.length() > 9)
                txtQuantity.setText(oldValue);
            else if(!newValue.matches("\\d*"))
                txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
        });
        txtQuantity.setOnAction(e ->
        {
            mainController.updateFilters();
            RegistryWrapper.storeInt("quantity", Integer.parseInt(txtQuantity.getText()));
            MixpanelHandler.addMessage(MixpanelEvents.FILTERS_CHANGE.name(), com.fi0x.edct.Main.getProgramState());
        });
        cbCarrier.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            RegistryWrapper.storeBool("carrier", cbCarrier.isSelected());
            MixpanelHandler.addMessage(MixpanelEvents.FILTERS_CHANGE.name(), com.fi0x.edct.Main.getProgramState());
        });
        cbSurface.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            RegistryWrapper.storeBool("surface", cbSurface.isSelected());
            MixpanelHandler.addMessage(MixpanelEvents.FILTERS_CHANGE.name(), com.fi0x.edct.Main.getProgramState());
        });
        cbLandingPad.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            RegistryWrapper.storeBool("pad", cbLandingPad.isSelected());
            MixpanelHandler.addMessage(MixpanelEvents.FILTERS_CHANGE.name(), com.fi0x.edct.Main.getProgramState());
        });
        cbDemand.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            RegistryWrapper.storeBool("demand", cbDemand.isSelected());
            MixpanelHandler.addMessage(MixpanelEvents.FILTERS_CHANGE.name(), com.fi0x.edct.Main.getProgramState());
        });
        cbOdyssey.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            RegistryWrapper.storeBool("odyssey", cbOdyssey.isSelected());
            MixpanelHandler.addMessage(MixpanelEvents.FILTERS_CHANGE.name(), com.fi0x.edct.Main.getProgramState());
        });
        cbBlacklist.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            mainController.updateFilters();
            RegistryWrapper.storeBool("blacklist", cbBlacklist.isSelected());
            MixpanelHandler.addMessage(MixpanelEvents.FILTERS_CHANGE.name(), com.fi0x.edct.Main.getProgramState());
        });
        txtGalacticAverage.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9)
                txtGalacticAverage.setText(oldValue);
            else if(!newValue.matches("\\d*"))
                txtGalacticAverage.setText(newValue.replaceAll("[^\\d]", ""));
        });
        txtGalacticAverage.setOnAction(e ->
        {
            mainController.updateFilters();
            RegistryWrapper.storeInt("average", Integer.parseInt(txtGalacticAverage.getText()));
            MixpanelHandler.addMessage(MixpanelEvents.FILTERS_CHANGE.name(), com.fi0x.edct.Main.getProgramState());
        });
    }

    public void setMainController(Main controller)
    {
        mainController = controller;
    }

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
        txtQuantity.setText(String.valueOf(RegistryWrapper.getInt("quantity", 10000)));
        cbCarrier.setSelected(RegistryWrapper.getBool("carrier", false));
        cbSurface.setSelected(RegistryWrapper.getBool("surface", false));
        cbLandingPad.setSelected(RegistryWrapper.getBool("pad", false));
        cbDemand.setSelected(RegistryWrapper.getBool("demand", true));
        cbOdyssey.setSelected(RegistryWrapper.getBool("odyssey", false));
        cbBlacklist.setSelected(RegistryWrapper.getBool("blacklist", true));
        txtGalacticAverage.setText(String.valueOf(RegistryWrapper.getInt("average", 2000)));
    }
}