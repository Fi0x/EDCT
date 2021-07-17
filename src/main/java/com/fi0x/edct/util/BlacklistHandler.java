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

    public static final String[] DEFAULT_BLACKLIST = new String[] {
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
}
