package com.fi0x.edct.controller;

import com.fi0x.edct.dbconnection.RequestThread;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Interaction implements Initializable
{
    @FXML
    private TextField quantity;
    @FXML
    private CheckBox cbCarrier;
    @FXML
    private CheckBox cbSurface;
    @FXML
    private CheckBox cbLandingPad;
    @FXML
    private CheckBox cbDemand;
    @FXML
    public Button btnStart;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Thread threadReq = new Thread(new RequestThread(this, 0));
        threadReq.start();

        quantity.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(!newValue.matches("\\d*")) quantity.setText(newValue.replaceAll("[^\\d]", ""));
            else updateFilters();

        });
        cbCarrier.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbSurface.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbLandingPad.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbDemand.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
    }

    @FXML
    private void calculate()
    {
        btnStart.setVisible(false);

        Thread threadReq = new Thread(new RequestThread(this, 1));
        threadReq.start();
    }
}
