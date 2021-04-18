package com.fi0x.edct.controller;

import com.fi0x.edct.util.Out;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Observable;
import java.util.Observer;

public class ControllerMain implements Observer
{
    private int currentResult;

    @FXML
    private TextField input;
    @FXML
    private CheckBox caesar;
    @FXML
    private CheckBox skytale;
    @FXML
    private TextField possibleDecryptions;
    @FXML
    private Label running;
    @FXML
    private TextField result;

    @FXML
    private void start()
    {
    }
    @FXML
    private void cancel()
    {
    }
    @FXML
    private void showResult()
    {
    }
    @Override
    public void update(Observable o, Object running)
    {
        this.running.setVisible((int) running > 0);
        this.running.setText("running");
    }
}