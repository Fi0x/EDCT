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

public class DiscordHandler
{
    public static void addMissingKeys(JSONObject json)
    {
        try
        {
            var jsonInput = Main.class.getResourceAsStream("/defaults/discord.json");
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
    public static JSONObject getDiscordConfig()
    {
        JSONObject json = null;
        try
        {
            String fileContent = Files.readString(Main.discord.toPath());
            json = (JSONObject) new JSONParser().parse(fileContent);
        } catch(IOException | ParseException ignored)
        {
        }

        return json;
    }

    public static void fillDiscordFileIfEmpty()
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.discord.toPath(), StandardCharsets.UTF_8));

            if(fileContent.size() <= 0)
            {
                var jsonInput = Main.class.getResourceAsStream("/defaults/discord.json");
                fileContent = new BufferedReader(new InputStreamReader(jsonInput, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
            }

            Files.write(Main.discord.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.WARNING(996, "Could not write default entry to discord config", e);
        }
    }
}
