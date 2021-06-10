package com.fi0x.edct.controller;

import com.fi0x.edct.Main;
import com.fi0x.edct.data.websites.GitHub;
import com.fi0x.edct.util.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProgramInfo implements Initializable
{
    private int errorCode = 0;
    private String updateUrl = "https://github.com/Fi0x/EDCT/releases";

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

        Image img = new Image(getClass().getResourceAsStream("/images/settings.png"), 20, 20, false, false);
        btnSettings.setGraphic(new ImageView(img));

        btnSettings.hoverProperty().addListener((observable, oldValue, newValue) ->
        {
            Image imgDark = new Image(getClass().getResourceAsStream("/images/settings_dark.png"), 20, 20, false, false);
            if(btnSettings.isHover()) btnSettings.setGraphic(new ImageView(imgDark));
            else btnSettings.setGraphic(new ImageView(img));
        });
    }

    @FXML
    private void openErrorPage()
    {
        openWebsite("https://github.com/Fi0x/EDCT/wiki/Errors#" + errorCode);
        lblError.setVisible(false);
    }
    @FXML
    private void reportBug()
    {
        openWebsite("https://github.com/Fi0x/EDCT/issues");
    }
    @FXML
    private void updateVersion()
    {
        openWebsite(updateUrl);
    }
    @FXML
    private void openSettings()
    {
        //TODO: open new settings page
    }

    public void setError(int code)
    {
        errorCode = code;
        lblError.setText("Error " + errorCode + " occurred");
        lblError.setVisible(true);
    }

    public void checkForUpdates()
    {
        String url = GitHub.checkForVersionUpdates();
        if(url != null)
        {
            updateUrl = url;
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
            Logger.WARNING("Could not open url in browser", e);
        }
    }
}
