package com.fi0x.edct.logging;

import com.fi0x.edct.Main;
import com.fi0x.edct.gui.visual.MainWindow;
import javafx.application.Platform;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Logger
{
    private static final String RESET = "\u001B[0m";
    private static final String WHITE = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    private static boolean debug = false;

    public static void INFO(String text)
    {
        log(0, LEVEL.INF, text, null);
    }
    public static void WARNING(String text)
    {
        log(0, LEVEL.WRN, text, null);
    }
    public static void WARNING(String text, Exception e)
    {
        log(0, LEVEL.WRN, text, e);
    }
    public static void WARNING(int code, String text)
    {
        log(code, LEVEL.WRN, text, null);
    }
    public static void WARNING(int code, String text, Exception e)
    {
        log(code, LEVEL.WRN, text, e);
    }
    public static void ERROR(int code, String text)
    {
        log(code, LEVEL.ERR, text, null);
    }
    public static void ERROR(int code, String text, Exception e)
    {
        log(code, LEVEL.ERR, text, e);
    }

    private static void log(int code, LEVEL lvl, String text, @Nullable Exception e)
    {
        String time = getDateString();
        String errorCode = code == 0 ? "[---]" : "[" + code + "]";
        String prefix = "[" + lvl + "]";
        if(debug || lvl == LEVEL.INF)
        {
            String color = WHITE;
            if(lvl == LEVEL.WRN) color = YELLOW;
            else if(lvl == LEVEL.ERR) color = RED;
            System.out.println(color + time + " " + prefix + " " + errorCode + " " + text + RESET);
            if(e != null) e.printStackTrace();
        }

        if(lvl != LEVEL.INF)
        {
            try
            {
                if(!Main.errors.exists()) Main.createLogFile();
                List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.errors.toPath(), StandardCharsets.UTF_8));

                fileContent.add(time + prefix + errorCode + text);
                if(e != null) fileContent.add("\t" + Arrays.toString(e.getStackTrace())
                        .replace(", ", "\n\t")
                        .replace("[", "").replace("]", ""));

                Files.write(Main.errors.toPath(), fileContent, StandardCharsets.UTF_8);
            } catch(IOException ex)
            {
                time = "[" + Date.from(Instant.now()) + "]";
                prefix = "[ERR]";
                errorCode = "[996]";
                System.out.println(time + RED + prefix + errorCode + "Something went wrong when writing to the log-file" + RESET);
                ex.printStackTrace();
            }

            if(text.equals("Could not send messages to mixpanel")) return;
            if(lvl == LEVEL.ERR)
            {
                MixpanelHandler.addMessage(MixpanelHandler.EVENT.ERROR, getMixpanelProps(errorCode, text, e));
                Platform.runLater(() -> MainWindow.getInstance().infoController.setError(code));
            } else MixpanelHandler.addMessage(MixpanelHandler.EVENT.WARNING, getMixpanelProps(errorCode, text, e));
        }
    }

    public static void setDebugMode(boolean isDebug)
    {
        debug = isDebug;
        Logger.INFO("DEBUG-MODE: " + isDebug);
    }

    private static String getDateString()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        return "[" + dtf.format(now) + "]";
    }
    private static Map<String, String> getMixpanelProps(String errorCode, String message, @Nullable Exception exception)
    {
        Map<String, String> props = new HashMap<>();

        props.put("code", errorCode);
        props.put("message", message);
        if(exception != null) props.put("exception", exception.toString());

        return props;
    }

    public enum LEVEL
    {
        INF,
        WRN,
        ERR
    }
}
