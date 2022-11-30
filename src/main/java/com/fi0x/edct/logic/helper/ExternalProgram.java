package com.fi0x.edct.logic.helper;

import com.fi0x.edct.logging.LogName;
import io.fi0x.javalogger.logging.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ExternalProgram
{
    public static void openWebsite(String url)
    {
        Desktop desktop = Desktop.getDesktop();
        try
        {
            desktop.browse(new URI(url));
        } catch(IOException | URISyntaxException e)
        {
            Logger.log("Could not open url in browser", LogName.getError(992), e, 992);
        }
    }

    public static void openNotepad(File fileToOpen)
    {
        ProcessBuilder processBuilder = new ProcessBuilder("Notepad.exe", fileToOpen.getPath());
        try
        {
            processBuilder.start();
        } catch(IOException e)
        {
            Logger.log("Could not open file with Notepad, trying default editor next", LogName.WARNING, e, 992);
            openDefaultEditor(fileToOpen);
        }
    }

    public static void openDefaultEditor(File fileToOpen)
    {
        if(Desktop.isDesktopSupported())
        {
            try
            {
                Desktop.getDesktop().edit(fileToOpen);
            } catch(IOException e)
            {
                Logger.log("Could not open file (" + fileToOpen.getName() + ") in editor", LogName.getError(992), e, 992);
            }
        }
    }

    public static void copyToClipboard(String stringToCopy)
    {
        StringSelection content = new StringSelection(stringToCopy);
        Clipboard clb = Toolkit.getDefaultToolkit().getSystemClipboard();
        clb.setContents(content, null);
    }
}
