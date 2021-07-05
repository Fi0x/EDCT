package com.fi0x.edct.data.localstorage;

import com.fi0x.edct.MainWindow;
import com.fi0x.edct.data.localstorage.db.DBHandler;
import com.fi0x.edct.data.websites.EDSM;
import com.sun.javafx.geom.Vec3d;
import javafx.application.Platform;

public class DistanceHandler implements Runnable
{
    private final String SYS1;
    private final String SYS2;

    public DistanceHandler(String system1, String system2)
    {
        SYS1 = system1;
        SYS2 = system2;
    }

    @Override
    public void run()
    {
        Vec3d coords1 = DBHandler.getSystemCoords(SYS1);
        Vec3d coords2 = DBHandler.getSystemCoords(SYS2);

        try
        {
            if(coords1 == null)
            {
                coords1 = EDSM.getSystemCoordinates(SYS1);
                if(coords1 == null) return;
                DBHandler.setSystemCoordinates(SYS1, coords1);
            }
            if(coords2 == null)
            {
                coords2 = EDSM.getSystemCoordinates(SYS2);
                if(coords2 == null) return;
                DBHandler.setSystemCoordinates(SYS2, coords2);
            }
        } catch(InterruptedException e)
        {
            return;
        }

        double sum = (coords1.x - coords2.x) * (coords1.x - coords2.x);
        sum += (coords1.y - coords2.y) * (coords1.y - coords2.y);
        sum += (coords1.z - coords2.z) * (coords1.z - coords2.z);

        double distance = Math.sqrt(sum);
        DBHandler.setSystemDistance(SYS1, SYS2, distance);
        Platform.runLater(() -> MainWindow.getInstance().resultsController.updateDistance(SYS1, SYS2, distance));
    }
}
