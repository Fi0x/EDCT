package com.fi0x.edct;

import com.fi0x.edct.data.Updater;
import com.fi0x.edct.util.Out;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Main
{
    public static Thread updater;
    public static Thread reloader;

    public static File localStorage;
    public static File errors;
    public static final Date releaseDate = Date.from(Instant.parse("2021-05-15T08:56:30Z"));//TODO: Update releaseTime

    public static void main(String[] args)
    {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        if(arguments.contains("-v")) Out.v = true;
        if(arguments.contains("-vv")) Out.vv = true;
        if(arguments.contains("-d")) Out.d = true;

        setupLocalFiles();

        Out.newBuilder("Starting Program").verbose().INFO();

        updater = new Thread(new Updater());

        MainWindow.main(args);
    }

    public static void stopProgram()
    {
        if(updater != null) updater.interrupt();
        if(reloader != null) reloader.interrupt();
    }

    private static void setupLocalFiles()
    {
        Out.newBuilder("Setting up local storage").veryVerbose().INFO();

        localStorage = new File(System.getenv("APPDATA") + File.separator + "EDCT");
        if(createFileIfNotExists(localStorage, false))
        {
            Out.newBuilder("Could not create local storage folder").always().ERROR();
            System.exit(-1);
        }

        errors = new File(localStorage.getPath() + File.separator + "errors");
        if(createFileIfNotExists(errors, true)) Out.newBuilder("Could not create settings-file").debug().WARNING();
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
                } catch(IOException ignored)
                {
                }
            }
            else return !file.mkdir();
        }
        else return false;

        return true;
    }
}
