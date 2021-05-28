package com.fi0x.edct.util;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Date;

public class Logger
{
    private static final String RESET = "\u001B[0m";
    private static final String WHITE = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    public static boolean debug;

    public static void INFO(String text)
    {
        log(LOGLEVEL.INF, text, null);
    }
    public static void WARNING(String text)
    {
        WARNING(text, null);
    }
    public static void WARNING(String text, Exception e)
    {
        log(LOGLEVEL.WRN, text, e);
    }
    public static void ERROR(String text)
    {
        ERROR(text, null);
    }
    public static void ERROR(String text, Exception e)
    {
        log(LOGLEVEL.ERR, text, e);
    }

    private static void log(LOGLEVEL lvl, String text, @Nullable Exception e)
    {
        String time = "[" + Date.from(Instant.now()) + "]";
        String prefix = "[" + lvl + "]: ";
        if(debug || lvl == LOGLEVEL.INF)
        {
            String color = WHITE;
            if(lvl == LOGLEVEL.WRN) color = YELLOW;
            else if(lvl == LOGLEVEL.ERR) color = RED;
            System.out.println(time + color + prefix + text + RESET);
            if(e != null) e.printStackTrace();
        }

        //TODO: Write Log entry to .log-file
    }
}
