package com.fi0x.edct.logic.websites;

import com.fi0x.edct.logging.exceptions.HtmlConnectionException;
import com.fi0x.edct.logic.cleanup.EDSMCleanup;
import com.fi0x.edct.logic.structures.ENDPOINTS;
import com.fi0x.edct.logic.webrequests.RequestHandler;
import com.sun.javafx.geom.Vec3d;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class EDSM
{
    @Nullable
    public static Vec3d getSystemCoordinates(String systemName) throws InterruptedException
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(ENDPOINTS.EDSMCoordinates.parameter[0], systemName);
        parameters.put(ENDPOINTS.EDSMCoordinates.parameter[1], "1");

        String html = null;
        try
        {
            html = RequestHandler.sendHTTPRequest(ENDPOINTS.EDSMCoordinates.url, ENDPOINTS.EDSMCoordinates.type, parameters);
        } catch(HtmlConnectionException ignored)
        {
        }

        if(html == null) return null;

        return EDSMCleanup.getSystemCoordinates(html);
    }
}