package com.fi0x.edct.logic.filesystem;

import com.fi0x.edct.Main;
import com.fi0x.edct.logging.LogName;
import io.fi0x.javalogger.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            Logger.log("Could not read the blacklist", LogName.WARNING, e, 992);
        }

        return blacklistedStations;
    }

    public static void addSystemToBlacklist(String systemName)
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.blacklist.toPath(), StandardCharsets.UTF_8));

            fileContent.add("{" + systemName + "}");
            List<String> contentWithoutDuplicates = fileContent.stream().distinct().collect(Collectors.toList());

            Files.write(Main.blacklist.toPath(), contentWithoutDuplicates, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.log("Could not write an entry to the blacklist", LogName.WARNING, e, 992);
        }
    }

    public static void fillBlacklistIfEmpty()
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.blacklist.toPath(), StandardCharsets.UTF_8));

            if(fileContent.size() <= 0)
            {
                var jsonInput = Main.class.getResourceAsStream("/defaults/blacklist.txt");
                assert jsonInput != null;
                fileContent = new BufferedReader(new InputStreamReader(jsonInput, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
            }

            Files.write(Main.blacklist.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.log("Could not write default entry to blacklist", LogName.WARNING, e, 996);
        }
    }
}
