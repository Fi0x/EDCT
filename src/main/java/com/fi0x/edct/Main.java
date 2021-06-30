package com.fi0x.edct;

import com.fi0x.edct.controller.Settings;
import com.fi0x.edct.data.Updater;
import com.fi0x.edct.util.Logger;
import com.fi0x.edct.util.SettingsHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main
{
    public static Thread updater;
    public static Thread reloader;
    public static Thread eddn;

    private static File localStorage;
    public static File errors;
    public static File settings;
    //TODO: Update version information
    public static final String version = "1.2.4.4";//All.GUI.Logic.Hotfix

    public static void main(String[] args)
    {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        if(arguments.contains("-d")) Logger.debug = true;

        setupLocalFiles();
        SettingsHandler.verifyIntegrity();
        Settings.loadSettings();

        updater = new Thread(new Updater());

        MainWindow.main(args);
    }

    public static void stopProgram()
    {
        if(updater != null) updater.interrupt();
        if(reloader != null) reloader.interrupt();
        if(eddn != null) eddn.interrupt();
    }

    public static String getDBURL()
    {
        return localStorage.getPath() + File.separator + "Trades-v2.db";
    }

    private static void setupLocalFiles()
    {
        localStorage = new File(System.getenv("APPDATA") + File.separator + "EDCT");
        if(createFileIfNotExists(localStorage, false))
        {
            System.exit(-1);
        }

        errors = new File(localStorage.getPath() + File.separator + "carrier_trader.log");
        createFileIfNotExists(errors, true);

        settings = new File(localStorage.getPath() + File.separator + "settings.txt");
        createFileIfNotExists(settings, true);
    }

    private static boolean createFileIfNotExists(File file, boolean isFile)
    {
        if(!file.exists())
        {
            if(isFile)
            {
                try
                {
                    if(file.createNewFile()) return false;
                } catch(IOException e)
                {
                    Logger.ERROR(997, "Could not create file: " + file, e);
                    System.exit(997);
                }
            }
            else return !file.mkdir();
        }
        else return false;

        return true;
    }
}
