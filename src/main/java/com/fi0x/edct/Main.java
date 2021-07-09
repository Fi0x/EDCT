package com.fi0x.edct;

import com.fi0x.edct.controller.Settings;
import com.fi0x.edct.data.Updater;
import com.fi0x.edct.telemetry.EVENT;
import com.fi0x.edct.telemetry.MixpanelHandler;
import com.fi0x.edct.util.Logger;
import com.fi0x.edct.util.SettingsHandler;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Main
{
    public static Thread updater;
    public static Thread reloader;
    public static Thread eddn;
    private static Thread mixpanel;

    private static File localStorage;
    public static File errors;
    public static File settings;
    //TODO: Update version information
    public static final String version = "1.2.6.5";//All.GUI.Logic.Hotfix
    public static final boolean portable = false;

    public static void main(String[] args)
    {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        Logger.setDebugMode(arguments.contains("-d"));
        MixpanelHandler.setDebugMode(arguments.contains("-d"));

        setupLocalFiles();
        SettingsHandler.verifyIntegrity();
        Settings.loadSettings();

        MixpanelHandler.addMessage(EVENT.INITIALIZATION, MixpanelHandler.getProgramState());
        MixpanelHandler.sendMessages();

        updater = new Thread(new Updater());
        mixpanel = new Thread(new MixpanelHandler());
        mixpanel.start();

        MainWindow.main(args);
    }

    public static void stopProgram()
    {
        if(updater != null) updater.interrupt();
        if(reloader != null) reloader.interrupt();
        if(eddn != null) eddn.interrupt();
        if(mixpanel != null) mixpanel.interrupt();

        MixpanelHandler.addMessage(EVENT.SHUTDOWN, MixpanelHandler.getProgramState());
        MixpanelHandler.sendMessages();
    }

    public static String getDBURL()
    {
        return localStorage.getPath() + File.separator + "Trades-v3.db";
    }

    public static void createLogFile()
    {
        createFileIfNotExists(errors, true);
    }

    private static void setupLocalFiles()
    {
        localStorage = new File(System.getenv("APPDATA") + File.separator + "EDCT");
        if(createFileIfNotExists(localStorage, false))
        {
            System.exit(-1);
        }

        createFileIfNotExists(new File(localStorage.getPath() + File.separator + "Logs"), false);
        errors = new File(localStorage.getPath() + File.separator + "Logs" + File.separator + getDateString() + ".log");

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
            } else return !file.mkdir();
        } else return false;

        return true;
    }
    private static String getDateString()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }
}
