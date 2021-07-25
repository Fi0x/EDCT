package com.fi0x.edct.logic.filesystem;

import com.fi0x.edct.Main;
import com.fi0x.edct.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RedditHandler
{
    public static void addMissingKeys(JSONObject json)
    {
        try
        {
            var jsonInput = Main.class.getResourceAsStream("/defaults/reddit.json");
            JSONObject originalJson = (JSONObject) new JSONParser().parse(jsonInput.toString());
            for(Object o : originalJson.keySet())
            {
                String key = o.toString();
                if(!json.containsKey(key)) json.put(key, originalJson.get(key));
            }
        } catch(ParseException ignored)
        {
        }
    }

    @Nullable
    public static JSONObject getRedditConfig()
    {
        JSONObject json = null;
        try
        {
            String fileContent = Files.readString(Main.reddit.toPath());
            json = (JSONObject) new JSONParser().parse(fileContent);
        } catch(IOException | ParseException ignored)
        {
        }

        return json;
    }

    public static String getValue(JSONObject json, String key, boolean unloading, @Nullable String type)
    {
        JSONObject keyArea = null;
        if(json.containsKey("Required Variables") && ((JSONObject) json.get("Required Variables")).containsKey(key))
        {
            keyArea = (JSONObject) json.get("Required Variables");
        } else if(json.containsKey("Custom Variables") && ((JSONObject) json.get("Custom Variables")).containsKey(key))
        {
            keyArea = (JSONObject) json.get("Custom Variables");
        }
        if(keyArea == null) return "";

        String value = keyArea.get(key).toString();
        if(value.charAt(0) != '{') return value;

        JSONObject keyJson = (JSONObject) keyArea.get(key);
        if(keyJson.containsKey(unloading ? "UNLOADING" : "LOADING"))
        {
            if(type == null) return keyJson.get(unloading ? "UNLOADING" : "LOADING").toString();
            else
            {
                JSONObject loadingJson = (JSONObject) keyJson.get(unloading ? "UNLOADING" : "LOADING");
                if(loadingJson.containsKey(type)) return loadingJson.get(type).toString();
            }
        } else return keyJson.toString();

        return "";
    }

    public static void fillRedditFileIfEmpty()
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.reddit.toPath(), StandardCharsets.UTF_8));

            if(fileContent.size() <= 0)
            {
                var jsonInput = Main.class.getResourceAsStream("/defaults/reddit.json");
                fileContent = new BufferedReader(new InputStreamReader(jsonInput, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
            }

            Files.write(Main.reddit.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.WARNING(996, "Could not write default entry to blacklist", e);
        }
    }
}
