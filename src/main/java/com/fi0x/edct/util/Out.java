package com.fi0x.edct.util;
public class Out
{
    private static final String RESET = "\u001B[0m";
    private static final String WHITE = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";

    public static boolean d;
    public static boolean v;
    public static boolean vv;

    private boolean always;
    private boolean debug;
    private boolean verbose;
    private boolean veryVerbose;

    private String text;
    private String origin;
    private String color;

    private Out()
    {
        always = false;
    }

    public static Out newBuilder(String message)
    {
        Out out = new Out();
        out.text = message;
        out.origin = null;
        out.color = WHITE;
        return out;
    }
    public Out origin(String className)
    {
        origin = className;
        return this;
    }

    public Out always()
    {
        always = true;
        return this;
    }
    public Out debug()
    {
        debug = true;
        return this;
    }
    public Out verbose()
    {
        verbose = true;
        return this;
    }
    public Out veryVerbose()
    {
        veryVerbose = true;
        return this;
    }

    public void WARNING()
    {
        color = YELLOW;
        print("WRN");
        //TODO: Write warnings to .log file
    }
    public void ERROR()
    {
        color = RED;
        print("ERR");
        //TODO: Write errors to .log file
        //TODO: Display error in programinfo
    }
    public void SUCCESS()
    {
        color = GREEN;
        print("INF");
    }
    public void INFO()
    {
        color = WHITE;
        print("INF");
    }

    private void print(String prefix)
    {
        boolean allowed = always;

        if(debug && d) allowed = true;
        if(verbose && (v || vv)) allowed = true;
        if(veryVerbose && vv) allowed = true;

        if(allowed)
        {
            prefix = "[" +prefix + "]: ";
            if(origin == null) System.out.println(prefix + color + text + RESET);
            else System.out.println(prefix + origin + ":\t\t" + color + text + RESET);
        }
    }
}