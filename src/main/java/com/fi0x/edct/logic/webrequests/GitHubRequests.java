package com.fi0x.edct.logic.webrequests;

import com.fi0x.edct.logging.Logger;
import com.fi0x.edct.logging.exceptions.HtmlConnectionException;
import com.fi0x.edct.logic.structures.ENDPOINTS;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GitHubRequests
{
    @Nullable
    public static String getReleases()
    {
        Map<String, String> params = new HashMap<>();

        int counter = 0;
        while(counter < 3)
        {
            counter++;
            try
            {
                return RequestHandler.sendHTTPRequest(ENDPOINTS.Releases.url, ENDPOINTS.Releases.type, params, true);
            } catch(InterruptedException ignored)
            {
                return null;
            } catch(IOException e)
            {
                Logger.WARNING("Could not find out if there is a newer version", e);
                return null;
            } catch(HtmlConnectionException ignored)
            {
            }
        }
        return null;
    }
}
