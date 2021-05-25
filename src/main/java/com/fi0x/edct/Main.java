package com.fi0x.edct;

import com.fi0x.edct.data.Updater;
import com.fi0x.edct.data.webconnection.UpdateThread;
import com.fi0x.edct.util.Out;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main
{
    @Deprecated
    public static Thread updaterThread;
    @Deprecated
    public static Thread downloadThread;

    public static Thread updater;

    public static File localStorage;
    @Deprecated
    public static File commodityList;
    public static File errors;

    public static void main(String[] args)
    {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        if(arguments.contains("-v")) Out.v = true;
        if(arguments.contains("-vv")) Out.vv = true;
        if(arguments.contains("-d")) Out.d = true;

        setupLocalFiles();

        Out.newBuilder("Starting Program").verbose().print();

        updaterThread = new Thread(new UpdateThread());
        updater = new Thread(new Updater());

        MainWindow.main(args);
    }

    public static void stopProgram()
    {
        if(updaterThread != null) updaterThread.stop();
        if(downloadThread != null) downloadThread.stop();

        if(updater != null) updater.interrupt();
    }

    private static void setupLocalFiles()
    {
        Out.newBuilder("Setting up local storage").veryVerbose().print();

        localStorage = new File(System.getenv("APPDATA") + File.separator + "EDCT");
        if(!createFileIfNotExists(localStorage, false))
        {
            Out.newBuilder("Could not create local storage folder").always().ERROR().print();
            System.exit(-1);
        }

        @Deprecated
        File commodityFolder1 = new File(localStorage.getPath() + File.separator + "CommoditySells");
        if(!createFileIfNotExists(commodityFolder1, false)) Out.newBuilder("Could not create sell-folder").always().ERROR().print();
        @Deprecated
        File commodityFolder2 = new File(localStorage.getPath() + File.separator + "CommodityBuys");
        if(!createFileIfNotExists(commodityFolder2, false)) Out.newBuilder("Could not create buy-folder").always().ERROR().print();

        errors = new File(localStorage.getPath() + File.separator + "errors");
        if(!createFileIfNotExists(errors, true)) Out.newBuilder("Could not create settings-file").debug().WARNING().print();

        commodityList = new File(localStorage.getPath() + File.separator + "CommodityList");
        if(!createFileIfNotExists(commodityList, true)) Out.newBuilder("Could not create commodityList-file").debug().WARNING().print();
    }

    private static boolean createFileIfNotExists(File file, boolean isFile)
    {
        if(!file.exists())
        {
            if(isFile)
            {
                try
                {
                    if(file.createNewFile()) return true;
                } catch(IOException ignored)
                {
                }
            }
            else return file.mkdir();
        }
        else return true;

        return false;
    }
}
