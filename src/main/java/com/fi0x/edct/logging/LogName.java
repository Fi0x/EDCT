package com.fi0x.edct.logging;

import com.fi0x.edct.gui.visual.MainWindow;
import javafx.application.Platform;

public enum LogName
{
    VERBOSE,
    INFO,
    WARNING,
    ERROR;

    public static LogName getError(int errorCode)
    {
        Platform.runLater(() -> MainWindow.getInstance().infoController.setError(errorCode));
        return ERROR;
    }
}
