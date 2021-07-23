package com.fi0x.edct.logic.filesystem;

import com.fi0x.edct.Main;
import com.fi0x.edct.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlacklistHandler
{
    private static final String[] DEFAULT_BLACKLIST = new String[] {
            "{Achenar}",
            "{Alioth}",
            "{Beta Hydri}",
            "{CD-43 11917}",
            "{Crom}",
            "{Exbeur}",
            "{Facece}",
            "{HIP 54530}",
            "{Hodack}",
            "{Hors}",
            "{Isinor}",
            "{Jotun}",
            "{LTT 198}",
            "{Luyten 347-14}",
            "{Nastrond}",
            "{Peregrina}",
            "{Pi Mensae}",
            "{PLX 695}",
            "{Ross 128}",
            "{Shinrarta Dezhra}",
            "{Sirius}",
            "{Sol}",
            "{Summerland}",
            "{Terra Mater}",
            "{Tiliala}",
            "{van Maanen's Star}",
            "{Vega}"
    };

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
            Logger.WARNING(992, "Could not read the blacklist", e);
        }

        return blacklistedStations;
    }

    public static void addSystemToBlacklist(String systemName)
    {
        ArrayList<String> blacklistedStations = new ArrayList<>();
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.blacklist.toPath(), StandardCharsets.UTF_8));

            fileContent.add("{" + systemName + "}");
            List<String> contentWithoutDuplicates = fileContent.stream().distinct().collect(Collectors.toList());

            Files.write(Main.blacklist.toPath(), contentWithoutDuplicates, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.WARNING(992, "Could not write an entry to the blacklist", e);
        }
    }

    public static void fillBlacklistIfEmpty()
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.blacklist.toPath(), StandardCharsets.UTF_8));

            if(fileContent.size() <= 0)
            {
                fileContent.add("This blacklist is used to store system-names that should not appear in the trade results.");
                fileContent.add("If you want to add a system, just put it's name inside curly brackets {}.");
                fileContent.add("Each system needs to be in a new row. See examples below:");
                fileContent.add("");
                fileContent.addAll(Arrays.asList(BlacklistHandler.DEFAULT_BLACKLIST));
            }

            Files.write(Main.blacklist.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.WARNING(996, "Could not write default entry to blacklist", e);
        }
    }
}
