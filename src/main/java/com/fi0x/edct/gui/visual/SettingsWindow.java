package com.fi0x.edct.gui.visual;

import com.fi0x.edct.gui.controller.ProgramInfo;
import com.fi0x.edct.gui.controller.Settings;
import com.fi0x.edct.logging.LogName;
import io.fi0x.javalogger.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsWindow extends Stage
{
    public SettingsWindow(ProgramInfo infoController)
    {
        FXMLLoader settingsLoader = new FXMLLoader();
        settingsLoader.setLocation(getClass().getResource("/fxml/settings.fxml"));

        Scene scene;
        try
        {
            scene = new Scene(settingsLoader.load());
            ((Settings) settingsLoader.getController()).infoController = infoController;
        } catch(IOException e)
        {
            Logger.log("Could not load settings", LogName.WARNING, e, 999);
            return;
        }

        setTitle("EDCT - Settings by Fi0x");
        setScene(scene);
        setResizable(false);

        initModality(Modality.APPLICATION_MODAL);
        sizeToScene();
    }
}
