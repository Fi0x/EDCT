package com.fi0x.edct.logic.helper;

import com.fi0x.edct.logging.Logger;

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
            Logger.ERROR(992, "Could not open url in browser", e);
        }
    }

    public static void openNotepad(File fileToOpen)
    {
        if(Desktop.isDesktopSupported())
        {
            try
            {
                Desktop.getDesktop().edit(fileToOpen);
            } catch(IOException e)
            {
                Logger.ERROR(992, "Could not open file (" + fileToOpen.getName() + ") in editor");
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
