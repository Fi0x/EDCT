package com.fi0x.edct.data.websites;

import com.fi0x.edct.data.RequestHandler;
import com.fi0x.edct.data.cleanup.JSONCleanup;
import com.sun.javafx.geom.Vec3d;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class EDSM
{
    @Nullable
    public static Vec3d getInaraStationID(String systemName, Vec3d coordinates) throws InterruptedException
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(ENDPOINTS.EDSMCoordinates.parameter[0], systemName);
        parameters.put(ENDPOINTS.EDSMCoordinates.parameter[1], "1");

        String html = RequestHandler.sendHTTPRequest(ENDPOINTS.EDSMCoordinates.url, ENDPOINTS.EDSMCoordinates.type, parameters);

        if(html == null) return null;

        return JSONCleanup.getSystemCoordinates(html);
    }
}