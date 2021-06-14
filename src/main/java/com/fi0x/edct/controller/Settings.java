package com.fi0x.edct.controller;

import com.fi0x.edct.Main;
import com.fi0x.edct.MainWindow;
import com.fi0x.edct.data.Updater;
import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.util.Logger;
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
    public static int maxDataAge = 1000 * 60 * 60 * 24 * 4;//TODO: Verify that time always gets converted correctly
    public static int inaraDelay = 15000;//TODO: Verify that time always gets converted correctly
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
            cbInaraDelay.setValue("minutes");
        } else
        {
            txtInaraDelay.setText(String.valueOf(inaraDelay / (1000)));
            cbInaraDelay.setValue("seconds");
        }
    }

    @FXML
    private void changeDetailMode()
    {
        detailedResults = !detailedResults;
        if(detailedResults) btnDetails.setText("Simple Results");
        else btnDetails.setText("Detailed Results");

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
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(com.fi0x.edct.Main.settings.toPath(), StandardCharsets.UTF_8));
            for(String line : fileContent)
            {
                String[] setting = line.split("=");
                if(setting.length < 2) continue;

                switch(setting[0])
                {
                    case "lowProfit":
                        txtLowProfit.setText(setting[1]);
                        break;
                    case "highProfit":
                        txtHighProfit.setText(setting[1]);
                        break;
                    case "dataAge":
                        txtMaxAge.setText(setting[1]);
                        break;
                    case "inaraDelay":
                        txtInaraDelay.setText(setting[1]);
                        break;
                }
            }
        } catch(IOException e)
        {
            Logger.WARNING("Could not read settings from local file", e);
        }

        lowProfitBorder = Math.max(lowProfitBorder, 0);
        highProfitBorder = Math.max(highProfitBorder, 0);
        maxDataAge = Math.max(maxDataAge, 0);
        inaraDelay = Math.max(inaraDelay, 15000);
    }
    private void updateProfitBorder()
    {
        if(txtLowProfit.getText().length() > 0) lowProfitBorder = Integer.parseInt(txtLowProfit.getText());
        if(txtHighProfit.getText().length() > 0) highProfitBorder = Integer.parseInt(txtHighProfit.getText());

        try
        {
            List<String> fileContent = new ArrayList<>();

            fileContent.add("lowProfit=" + lowProfitBorder);
            fileContent.add("highProfit=" + highProfitBorder);

            Files.write(com.fi0x.edct.Main.settings.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.WARNING("Could not write profit settings to local file", e);
        }
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

        try
        {
            List<String> fileContent = new ArrayList<>();

            fileContent.add("dataAge=" + maxDataAge);
            fileContent.add("inaraDelay=" + inaraDelay);

            Files.write(com.fi0x.edct.Main.settings.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.WARNING("Could not write age settings to local file", e);
        }
    }
}
