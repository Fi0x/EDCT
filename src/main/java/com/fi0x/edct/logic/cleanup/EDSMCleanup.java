package com.fi0x.edct.logic.cleanup;

import com.fi0x.edct.logging.LogName;
import com.sun.javafx.geom.Vec3d;
import io.fi0x.javalogger.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EDSMCleanup
{
    public static Vec3d getSystemCoordinates(String jsonString)
    {
        Vec3d vector = null;

        if(jsonString.contains("coords"))
        {
            try
            {
                JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
                JSONObject coordinates = (JSONObject) json.get("coords");

                double x;
                double y;
                double z;

                try
                {
                    x = (double) coordinates.get("x");
                } catch(ClassCastException ignored)
                {
                    x = (long) coordinates.get("x");
                }
                try
                {
                    y = (double) coordinates.get("y");
                } catch(ClassCastException ignored)
                {
                    y = (long) coordinates.get("y");
                }
                try
                {
                    z = (double) coordinates.get("z");
                } catch(ClassCastException ignored)
                {
                    z = (long) coordinates.get("z");
                }

                vector = new Vec3d(x, y, z);
            } catch(ParseException | ClassCastException e)
            {
                Logger.log("Could not parse the coordinates for a system. JSON: " + jsonString, LogName.WARNING, e);
            }
        }

        return vector;
    }
}
