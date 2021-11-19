package com.fi0x.edct.logic.versioncontrol;

import com.fi0x.edct.Main;
import com.fi0x.edct.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReleaseCleanup
{
    public static Map<String, ArrayList<String>> getReleases(String jsonString)
    {
        Map<String, ArrayList<String>> releaseDates = new HashMap<>();

        try
        {
            JSONArray jsonReleases = (JSONArray) new JSONParser().parse(jsonString);
            for(Object release : jsonReleases)
            {
                JSONObject releaseJson = (JSONObject) release;
                if((boolean) releaseJson.get("prerelease")) continue;
                if((boolean) releaseJson.get("draft")) continue;

                String tag = releaseJson.get("tag_name").toString();
                String url = releaseJson.get("html_url").toString();

                JSONArray assets = (JSONArray) releaseJson.get("assets");
                String runExeUrl = getAssetUrl(assets, Main.VersionType.PORTABLE);
                String installUrl = getAssetUrl(assets, Main.VersionType.INSTALLER);
                String jarUrl = getAssetUrl(assets, Main.VersionType.JAR);

                ArrayList<String> urls = new ArrayList<>();
                urls.add(url);
                urls.add(runExeUrl);
                urls.add(installUrl);
                urls.add(jarUrl);

                releaseDates.put(tag, urls);
            }
        } catch(ParseException e)
        {
            Logger.WARNING("Could not convert release-json: " + jsonString, e);
        }

        return releaseDates;
    }

    @Nullable
    private static String getAssetUrl(JSONArray jsonAssets, Main.VersionType portable)
    {
        for(Object asset : jsonAssets)
        {
            JSONObject assetJson = (JSONObject) asset;
            switch (portable)
            {
                case PORTABLE:
                    if(assetJson.get("name").equals("EDCT.exe"))
                        return assetJson.get("browser_download_url").toString();
                    break;
                case INSTALLER:
                    if(assetJson.get("name").equals("edctsetup.exe"))
                        return assetJson.get("browser_download_url").toString();
                    break;
                case JAR:
                    if(assetJson.get("name").equals("EDCT.jar"))
                        return assetJson.get("browser_download_url").toString();
                    break;
            }
        }

        return null;
    }
}
