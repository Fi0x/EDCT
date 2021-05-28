package com.fi0x.edct.controller;

import com.fi0x.edct.data.webconnection.GitHub;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ProgramInfo
{
    private int errorCode = 0;

    @FXML
    private Label lblError;
    @FXML
    private Button btnUpdate;

    @FXML
    private void openErrorPage()
    {
        //TODO: Open a GitHub wiki-page that explains the error code
        lblError.setVisible(false);
    }
    @FXML
    private void updateVersion()
    {
        Desktop desktop = Desktop.getDesktop();
        try
        {
            desktop.browse(new URI("https://github.com/Fi0x/EDCT/releases"));
        } catch(IOException | URISyntaxException ignored)
        {
        }
    }

    public void setError(int code)
    {
        errorCode = code;
        lblError.setText("Error " + errorCode + " occured");
        lblError.setVisible(true);
    }

    public void checkForUpdates()
    {
        if(GitHub.checkForVersionUpdates()) btnUpdate.setVisible(true);
    }
}
