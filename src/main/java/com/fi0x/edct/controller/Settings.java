package com.fi0x.edct.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Settings implements Initializable
{
    public static int lowProfitBorder = 10000;
    public static int highProfitBorder = 30000;
    public static int maxDataAge = 1000 * 60 * 60 * 24 * 4;
    public static int inaraDelay = 15000;

    @FXML
    private TextField txtLowProfit;
    @FXML
    private TextField txtHighProfit;
    @FXML
    private TextField txtMaxAge;
    @FXML
    private ChoiceBox<String> cbDataAge;
    @FXML
    private TextField txtInaraDelay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //TODO: Load settings from file if they exist
        txtLowProfit.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtLowProfit.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtLowProfit.setText(newValue.replaceAll("[^\\d]", ""));
            else updateProfitBorder();
        });
        txtLowProfit.setText(String.valueOf(lowProfitBorder));

        txtHighProfit.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtHighProfit.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtHighProfit.setText(newValue.replaceAll("[^\\d]", ""));
            else updateProfitBorder();
        });
        txtHighProfit.setText(String.valueOf(highProfitBorder));

        txtMaxAge.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtMaxAge.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtMaxAge.setText(newValue.replaceAll("[^\\d]", ""));
            else updateAgeSettings();
        });
        if(maxDataAge >= 1000 * 60 * 60 * 24)
        {
            txtMaxAge.setText(String.valueOf(maxDataAge / (1000 * 60 * 60 * 24)));
            cbDataAge.setValue("days");
        } else
        {
            txtMaxAge.setText(String.valueOf(maxDataAge / (1000 * 60 * 60)));
            cbDataAge.setValue("hours");
        }

        txtInaraDelay.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtInaraDelay.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtInaraDelay.setText(newValue.replaceAll("[^\\d]", ""));
            else updateAgeSettings();
        });
        if(inaraDelay >= 1000 * 60)
        {
            txtInaraDelay.setText(String.valueOf(inaraDelay / (1000 * 60)));
            cbDataAge.setValue("minutes");
        } else
        {
            txtInaraDelay.setText(String.valueOf(inaraDelay / (1000)));
            cbDataAge.setValue("seconds");
        }
    }

    @FXML
    private void changeDetailMode()
    {
        //TODO: Change between detailed and simple display mode
        //TODO: Display detailed or simple trade data
    }
    @FXML
    private void clearLogs()
    {
        //TODO: Clear log-file
    }
    @FXML
    private void clearDB()
    {
        //TODO: Clear local db
        //TODO: Start initialization phase again
    }

    private void updateProfitBorder()
    {
        //TODO: Store low and high profit border in settings file
        //TODO: Update low and high profit border
    }
    private void updateAgeSettings()
    {
        //TODO: Store max age and inara delay in settings file
        //TODO: Update max age and inara delay
    }
}
