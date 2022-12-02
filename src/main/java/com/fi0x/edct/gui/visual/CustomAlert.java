package com.fi0x.edct.gui.visual;

import com.fi0x.edct.logging.LogName;
import io.fi0x.javalogger.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.net.URL;

public class CustomAlert extends Alert
{
    public CustomAlert(AlertType alertType)
    {
        super(alertType);
        setCSS(alertType);
        initStyle(StageStyle.UNDECORATED);
    }
    public CustomAlert(AlertType alertType, String message, ButtonType... buttonTypes)
    {
        super(alertType, message, buttonTypes);
        setCSS(alertType);
        initStyle(StageStyle.UNDECORATED);
    }

    private void setCSS(AlertType type)
    {
        URL resource = null;

        switch(type)
        {
            case NONE:
                resource = getClass().getResource("/css/alert_none.css");
                break;
            case INFORMATION:
                setGraphic(null);
                resource = getClass().getResource("/css/alert_info.css");
                break;
            case WARNING:
                resource = getClass().getResource("/css/alert_warning.css");
                break;
            case CONFIRMATION:
                resource = getClass().getResource("/css/alert_confirm.css");
                break;
            case ERROR:
                resource = getClass().getResource("/css/alert_error.css");
                break;
        }

        if(resource == null)
            Logger.log("Could not find css-file for alert", LogName.WARNING);
        else
            getDialogPane().getStylesheets().add(resource.toExternalForm());
    }
}
