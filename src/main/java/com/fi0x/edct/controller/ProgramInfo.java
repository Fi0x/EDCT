package com.fi0x.edct.controller;

import com.fi0x.edct.Main;
import com.fi0x.edct.data.localstorage.db.DBHandler;
import com.fi0x.edct.telemetry.EVENT;
import com.fi0x.edct.telemetry.MixpanelHandler;
import com.fi0x.edct.util.Logger;
import com.fi0x.edct.versioncontrol.GitHub;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
        MixpanelHandler.addMessage(EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("error-label"));
        openWebsite("https://github.com/Fi0x/EDCT/wiki/Errors#" + errorCode);
        lblError.setVisible(false);
    }
    @FXML
    private void reportBug()
    {
        MixpanelHandler.addMessage(EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("bug-report"));
        openWebsite("https://github.com/Fi0x/EDCT/issues");
    }
    @FXML
    private void updateVersion()
    {
        MixpanelHandler.addMessage(EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("download-new-version"));
        openWebsite(updateUrl);
        if(assetUrl != null) openWebsite(assetUrl);
    }
    @FXML
    private void openSettings()
    {
        MixpanelHandler.addMessage(EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("open-settings"));

        FXMLLoader settingsLoader = new FXMLLoader();
        settingsLoader.setLocation(getClass().getResource("/fxml/settings.fxml"));

        settingsStage = new Stage();
        Scene scene;

        try
        {
            scene = new Scene(settingsLoader.load());
            ((Settings) settingsLoader.getController()).infoController = this;
        } catch(IOException e)
        {
            Logger.WARNING(999, "Could not load settings");
            return;
        }

        settingsStage.setTitle("EDCT - Settings by Fi0x");
        settingsStage.setScene(scene);
        settingsStage.setResizable(false);

        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.sizeToScene();
        settingsStage.showAndWait();

        MixpanelHandler.addMessage(EVENT.SETTINGS_CLOSED, MixpanelHandler.getProgramState());

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
        ArrayList<String> url = GitHub.checkForVersionUpdates();
        if(url != null && url.size() > 0)
        {
            updateUrl = url.get(0);
            if(url.size() >= 3) assetUrl = url.get((Main.portable ? 1 : 2));
            btnUpdate.setVisible(true);
            btnBugReport.setVisible(false);
        }
    }

    private void openWebsite(String url)
    {
        Desktop desktop = Desktop.getDesktop();
        try
        {
            desktop.browse(new URI(url));
        } catch(IOException | URISyntaxException e)
        {
            Logger.ERROR(992, "Could not open url in browser", e);
        }
    }
}
