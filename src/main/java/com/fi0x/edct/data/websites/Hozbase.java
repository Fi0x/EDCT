package com.fi0x.edct.data.websites;

import com.fi0x.edct.MainWindow;
import com.fi0x.edct.data.RequestHandler;
import com.fi0x.edct.data.cleanup.HTMLCleanup;
import com.fi0x.edct.data.localstorage.DBHandler;
import com.fi0x.edct.data.structures.ENDPOINTS;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.Map;

public class Hozbase implements Runnable
{
    private final String startSystem;
    private final String endSystem;

    public Hozbase(String system1, String system2)
    {
        startSystem = system1;
        endSystem = system2;
    }
    @Override
    public void run()
    {
        try
        {
            double distance = getStarDistance(startSystem, endSystem);
            Platform.runLater(() -> MainWindow.getInstance().resultsController.updateDistance(startSystem, endSystem, distance));
        } catch(InterruptedException ignored)
        {
        }
    }

    private static double getStarDistance(String system1, String system2) throws InterruptedException
    {
        double distance = DBHandler.getInstance().getSystemDistance(system1, system2);
        if(distance != 0) return distance;

        Map<String, String> parameters = getRefinedParameters(ENDPOINTS.SystemDistance.parameter, system1, system2);
        String html = RequestHandler.sendHTTPRequest(ENDPOINTS.SystemDistance.url, ENDPOINTS.SystemDistance.type, parameters);

        if(html == null) return 0;
        distance = HTMLCleanup.getSystemDistance(html);

        DBHandler.getInstance().setSystemDistance(system1, system2, distance);
        return distance;
    }

    private static Map<String, String> getRefinedParameters(String[] parameter, String system1, String system2)
    {
        Map<String, String> parameters = new HashMap<>();

        for(String param : parameter)
        {
            if("systems".equals(param)) parameters.put(param, system1 + "," + system2);
        }

        return parameters;
    }
}
