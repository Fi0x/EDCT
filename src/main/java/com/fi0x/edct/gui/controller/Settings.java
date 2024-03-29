package com.fi0x.edct.gui.controller;

import com.fi0x.edct.Main;
import com.fi0x.edct.gui.visual.CustomAlert;
import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logging.exceptions.MixpanelEvents;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.helper.ExternalProgram;
import com.fi0x.edct.logic.registry.RegistryWrapper;
import com.fi0x.edct.logic.threads.Updater;
import io.fi0x.javalogger.logging.Logger;
import io.fi0x.javalogger.mixpanel.MixpanelHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Settings implements Initializable
{
    public ProgramInfo infoController;

    public static int lowProfitBorder = 10000;
    public static int highProfitBorder = 30000;
    public static int maxDataAge = 1000 * 60 * 60 * 24 * 4;
    public static int inaraDelay = 15000;
    public static Details detailedResults = Details.Advanced;
    public static int shipCargoSpace = 790;
    public static int loadingTonProfit = 10000;
    public static int unloadingTonProfit = 10000;
    public static String carrierName;

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
    private Button btnDetails;
    @FXML
    private VBox vbSecretSettings;
    @FXML
    private TextField txtShipCargoSpace;
    @FXML
    private TextField txtLoadingTonProfit;
    @FXML
    private TextField txtUnloadingTonProfit;
    @FXML
    private TextField txtCarrierName;
    @FXML
    private Tooltip ttReddit;
    @FXML
    private Tooltip ttDiscord;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        loadSettings();
        txtLowProfit.setText(String.valueOf(lowProfitBorder));
        txtHighProfit.setText(String.valueOf(highProfitBorder));
        updateSecretVisibility(detailedResults == Details.Advanced);

        txtLowProfit.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtLowProfit.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtLowProfit.setText(newValue.replaceAll("[^\\d]", ""));
            else
            {
                updateProfitBorder();
                MainWindow.getInstance().resultsController.displayResults();
            }
        });
        txtLowProfit.setText(String.valueOf(lowProfitBorder));

        txtHighProfit.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtHighProfit.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtHighProfit.setText(newValue.replaceAll("[^\\d]", ""));
            else
            {
                updateProfitBorder();
                MainWindow.getInstance().resultsController.displayResults();
            }
        });
        txtHighProfit.setText(String.valueOf(highProfitBorder));

        txtMaxAge.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtMaxAge.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtMaxAge.setText(newValue.replaceAll("[^\\d]", ""));
            else updateAgeSettings();
        });
        cbDataAge.valueProperty().addListener((observable, oldValue, newValue) -> updateAgeSettings());

        txtInaraDelay.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtInaraDelay.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtInaraDelay.setText(newValue.replaceAll("[^\\d]", ""));
            else updateAgeSettings();
        });

        setCorrectAgeFields();

        txtShipCargoSpace.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtShipCargoSpace.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtShipCargoSpace.setText(newValue.replaceAll("[^\\d]", ""));
            else
            {
                updateSecretSettings();
                MainWindow.getInstance().resultsController.displayResults();
            }
        });
        txtShipCargoSpace.setText(String.valueOf(shipCargoSpace));

        txtLoadingTonProfit.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtLoadingTonProfit.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtLoadingTonProfit.setText(newValue.replaceAll("[^\\d]", ""));
            else
            {
                updateSecretSettings();
                MainWindow.getInstance().resultsController.displayResults();
            }
        });
        txtLoadingTonProfit.setText(String.valueOf(loadingTonProfit));

        txtUnloadingTonProfit.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtUnloadingTonProfit.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtUnloadingTonProfit.setText(newValue.replaceAll("[^\\d]", ""));
            else
            {
                updateSecretSettings();
                MainWindow.getInstance().resultsController.displayResults();
            }
        });
        txtUnloadingTonProfit.setText(String.valueOf(unloadingTonProfit));

        txtCarrierName.textProperty().addListener((observable, oldValue, newValue) -> updateSecretSettings());
        txtCarrierName.setText(carrierName);

        btnDetails.setText(detailedResults.name() + " Results");

        ttReddit.setText("This opens the config file for reddit-texts in the Windows-editor.\nYou can find out how to configure the file correctly in the wiki");
        ttDiscord.setText("This opens the config file for discord-texts in the Windows-editor.\nYou can find out how to configure the file correctly in the wiki");
    }

    @FXML
    private void provideFeedback()
    {
        MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "feedback");}});

        Map<String, String> results = new HashMap<>();
        boolean noFlag = false;

        Alert alertUseful = new CustomAlert(Alert.AlertType.NONE, "Do you think this tool is useful?", ButtonType.YES, ButtonType.NO);
        alertUseful.showAndWait();
        results.put("usefulTool", alertUseful.getResult() == ButtonType.YES ? "Yes" : "No");
        if(alertUseful.getResult() == ButtonType.NO)
            noFlag = true;

        Alert alertEasyUse = new CustomAlert(Alert.AlertType.NONE, "Is the tool easy to use and understand?", ButtonType.YES, ButtonType.NO);
        alertEasyUse.showAndWait();
        results.put("easyUse", alertEasyUse.getResult() == ButtonType.YES ? "Yes" : "No");
        if(alertEasyUse.getResult() == ButtonType.NO)
            noFlag = true;

        Alert alertCorrectInfo = new CustomAlert(Alert.AlertType.NONE, "Is the provided information always correct?", ButtonType.YES, ButtonType.NO);
        alertCorrectInfo.showAndWait();
        results.put("correctInformation", alertCorrectInfo.getResult() == ButtonType.YES ? "Yes" : "No");
        if(alertCorrectInfo.getResult() == ButtonType.NO)
            noFlag = true;

        Alert alertMissingFilter = new CustomAlert(Alert.AlertType.NONE, "Is there a filter-option missing you would like to use?", ButtonType.YES, ButtonType.NO);
        alertMissingFilter.showAndWait();
        results.put("missingFilter", alertMissingFilter.getResult() == ButtonType.YES ? "Yes" : "No");
        if(alertMissingFilter.getResult() == ButtonType.YES)
            noFlag = true;

        if(noFlag)
        {
            Alert alertGithub = new CustomAlert(Alert.AlertType.NONE, "Would you like to provide more information and create a GitHub issue?", ButtonType.YES, ButtonType.NO);
            alertGithub.showAndWait();
            results.put("githubIssue", alertGithub.getResult() == ButtonType.YES ? "Yes" : "No");
            if(alertGithub.getResult() == ButtonType.YES)
                ExternalProgram.openWebsite("https://github.com/Fi0x/EDCT/issues/new/choose");
        }

        MixpanelHandler.addMessage(MixpanelEvents.FEEDBACK.name(), results);

        Alert alertThanks = new CustomAlert(Alert.AlertType.INFORMATION, "Your feedback was sent to the developer", ButtonType.CLOSE);
        alertThanks.setHeaderText("Thank you!");
        alertThanks.showAndWait();
    }
    @FXML
    private void changeDetailMode()
    {
        detailedResults = Details.values()[(detailedResults.ordinal() + 1 ) % Details.values().length];
        btnDetails.setText(detailedResults.name() + " Results");
        updateSecretVisibility(detailedResults == Details.Advanced);
        infoController.settingsStage.sizeToScene();

        RegistryWrapper.storeString("detailedResults", detailedResults.name());

        MainWindow.getInstance().resultsController.updateDetails(detailedResults);
    }
    @FXML
    private void clearLogs()
    {
        MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "clear-logs");}});
        try
        {
            Main.clearLogs();
            new CustomAlert(Alert.AlertType.NONE, "Log files cleared", ButtonType.CLOSE).showAndWait();
        } catch(IOException e)
        {
            Logger.log("Could not clear the log files", LogName.WARNING, e);
        }
    }
    @FXML
    private void clearDB()
    {
        MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "clear-DB");}});

        int stationEntries = DBHandler.countStationEntries();
        int tradeEntries = DBHandler.countTradeEntries();

        String alertText = "Are you sure you want to clear the database?";
        if(stationEntries > 0) alertText += " It contains " + stationEntries + " stations";
        else if(tradeEntries > 0) alertText += " It contains " + tradeEntries + " trades";
        if(stationEntries > 0 && tradeEntries > 0) alertText += " and " + tradeEntries + " trades";

        Alert alert = new CustomAlert(Alert.AlertType.WARNING, alertText, ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();

        if(alert.getResult() == ButtonType.CANCEL)
            return;

        DBHandler.removeTradeData();
        DBHandler.removeStationDATA();

        if(Main.eddn != null) Main.eddn.interrupt();
        if(Main.updater != null) Main.updater.interrupt();

        Main.updater = new Thread(new Updater());
        Main.updater.start();
    }
    @FXML
    private void openBlacklist()
    {
        MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "open-blacklist");}});
        ExternalProgram.openNotepad(Main.blacklist);
    }
    @FXML
    private void openRedditConfig()
    {
        MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "open-reddit-config");}});
        ExternalProgram.openNotepad(Main.reddit);
    }
    @FXML
    private void openDiscordConfig()
    {
        MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "open-discord-config");}});
        ExternalProgram.openNotepad(Main.discord);
    }

    public static void loadSettings()
    {
        lowProfitBorder = RegistryWrapper.getInt("lowProfit", 10000);
        highProfitBorder = RegistryWrapper.getInt("highProfit", 30000);
        maxDataAge = RegistryWrapper.getInt("dataAge", 1000 * 60 * 60 * 24 * 4);
        inaraDelay = RegistryWrapper.getInt("inaraDelay", 15000);
        detailedResults = Settings.Details.valueOf(RegistryWrapper.getString("detailedResults", Details.Advanced.name()));
        shipCargoSpace = RegistryWrapper.getInt("shipCargoSpace", 790);
        loadingTonProfit = RegistryWrapper.getInt("loadingProfit", 10000);
        unloadingTonProfit = RegistryWrapper.getInt("unloadingProfit", 10000);
        carrierName = RegistryWrapper.getString("carrierName", "");

        lowProfitBorder = Math.max(lowProfitBorder, 0);
        highProfitBorder = Math.max(highProfitBorder, 0);
        maxDataAge = Math.max(maxDataAge, 0);
        inaraDelay = Math.max(inaraDelay, 15000);
        if(shipCargoSpace < 0)
            shipCargoSpace = 0;
    }

    private void updateProfitBorder()
    {
        if(txtLowProfit.getText().length() > 0) lowProfitBorder = Integer.parseInt(txtLowProfit.getText());
        if(txtHighProfit.getText().length() > 0) highProfitBorder = Integer.parseInt(txtHighProfit.getText());

        RegistryWrapper.storeInt("lowProfit", lowProfitBorder);
        RegistryWrapper.storeInt("highProfit", highProfitBorder);
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
        }

        RegistryWrapper.storeInt("dataAge", maxDataAge);
        RegistryWrapper.storeInt("inaraDelay", inaraDelay);
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

        txtInaraDelay.setText(String.valueOf(inaraDelay / (1000)));
    }
    private void updateSecretSettings()
    {
        if(txtShipCargoSpace.getText().length() > 0)
            shipCargoSpace = Integer.parseInt(txtShipCargoSpace.getText());
        if(txtLoadingTonProfit.getText().length() > 0)
            loadingTonProfit = Integer.parseInt(txtLoadingTonProfit.getText());
        if(txtUnloadingTonProfit.getText().length() > 0)
            unloadingTonProfit = Integer.parseInt(txtUnloadingTonProfit.getText());
        if(txtCarrierName.getText().length() > 0)
            carrierName = txtCarrierName.getText();

        RegistryWrapper.storeInt("shipCargoSpace", shipCargoSpace);
        RegistryWrapper.storeInt("loadingProfit", loadingTonProfit);
        RegistryWrapper.storeInt("unloadingProfit", unloadingTonProfit);
        RegistryWrapper.storeString("carrierName", carrierName);
    }
    private void updateSecretVisibility(boolean visible)
    {
        vbSecretSettings.setVisible(visible);
        vbSecretSettings.setManaged(visible);
    }

    public enum Details
    {
        Simple,
        Normal,
        Advanced
    }
}
