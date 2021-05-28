package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.Main;
import com.fi0x.edct.data.structures.ENDPOINTS;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Date;
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
            String response = RequestHandler.sendHTTPRequest(ENDPOINTS.Releases.url, ENDPOINTS.Releases.type, params, true);
            Map<Date, String> releases = JSONCleanup.getReleases(response);

            Date newestVersion = Main.releaseDate;
            for(Map.Entry<Date, String> version : releases.entrySet())
            {
                if(version.getKey().after(newestVersion))
                    newestVersion = version.getKey();
            }

            if(newestVersion.after(Main.releaseDate))
                return releases.get(newestVersion);
        } catch(IOException ignored)
        {
        }
        return null;
    }
}
