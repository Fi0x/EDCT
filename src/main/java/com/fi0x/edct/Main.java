package com.fi0x.edct;

import com.fi0x.edct.gui.controller.Settings;
import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logging.Logger;
import com.fi0x.edct.logging.MixpanelHandler;
import com.fi0x.edct.logic.filesystem.BlacklistHandler;
import com.fi0x.edct.logic.filesystem.DiscordHandler;
import com.fi0x.edct.logic.filesystem.RedditHandler;
import com.fi0x.edct.logic.filesystem.SettingsHandler;
import com.fi0x.edct.logic.threads.DistanceHandler;
import com.fi0x.edct.logic.threads.EDDNHandler;
import com.fi0x.edct.logic.threads.StationUpdater;
import com.fi0x.edct.logic.threads.Updater;

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
    private static Thread distance;
    private static Thread stations;
    private static Thread eddnHandler;
    private static Thread mixpanel;

    private static File localStorage;
    public static File errors;
    public static File settings;
    public static File blacklist;
    public static File reddit;
    public static File discord;
    //TODO: Update version information
    public static final String version = "1.6.9.9";//All.GUI.Logic.Hotfix
    public static final VersionType versionType = VersionType.INSTALLER;

    public static void main(String[] args)
    {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        Logger.setDebugMode(arguments.contains("-d"));
        MixpanelHandler.setDebugMode(arguments.contains("-d"));

        setupLocalFiles();
        SettingsHandler.verifyIntegrity();
        Settings.loadSettings();

        MixpanelHandler.addMessage(MixpanelHandler.EVENT.INITIALIZATION, MixpanelHandler.getProgramState());
        MixpanelHandler.sendMessages();

        updater = new Thread(new Updater());
        distance = new Thread(DistanceHandler.getInstance());
        stations = new Thread(StationUpdater.getInstance());
        eddnHandler = new Thread(EDDNHandler.getInstance());
        mixpanel = new Thread(new MixpanelHandler());

        mixpanel.start();
        distance.start();
        stations.start();
        eddnHandler.start();

        MainWindow.main(args);
    }

    public static void stopProgram()
    {
        if(updater != null) updater.interrupt();
        if(reloader != null) reloader.stop();
        if(eddn != null) eddn.interrupt();
        if(distance != null) distance.interrupt();
        if(stations != null) stations.interrupt();
        if(eddnHandler != null) eddnHandler.interrupt();
        if(mixpanel != null) mixpanel.interrupt();

        MixpanelHandler.addMessage(MixpanelHandler.EVENT.SHUTDOWN, MixpanelHandler.getProgramState());
        MixpanelHandler.sendMessages();

        System.exit(0);
    }

    public static String getDBURL()
    {
        return localStorage.getPath() + File.separator + "Trades-v4.db";
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

        blacklist = new File(localStorage.getPath() + File.separator + "blacklist.txt");
        createFileIfNotExists(blacklist, true);
        BlacklistHandler.fillBlacklistIfEmpty();

        reddit = new File(localStorage.getPath() + File.separator + "reddit.json");
        createFileIfNotExists(reddit, true);
        RedditHandler.fillRedditFileIfEmpty();

        discord = new File(localStorage.getPath() + File.separator + "discord.json");
        createFileIfNotExists(discord, true);
        DiscordHandler.fillDiscordFileIfEmpty();
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

    public enum VersionType
    {
        PORTABLE,
        INSTALLER,
        JAR
    }
}
