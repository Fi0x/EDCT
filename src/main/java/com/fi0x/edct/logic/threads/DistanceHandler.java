package com.fi0x.edct.logic.threads;

import com.fi0x.edct.gui.visual.MainWindow;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.websites.EDSM;
import com.sun.javafx.geom.Vec3d;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.Map;

public class DistanceHandler implements Runnable
{
    private static final Map<String, String> QUEUE = new HashMap<>();
    private static DistanceHandler instance;

    private DistanceHandler()
    {
    }

    public static DistanceHandler getInstance()
    {
        if(instance == null) instance = new DistanceHandler();
        return instance;
    }

    @Override
    public void run()
    {
        while(!Thread.interrupted())
        {
            if(QUEUE.size() > 0)
            {
                Map.Entry<String, String> firstEntry = QUEUE.entrySet().iterator().next();
                calculateDistance(firstEntry.getKey(), firstEntry.getValue());
                QUEUE.remove(firstEntry.getKey());
            }
        }
    }

    public static void addDistanceCheck(String system1, String system2)
    {
        QUEUE.put(system1, system2);
    }

    private void calculateDistance(String system1, String system2)
    {
        Vec3d coords1 = DBHandler.getSystemCoords(system1);
        Vec3d coords2 = DBHandler.getSystemCoords(system2);

        try
        {
            if(coords1 == null)
            {
                coords1 = EDSM.getSystemCoordinates(system1);
                if(coords1 == null) return;
                DBHandler.setSystemCoordinates(system1, coords1);
            }
            if(coords2 == null)
            {
                coords2 = EDSM.getSystemCoordinates(system2);
                if(coords2 == null) return;
                DBHandler.setSystemCoordinates(system2, coords2);
            }
        } catch(InterruptedException e)
        {
            return;
        }

        double sum = (coords1.x - coords2.x) * (coords1.x - coords2.x);
        sum += (coords1.y - coords2.y) * (coords1.y - coords2.y);
        sum += (coords1.z - coords2.z) * (coords1.z - coords2.z);

        double distance = Math.sqrt(sum);
        DBHandler.setSystemDistance(system1, system2, distance);
        Platform.runLater(() -> MainWindow.getInstance().resultsController.updateDistance(system1, system2, distance));
    }
}
