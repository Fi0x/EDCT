package com.fi0x.edct.controller;

import com.fi0x.edct.Main;
import com.fi0x.edct.MainWindow;
import com.fi0x.edct.data.Updater;
import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.util.Logger;
import com.fi0x.edct.util.SettingsHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Settings implements Initializable
{
    public static int lowProfitBorder = 10000;
    public static int highProfitBorder = 30000;
    public static int maxDataAge = 1000 * 60 * 60 * 24 * 4;
    public static int inaraDelay = 15000;
    public static boolean detailedResults = true;

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
    @FXML
    private ChoiceBox<String> cbInaraDelay;
    @FXML
    private Button btnDetails;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        loadSettings();

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

        txtInaraDelay.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtInaraDelay.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtInaraDelay.setText(newValue.replaceAll("[^\\d]", ""));
            else updateAgeSettings();
        });

        setCorrectAgeFields();

        if(detailedResults) btnDetails.setText("Simple Results");
        else btnDetails.setText("Detailed Results");
    }

    @FXML
    private void changeDetailMode()
    {
        detailedResults = !detailedResults;
        if(detailedResults) btnDetails.setText("Simple Results");
        else btnDetails.setText("Detailed Results");

        SettingsHandler.storeValue("detailedResults", detailedResults);

        MainWindow.getInstance().resultsController.updateDetails(detailedResults);
    }
    @FXML
    private void clearLogs()
    {
        try
        {
            List<String> fileContent = new ArrayList<>();
            Files.write(Main.errors.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.WARNING("Could not clear log-file", e);
        }
    }
    @FXML
    private void clearDB()
    {
        DBHandler.getInstance().removeTradeData();
        if(Main.updater != null) Main.updater.interrupt();
        Main.updater = new Thread(new Updater());
        Main.updater.start();
    }

    private void loadSettings()
    {
        lowProfitBorder = SettingsHandler.loadInt("lowProfit", 10000);
        highProfitBorder = SettingsHandler.loadInt("highProfit", 30000);
        maxDataAge = SettingsHandler.loadInt("dataAge", 1000 * 60 * 60 * 24 * 4);
        inaraDelay = SettingsHandler.loadInt("inaraDelay", 1000 * 15);
        detailedResults = SettingsHandler.loadBoolean("detailedResults", true);

        lowProfitBorder = Math.max(lowProfitBorder, 0);
        highProfitBorder = Math.max(highProfitBorder, 0);
        maxDataAge = Math.max(maxDataAge, 0);
        inaraDelay = Math.max(inaraDelay, 15000);

        txtLowProfit.setText(String.valueOf(lowProfitBorder));
        txtHighProfit.setText(String.valueOf(highProfitBorder));

        setCorrectAgeFields();
    }
    private void updateProfitBorder()
    {
        if(txtLowProfit.getText().length() > 0) lowProfitBorder = Integer.parseInt(txtLowProfit.getText());
        if(txtHighProfit.getText().length() > 0) highProfitBorder = Integer.parseInt(txtHighProfit.getText());

        SettingsHandler.storeValue("lowProfit", lowProfitBorder);
        SettingsHandler.storeValue("highProfit", highProfitBorder);
    }
    private void updateAgeSettings()
    {
        if(txtMaxAge.getText().length() > 0)
        {
            maxDataAge = Integer.parseInt(txtMaxAge.getText()) * 1000 * 60 * 60;
            if(cbDataAge.getValue().equals("days")) maxDataAge *= 24;
        }
        if(txtInaraDelay.getText().length() > 0)
        {
            inaraDelay = Integer.parseInt(txtInaraDelay.getText()) * 1000;
            if(cbInaraDelay.getValue().equals("minutes")) inaraDelay *= 60;
        }

        SettingsHandler.storeValue("dataAge", maxDataAge);
        SettingsHandler.storeValue("inaraDelay", inaraDelay);
    }
    private void setCorrectAgeFields()
    {
        if(maxDataAge >= 1000 * 60 * 60 * 24)
        {
            txtMaxAge.setText(String.valueOf(maxDataAge / (1000 * 60 * 60 * 24)));
            cbDataAge.setValue("days");
        } else
        {
            txtMaxAge.setText(String.valueOf(maxDataAge / (1000 * 60 * 60)));
            cbDataAge.setValue("hours");
        }

        if(inaraDelay >= 1000 * 60)
        {
            txtInaraDelay.setText(String.valueOf(inaraDelay / (1000 * 60)));
            cbInaraDelay.setValue("minutes");
        } else
        {
            txtInaraDelay.setText(String.valueOf(inaraDelay / (1000)));
            cbInaraDelay.setValue("seconds");
        }
    }
}
