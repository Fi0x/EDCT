package com.fi0x.edct.gui.controller;

import com.fi0x.edct.Main;
import com.fi0x.edct.gui.visual.SettingsWindow;
import com.fi0x.edct.logging.MixpanelHandler;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.helper.ExternalProgram;
import com.fi0x.edct.logic.versioncontrol.GitHub;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProgramInfo implements Initializable
{
    public Stage settingsStage;

    private int errorCode = 0;
    private String updateUrl = "https://github.com/Fi0x/EDCT/releases/latest";
    private String assetUrl = null;

    @FXML
    private Label lblError;
    @FXML
    private Label lblVersion;
    @FXML
    private Button btnBugReport;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnSettings;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        lblVersion.setText("Current version: " + Main.version);

        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/settings.png")), 20, 20, false, false);
        btnSettings.setGraphic(new ImageView(img));

        btnSettings.hoverProperty().addListener((observable, oldValue, newValue) ->
        {
            Image imgDark = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/settings_dark.png")), 20, 20, false, false);
            if(btnSettings.isHover()) btnSettings.setGraphic(new ImageView(imgDark));
            else btnSettings.setGraphic(new ImageView(img));
        });
    }

    @FXML
    private void openErrorPage()
    {
        MixpanelHandler.addMessage(MixpanelHandler.EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("error-label"));
        ExternalProgram.openWebsite("https://github.com/Fi0x/EDCT/wiki/Errors#" + errorCode);
        lblError.setVisible(false);
    }
    @FXML
    private void reportBug()
    {
        MixpanelHandler.addMessage(MixpanelHandler.EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("bug-report"));
        ExternalProgram.openWebsite("https://github.com/Fi0x/EDCT/issues");
    }
    @FXML
    private void updateVersion()
    {
        MixpanelHandler.addMessage(MixpanelHandler.EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("download-new-version"));
        ExternalProgram.openWebsite(updateUrl);
        if(assetUrl != null) ExternalProgram.openWebsite(assetUrl);
    }
    @FXML
    private void openSettings()
    {
        MixpanelHandler.addMessage(MixpanelHandler.EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("open-settings"));

        settingsStage = new SettingsWindow(this);
        settingsStage.showAndWait();

        MixpanelHandler.addMessage(MixpanelHandler.EVENT.SETTINGS_CLOSED, MixpanelHandler.getProgramState());

        DBHandler.removeOldEntries();
    }

    public void setError(int code)
    {
        errorCode = code;
        lblError.setText("Error " + errorCode + " occurred");
        lblError.setVisible(true);
    }

    public void checkForUpdates()
    {
        ArrayList<String> url = GitHub.getNewerVersion();
        if(url != null && url.size() > 0)
        {
            updateUrl = url.get(0);
            if(url.size() >= 3)
            {
                switch (Main.versionType)
                {
                    case PORTABLE:
                        assetUrl = url.get(1);
                        break;
                    case INSTALLER:
                        assetUrl = url.get(2);
                        break;
                    case JAR:
                        assetUrl = url.get(3);
                        break;
                }
            }
            btnUpdate.setVisible(true);
            btnBugReport.setVisible(false);
        }
    }
}
