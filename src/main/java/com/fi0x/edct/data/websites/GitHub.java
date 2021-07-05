package com.fi0x.edct.data.websites;

import com.fi0x.edct.Main;
import com.fi0x.edct.data.RequestHandler;
import com.fi0x.edct.data.cleanup.JSONCleanup;
import com.fi0x.edct.util.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GitHub
{
    @Nullable
    public static String checkForVersionUpdates()
    {
        Map<String, String> params = new HashMap<>();
        try
        {
            String response = null;
            try
            {
                response = RequestHandler.sendHTTPRequest(ENDPOINTS.Releases.url, ENDPOINTS.Releases.type, params, true);
            } catch(InterruptedException ignored)
            {
            }
            if(response == null || response.equals("")) return null;
            Map<String, String> releases = JSONCleanup.getReleases(response);

            String newestVersion = Main.version;
            for(Map.Entry<String, String> version : releases.entrySet())
            {
                if(isNewer(newestVersion, version.getKey())) newestVersion = version.getKey();
            }

            if(isNewer(Main.version, newestVersion)) return releases.get(newestVersion);
        } catch(IOException e)
        {
            Logger.WARNING("Could not find out if there is a newer version", e);
        }
        return null;
    }

    private static boolean isNewer(String currentVersion, String nextVersion)
    {
        ArrayList<Integer> currentParts = new ArrayList<>();
        ArrayList<Integer> nextParts = new ArrayList<>();

        for(String part : currentVersion.replace(".", "-").split("-"))
        {
            currentParts.add(Integer.parseInt(part));
        }
        for(String part : nextVersion.replace(".", "-").split("-"))
        {
            nextParts.add(Integer.parseInt(part));
        }

        if(currentParts.get(0) < nextParts.get(0)) return true;
        else if(currentParts.get(0) > nextParts.get(0)) return false;

        if(currentParts.get(1) < nextParts.get(1)) return true;
        else if(currentParts.get(1) > nextParts.get(1)) return false;

        if(currentParts.get(2) < nextParts.get(2)) return true;
        else if(currentParts.get(2) > nextParts.get(2)) return false;

        return currentParts.get(3) < nextParts.get(3);
    }
}
