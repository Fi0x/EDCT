package com.fi0x.edct.util;

import com.fi0x.edct.data.localstorage.db.DBHandler;
import com.fi0x.edct.data.websites.EDSM;
import com.sun.javafx.geom.Vec3d;

import java.util.ArrayList;

public class StationUpdater implements Runnable
{
    private static StationUpdater instance;
    private static final ArrayList<String> QUEUE = new ArrayList<>();

    private StationUpdater()
    {
    }
    public static StationUpdater getInstance()
    {
        if(instance == null) instance = new StationUpdater();
        return instance;
    }

    @Override
    public void run()
    {
        while(!Thread.interrupted())
        {
            if(QUEUE.size() > 0)
            {
                try
                {
                    Vec3d coordinates = EDSM.getSystemCoordinates(QUEUE.get(0));
                    if(coordinates != null) DBHandler.setSystemCoordinates(QUEUE.get(0), coordinates);
                    QUEUE.remove(0);
                } catch(InterruptedException ignored)
                {
                    return;
                }
            }
        }
    }

    public static void addSystemToQueue(String systemname)
    {
        QUEUE.add(systemname);
    }
}
