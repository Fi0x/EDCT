package com.fi0x.edct;

import com.fi0x.edct.data.webconnection.UpdateThread;
import com.fi0x.edct.util.Out;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main
{
    public static Thread updaterThread;
    public static Thread downloadThread;

    public static File localStorage;
    public static File commodityList;

    public static void main(String[] args)
    {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        if(arguments.contains("-v")) Out.v = true;
        if(arguments.contains("-vv")) Out.vv = true;
        if(arguments.contains("-d")) Out.d = true;

        setupLocalFiles();

        Out.newBuilder("Starting Program").verbose().print();

        updaterThread = new Thread(new UpdateThread());

        MainWindow.main(args);
    }

    public static void stopProgram()
    {
        if(updaterThread != null) updaterThread.stop();
        if(downloadThread != null) downloadThread.stop();
    }

    private static void setupLocalFiles()
    {
        Out.newBuilder("Setting up local storage").veryVerbose().print();
        localStorage = new File(System.getenv("APPDATA") + File.separator + "EDCT");

        if(!localStorage.exists())
        {
            if(localStorage.mkdir()) Out.newBuilder("Created local storage directory").SUCCESS().verbose().print();
            else
            {
                Out.newBuilder("Could not create local storage directory").origin("MainWindow").WARNING().debug().print();
                return;
            }
        }

        File commodityFolder1 = new File(localStorage.getPath() + File.separator + "CommoditySells");
        if(!commodityFolder1.exists())
        {
            if(commodityFolder1.mkdir()) Out.newBuilder("Created directory for commodity data").SUCCESS().verbose().print();
            else
            {
                Out.newBuilder("Could not create directory for commodity data").origin("MainWindow").WARNING().debug().print();
                return;
            }
        }
        File commodityFolder2 = new File(localStorage.getPath() + File.separator + "CommodityBuys");
        if(!commodityFolder2.exists())
        {
            if(commodityFolder2.mkdir()) Out.newBuilder("Created directory for commodity data").SUCCESS().verbose().print();
            else
            {
                Out.newBuilder("Could not create directory for commodity data").origin("MainWindow").WARNING().debug().print();
                return;
            }
        }

        commodityList = new File(localStorage.getPath() + File.separator + "CommodityList");
        if(!commodityList.exists())
        {
            try
            {
                if(commodityList.createNewFile())
                {
                    Out.newBuilder("Created commodityList-file").SUCCESS().verbose().print();
                    return;
                }
            } catch(IOException ignored)
            {
            }
            Out.newBuilder("Could not create commodityList-file").origin("MainWindow").WARNING().debug().print();
        }
    }
}
