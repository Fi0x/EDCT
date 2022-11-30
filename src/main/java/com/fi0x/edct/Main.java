package com.fi0x.edct;

import com.fi0x.edct.gui.controller.Settings;
import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logging.exceptions.MixpanelEvents;
import com.fi0x.edct.logic.filesystem.BlacklistHandler;
import com.fi0x.edct.logic.filesystem.DiscordHandler;
import com.fi0x.edct.logic.filesystem.RedditHandler;
import com.fi0x.edct.logic.filesystem.SettingsHandler;
import com.fi0x.edct.logic.threads.DistanceHandler;
import com.fi0x.edct.logic.threads.EDDNHandler;
import com.fi0x.edct.logic.threads.StationUpdater;
import com.fi0x.edct.logic.threads.Updater;
import io.fi0x.javalogger.logging.LogColor;
import io.fi0x.javalogger.logging.Logger;
import io.fi0x.javalogger.mixpanel.MixpanelHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main
{
    public static Thread updater;
    public static Thread reloader;
    public static Thread eddn;
    private static Thread distance;
    private static Thread stations;
    private static Thread eddnHandler;

    private static String userID;
    private static File localStorage;
    public static File settings;
    public static File blacklist;
    public static File reddit;
    public static File discord;
    //TODO: Update version information
    public static final String version = "2.0.0.0";//All.GUI.Logic.Hotfix
    public static final VersionType versionType = VersionType.INSTALLER;

    public static void main(String[] args)
    {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        initializeLoggerSettings(arguments);
        setupLocalFiles();
        initializeMixpanelSettings(arguments);

        SettingsHandler.verifyIntegrity();
        Settings.loadSettings();

        MixpanelHandler.addMessage(MixpanelEvents.INITIALIZATION.name(), getProgramState());

        updater = new Thread(new Updater());
        distance = new Thread(DistanceHandler.getInstance());
        stations = new Thread(StationUpdater.getInstance());
        eddnHandler = new Thread(EDDNHandler.getInstance());

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

        MixpanelHandler.addMessage(MixpanelEvents.SHUTDOWN.name(), getProgramState());

        System.exit(0);
    }

    public static String getDBURL()
    {
        return localStorage.getPath() + File.separator + "Trades-v4.db";
    }

    private static void initializeLoggerSettings(ArrayList<String> arguments)
    {
        Logger.getInstance().setDebug(arguments.contains("-d"));
        Logger.getInstance().setVerbose(arguments.contains("-v"));

        Logger.createNewTemplate(LogName.VERBOSE, LogColor.WHITE, "", "VER", false, true, false, false, false, "");
        Logger.createNewTemplate(LogName.INFO, LogColor.WHITE_BRIGHT, "", "INF", false, false, false, false, false, "");
        Logger.createNewTemplate(LogName.WARNING, LogColor.get(LogColor.Color.YELLOW, LogColor.Design.BOLD, true), "", "WRN", true, false, true, false, true, "WARNING");
        Logger.createNewTemplate(LogName.ERROR, LogColor.get(LogColor.Color.WHITE, LogColor.Design.BOLD, true), LogColor.RED_BACKGROUND, "ERR", true, false, false, false, true, "ERROR");
    }
    private static void initializeMixpanelSettings(ArrayList<String> arguments)
    {
        MixpanelHandler.addDefaultProperty("version", Main.version + "-" + Main.versionType);
        MixpanelHandler.addDefaultProperty("debug", String.valueOf(arguments.contains("-d")));
        MixpanelHandler.addDefaultProperty("settingsMode", String.valueOf(Settings.detailedResults));
        MixpanelHandler.setProjectToken("cbdd63a3871a9f08b430df46217cf420");
        MixpanelHandler.setUniqueUserID(getUserID());
    }

    private static void setupLocalFiles()
    {
        localStorage = new File(System.getenv("APPDATA") + File.separator + "EDCT");
        if(createFileIfNotExists(localStorage, false))
        {
            System.exit(-1);
        }

        File logFolder = new File(localStorage.getPath() + File.separator + "Logs");
        createFileIfNotExists(logFolder, false);
        io.fi0x.javalogger.logging.Logger.getInstance().setLogFolder(logFolder);

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
    private static String getUserID()
    {
        if(userID == null)
        {
            userID = SettingsHandler.loadString("userID", "");

            if(userID.length() < 50)
            {
                String randomString = new Random().ints(48, 123)
                        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                        .limit(50)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();

                userID = randomString;
                SettingsHandler.storeValue("userID", randomString);
            }

            io.fi0x.javalogger.logging.Logger.log("UserID: " + userID, LogName.VERBOSE);
        }

        return userID;
    }

    public static Map<String, String> getProgramState()
    {
        Map<String, String> props = new HashMap<>();

        SettingsHandler.addFiltersToMap(props);
        SettingsHandler.addSettingsToMap(props);

        return props;
    }

    public static void clearLogs() throws IOException
    {
        FileUtils.cleanDirectory(new File(localStorage.getPath() + File.separator + "Logs"));
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
                    Logger.log("Could not create file: " + file, LogName.getError(997), e, 997);
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
