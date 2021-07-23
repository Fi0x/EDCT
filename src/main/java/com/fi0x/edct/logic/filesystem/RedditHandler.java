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
        //TODO: If a key is missing, add it from the reddit.json file
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

    public static String getValue(JSONObject json, String key)
    {
        if(json.containsKey(key)) return json.get(key).toString();
        else return "";
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
