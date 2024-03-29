package com.fi0x.edct.logic.versioncontrol;

import com.fi0x.edct.Main;
import com.fi0x.edct.logic.webrequests.GitHubRequests;

import java.util.ArrayList;
import java.util.Map;

public class GitHub
{
    public static ArrayList<String> getNewerVersion()
    {
        String response = GitHubRequests.getReleases();
        if(response == null || response.equals("")) return null;

        Map<String, ArrayList<String>> releases = ReleaseCleanup.getReleases(response);

        String newestVersion = Main.version;
        for(Map.Entry<String, ArrayList<String>> version : releases.entrySet())
        {
            if(isNewer(newestVersion, version.getKey())) newestVersion = version.getKey();
        }

        return isNewer(Main.version, newestVersion) ? releases.get(newestVersion) : null;
    }

    private static boolean isNewer(String currentVersion, String nextVersion)
    {
        ArrayList<Integer> currentParts = new ArrayList<>();
        ArrayList<Integer> nextParts = new ArrayList<>();

        for(String part : currentVersion.replace(".", "-").split("-"))
        {
            currentParts.add(Integer.parseInt(part));
        }
        for(String part : nextVersion.replace(".", "-").split("-"))
        {
            nextParts.add(Integer.parseInt(part));
        }

        if(currentParts.get(0) < nextParts.get(0)) return true;
        else if(currentParts.get(0) > nextParts.get(0)) return false;

        if(currentParts.get(1) < nextParts.get(1)) return true;
        else if(currentParts.get(1) > nextParts.get(1)) return false;

        if(currentParts.get(2) < nextParts.get(2)) return true;
        else if(currentParts.get(2) > nextParts.get(2)) return false;

        return currentParts.get(3) < nextParts.get(3);
    }
}
