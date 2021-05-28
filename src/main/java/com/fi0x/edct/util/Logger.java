package com.fi0x.edct.util;

import com.fi0x.edct.Main;
import com.fi0x.edct.MainWindow;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Logger
{
    private static final String RESET = "\u001B[0m";
    private static final String WHITE = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    public static boolean debug = false;

    public static void INFO(String text)
    {
        log(0, LOGLEVEL.INF, text, null);
    }
    public static void WARNING(String text)
    {
        WARNING(text, null);
    }
    public static void WARNING(String text, Exception e)
    {
        log(0, LOGLEVEL.WRN, text, e);
    }
    public static void ERROR(int code, String text)
    {
        ERROR(code, text, null);
    }
    public static void ERROR(int code, String text, Exception e)
    {
        log(code, LOGLEVEL.ERR, text, e);
    }

    private static void log(int code, LOGLEVEL lvl, String text, @Nullable Exception e)
    {
        String time = "[" + Date.from(Instant.now()) + "]";
        String errorCode = code == 0 ? "[---]" : "[" + code + "]";
        String prefix = "[" + lvl + "]: ";
        if(debug || lvl == LOGLEVEL.INF)
        {
            String color = WHITE;
            if(lvl == LOGLEVEL.WRN) color = YELLOW;
            else if(lvl == LOGLEVEL.ERR) color = RED;
            System.out.println(time + color + prefix + errorCode + text + RESET);
            if(e != null) e.printStackTrace();
        }

        if(lvl != LOGLEVEL.INF)
        {
            try
            {
                List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.errors.toPath(), StandardCharsets.UTF_8));

                fileContent.add(time + prefix + errorCode + text);
                if(e != null) fileContent.add(Arrays.toString(e.getStackTrace()));

                Files.write(Main.errors.toPath(), fileContent, StandardCharsets.UTF_8);
            } catch(IOException ignored)
            {
            }
        }

        if(lvl == LOGLEVEL.ERR)
        {
            MainWindow.getInstance().infoController.setError(code);
        }
    }
}
