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

    public Out WARNING()
    {
        color = YELLOW;
        return this;
    }
    public Out ERROR()
    {
        color = RED;
        return this;
    }
    public Out SUCCESS()
    {
        color = GREEN;
        return this;
    }

    public void print()
    {
        boolean allowed = always;

        if(debug && d) allowed = true;
        if(verbose && (v || vv)) allowed = true;
        if(veryVerbose && vv) allowed = true;

        if(allowed)
        {
            if(origin == null) System.out.println(color + text + RESET);
            else System.out.println(origin + ":\t\t" + color + text + RESET);
        }
    }
}