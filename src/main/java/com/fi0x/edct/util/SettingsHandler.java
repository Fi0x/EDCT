package com.fi0x.edct.util;

import com.fi0x.edct.controller.Settings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsHandler
{
    public static void verifyIntegrity()
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(com.fi0x.edct.Main.settings.toPath(), StandardCharsets.UTF_8));
            ArrayList<String> invalidLines = new ArrayList<>();

            for(String line : fileContent)
            {
                String[] setting = line.split("=");
                if(setting.length < 2) invalidLines.add(line);
                switch(setting[0])
                {
                    case "carrier":
                    case "demand":
                    case "odyssey":
                    case "pad":
                    case "surface":
                        if(!setting[1].equalsIgnoreCase("true") && !setting[1].equalsIgnoreCase("false")) invalidLines.add(line);
                        break;
                    case "dataAge":
                    case "highProfit":
                    case "inaraDelay":
                    case "lowProfit":
                    case "quantity":
                        try
                        {
                            Integer.parseInt(setting[1]);
                        } catch(NumberFormatException e)
                        {
                            invalidLines.add(line);
                        }
                        break;
                    case "detailedResults":
                        boolean valid = false;
                        for(Settings.Details dt : Settings.Details.values())
                        {
                            if(setting[1].equalsIgnoreCase(dt.name()))
                            {
                                valid = true;
                                break;
                            }
                        }
                        if(!valid) invalidLines.add(line);
                        break;
                }
            }

            for(String line : invalidLines)
            {
                fileContent.remove(line);
            }
            Files.write(com.fi0x.edct.Main.settings.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.WARNING("Could not remove an entry in the settings file", e);
        }
        Logger.INFO("Verified integrity of settings file");
    }

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
        } catch(NumberFormatException e)
        {
            Logger.WARNING("Could not parse an integer from the settings file");
        }

        verifyIntegrity();
        storeValue(key, defaultValue);

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

        verifyIntegrity();
        storeValue(key, defaultValue);

        return defaultValue;
    }
    public static String loadString(String key, String defaultValue)
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(com.fi0x.edct.Main.settings.toPath(), StandardCharsets.UTF_8));

            for(String line : fileContent)
            {
                String[] setting = line.split("=");
                if(setting.length < 2) continue;

                if(setting[0].equals(key)) return setting[1];
            }
        } catch(IOException e)
        {
            Logger.WARNING("Could not read a boolean from the settings file", e);
        }

        verifyIntegrity();
        storeValue(key, defaultValue);

        return defaultValue;
    }
    public static Settings.Details loadDetails(String key, Settings.Details defaultValue)
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(com.fi0x.edct.Main.settings.toPath(), StandardCharsets.UTF_8));

            for(String line : fileContent)
            {
                String[] setting = line.split("=");
                if(setting.length < 2) continue;

                if(setting[0].equals(key)) return Settings.Details.valueOf(setting[1]);
            }
        } catch(IOException e)
        {
            Logger.WARNING("Could not read a boolean from the settings file", e);
        } catch(IllegalArgumentException e)
        {
            Logger.WARNING("Could not parse the detailed mode from the settings file");
        }

        verifyIntegrity();
        storeValue(key, defaultValue);

        return defaultValue;
    }

    public static void addSettingsToMap(Map<String, String> props)
    {
        verifyIntegrity();

        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(com.fi0x.edct.Main.settings.toPath(), StandardCharsets.UTF_8));

            for(String line : fileContent)
            {
                String[] setting = line.split("=");
                if(setting.length < 2) continue;

                switch(setting[0])
                {
                    case "lowProfit":
                    case "highProfit":
                    case "dataAge":
                    case "inaraDelay":
                    case "detailedResults":
                    case "shipCargoSpace":
                    case "loadingProfit":
                    case "unloadingProfit":
                        props.put(setting[0], setting[1]);
                        break;
                }
            }
        } catch(IOException e)
        {
            Logger.WARNING("Could not read the content of the settings file", e);
        }
    }

    public static void addFiltersToMap(Map<String, String> props)
    {
        verifyIntegrity();

        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(com.fi0x.edct.Main.settings.toPath(), StandardCharsets.UTF_8));

            for(String line : fileContent)
            {
                String[] setting = line.split("=");
                if(setting.length < 2) continue;

                switch(setting[0])
                {
                    case "filterQuantity":
                    case "filterCarrier":
                    case "filterSurface":
                    case "filterLandingPad":
                    case "filterDemand":
                    case "filterOdyssey":
                        props.put(setting[0], setting[1]);
                        break;
                }
            }
        } catch(IOException e)
        {
            Logger.WARNING("Could not read the content of the settings file", e);
        }
    }
}
