package com.fi0x.edct.gui.controller;

import com.fi0x.edct.Main;
import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logging.Logger;
import com.fi0x.edct.logging.MixpanelHandler;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.filesystem.SettingsHandler;
import com.fi0x.edct.logic.helper.ExternalProgram;
import com.fi0x.edct.logic.threads.Updater;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URL;
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
    @FXML
    private VBox vbSecretSettings;
    @FXML
    private TextField txtShipCargoSpace;
    @FXML
    private TextField txtLoadingTonProfit;
    @FXML
    private TextField txtUnloadingTonProfit;
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
        cbInaraDelay.valueProperty().addListener((observable, oldValue, newValue) -> updateAgeSettings());

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

        btnDetails.setText(detailedResults.name() + " Results");

        ttReddit.setText("This opens the config file for reddit-texts in the Windows-editor.\nYou can find out how to configure the file correctly in the wiki");
        ttDiscord.setText("This opens the config file for discord-texts in the Windows-editor.\nYou can find out how to configure the file correctly in the wiki");
    }

    @FXML
    private void changeDetailMode()
    {
        detailedResults = Details.values()[(detailedResults.ordinal() + 1 ) % Details.values().length];
        btnDetails.setText(detailedResults.name() + " Results");
        updateSecretVisibility(detailedResults == Details.Advanced);
        infoController.settingsStage.sizeToScene();

        SettingsHandler.storeValue("detailedResults", detailedResults);

        MainWindow.getInstance().resultsController.updateDetails(detailedResults);
    }
    @FXML
    private void clearLogs()
    {
        MixpanelHandler.addMessage(MixpanelHandler.EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("clear-logs"));
        try
        {
            FileUtils.cleanDirectory(Main.errors.getParentFile());
        } catch(IOException e)
        {
            Logger.WARNING("Could not clear the log files", e);
        }
    }
    @FXML
    private void clearDB()
    {
        MixpanelHandler.addMessage(MixpanelHandler.EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("clear-DB"));
        DBHandler.removeTradeData();

        if(Main.eddn != null) Main.eddn.interrupt();
        if(Main.updater != null) Main.updater.interrupt();

        Main.updater = new Thread(new Updater());
        Main.updater.start();
    }
    @FXML
    private void openBlacklist()
    {
        MixpanelHandler.addMessage(MixpanelHandler.EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("open-blacklist"));
        ExternalProgram.openNotepad(Main.blacklist);
    }
    @FXML
    private void openRedditConfig()
    {
        MixpanelHandler.addMessage(MixpanelHandler.EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("open-reddit-config"));
        ExternalProgram.openNotepad(Main.reddit);
    }
    @FXML
    private void openDiscordConfig()
    {
        MixpanelHandler.addMessage(MixpanelHandler.EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("open-discord-config"));
        ExternalProgram.openNotepad(Main.discord);
    }

    public static void loadSettings()
    {
        lowProfitBorder = SettingsHandler.loadInt("lowProfit", 10000);
        highProfitBorder = SettingsHandler.loadInt("highProfit", 30000);
        maxDataAge = SettingsHandler.loadInt("dataAge", 1000 * 60 * 60 * 24 * 4);
        inaraDelay = SettingsHandler.loadInt("inaraDelay", 1000 * 15);
        detailedResults = SettingsHandler.loadDetails("detailedResults", Details.Advanced);
        shipCargoSpace = SettingsHandler.loadInt("shipCargoSpace", 790);
        loadingTonProfit = SettingsHandler.loadInt("loadingProfit", 10000);
        unloadingTonProfit = SettingsHandler.loadInt("unloadingProfit", 10000);

        lowProfitBorder = Math.max(lowProfitBorder, 0);
        highProfitBorder = Math.max(highProfitBorder, 0);
        maxDataAge = Math.max(maxDataAge, 0);
        inaraDelay = Math.max(inaraDelay, 15000);
        if(shipCargoSpace < 0) shipCargoSpace = 0;
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
    private void updateSecretSettings()
    {
        if(txtShipCargoSpace.getText().length() > 0) shipCargoSpace = Integer.parseInt(txtShipCargoSpace.getText());
        if(txtLoadingTonProfit.getText().length() > 0) loadingTonProfit = Integer.parseInt(txtLoadingTonProfit.getText());
        if(txtUnloadingTonProfit.getText().length() > 0) unloadingTonProfit = Integer.parseInt(txtUnloadingTonProfit.getText());

        SettingsHandler.storeValue("shipCargoSpace", shipCargoSpace);
        SettingsHandler.storeValue("loadingProfit", loadingTonProfit);
        SettingsHandler.storeValue("unloadingProfit", unloadingTonProfit);
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
