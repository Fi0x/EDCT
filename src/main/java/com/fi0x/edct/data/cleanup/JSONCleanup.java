package com.fi0x.edct.data.cleanup;

import com.fi0x.edct.util.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JSONCleanup
{
    public static Map<Date, String> getReleases(String jsonString)
    {
        Map<Date, String> releaseDates = new HashMap<>();

        try
        {
            JSONArray jsonReleases = (JSONArray) new JSONParser().parse(jsonString);
            for(Object release : jsonReleases)
            {
                String published = ((JSONObject) release).get("created_at").toString();
                String url = ((JSONObject) release).get("html_url").toString();
                Date publishedDate = Date.from(Instant.parse(published));
                releaseDates.put(publishedDate, url);
            }
        } catch(ParseException e)
        {
            Logger.WARNING("Could not convert release-json: " + jsonString, e);
        }

        return releaseDates;
    }
}
