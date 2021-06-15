package com.fi0x.edct.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SettingsHandler
{
    public static void storeValue(String key, Object value)
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(com.fi0x.edct.Main.settings.toPath(), StandardCharsets.UTF_8));

            for(String line : fileContent)
            {
                String[] setting = line.split("=");
                if(setting.length < 2) continue;
                if(setting[0].equals(key))
                {
                    fileContent.remove(line);
                    break;
                }
            }
            fileContent.add(key + "=" + value);

            Files.write(com.fi0x.edct.Main.settings.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.WARNING("Could not write an entry to the settings file", e);
        }
    }

    public static int loadInt(String key, int defaultValue)
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(com.fi0x.edct.Main.settings.toPath(), StandardCharsets.UTF_8));

            for(String line : fileContent)
            {
                String[] setting = line.split("=");
                if(setting.length < 2) continue;

                if(setting[0].equals(key)) return Integer.parseInt(setting[1]);
            }
        } catch(IOException e)
        {
            Logger.WARNING("Could not read an integer from the settings file", e);
        }

        return defaultValue;
    }
    public static boolean loadBoolean(String key, boolean defaultValue)
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(com.fi0x.edct.Main.settings.toPath(), StandardCharsets.UTF_8));

            for(String line : fileContent)
            {
                String[] setting = line.split("=");
                if(setting.length < 2) continue;

                if(setting[0].equals(key)) return Boolean.parseBoolean(setting[1]);
            }
        } catch(IOException e)
        {
            Logger.WARNING("Could not read a boolean from the settings file", e);
        }

        return defaultValue;
    }
}
