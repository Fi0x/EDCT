package com.fi0x.edct.util;

import com.fi0x.edct.Main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BlacklistHandler
{
    public static ArrayList<String> getBlacklistSystems()
    {
        ArrayList<String> blacklistedStations = new ArrayList<>();
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.blacklist.toPath(), StandardCharsets.UTF_8));

            for(String line : fileContent)
            {
                if(line.length() == 0) continue;
                if(line.charAt(0) != '{' || line.charAt(line.length() - 1) != '}') continue;

                blacklistedStations.add(line.substring(1, line.length() - 1));
            }
        } catch(IOException e)
        {
            Logger.WARNING("Could not read the blacklist", e);
        }

        return blacklistedStations;
    }
}
