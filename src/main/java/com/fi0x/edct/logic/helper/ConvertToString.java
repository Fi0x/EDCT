package com.fi0x.edct.logic.helper;

import com.fi0x.edct.gui.controller.Details;
import com.fi0x.edct.gui.controller.Filters;
import com.fi0x.edct.gui.controller.Results;
import com.fi0x.edct.gui.controller.Settings;
import com.fi0x.edct.logic.filesystem.ConfigHandler;
import com.fi0x.edct.logic.filesystem.DiscordHandler;
import com.fi0x.edct.logic.filesystem.RedditHandler;
import com.fi0x.edct.logic.structures.TRADE;
import org.json.simple.JSONObject;

import java.util.Locale;

public class ConvertToString
{
    public static String ageText(long age)
    {
        String text = "Local data age: ";
        if(age < (60 * 1000)) text += age / 1000 + "s";
        else if(age < (60 * 60 * 1000)) text += age / (60 * 1000) + "min";
        else if(age < (24 * 60 * 60 * 1000)) text += age / (60 * 60 * 1000) + "h";
        else text += age / (24 * 60 * 60 * 1000) + "d";

        return text;
    }

    public static String redditTitle(Results results, TRADE station, boolean unloading)
    {
        JSONObject redditConfig = RedditHandler.getRedditConfig();
        if(redditConfig == null) return null;
        RedditHandler.addMissingKeys(redditConfig);

        StringBuilder title = new StringBuilder();

        String titleConfig = redditConfig.get("Title Structure").toString();
        String[] titleParts = titleConfig.split(" ");
        for(String part : titleParts)
        {
            title.append(transformAndAddKey(part, redditConfig, results, station, unloading));
        }

        return title.toString();
    }
    public static String redditContent(Results results, TRADE station, boolean unloading)
    {
        JSONObject redditConfig = RedditHandler.getRedditConfig();
        if(redditConfig == null) return null;
        RedditHandler.addMissingKeys(redditConfig);

        StringBuilder content = new StringBuilder();

        String contentConfig = redditConfig.get("Text Structure").toString();
        String[] contentParts = contentConfig.split(" ");
        for(String part : contentParts)
        {
            content.append(transformAndAddKey(part, redditConfig, results, station, unloading));
        }

        return content.toString();
    }
    public static String discordText(Results results, TRADE station, boolean unloading)
    {
        JSONObject discordConfig = DiscordHandler.getDiscordConfig();
        if(discordConfig == null) return null;
        DiscordHandler.addMissingKeys(discordConfig);

        StringBuilder content = new StringBuilder();

        String contentConfig = discordConfig.get("Text Structure").toString();
        String[] contentParts = contentConfig.split(" ");
        for(String part : contentParts)
        {
            content.append(transformAndAddKey(part, discordConfig, results, station, unloading));
        }

        return content.toString();
    }

    private static String transformAndAddKey(String key, JSONObject config, Results results, TRADE station, boolean unloading)
    {
        boolean prefix = key.charAt(0) == '+';
        boolean suffix = key.charAt(key.length() - 1) == '+';

        StringBuilder part = new StringBuilder();
        switch(key.toUpperCase(Locale.ROOT).replace("+", ""))
        {
            case "LINE":
                part.append(System.lineSeparator());
                break;
            case "COMMODITY":
                if(prefix) part.append(ConfigHandler.getValue(config, "COMMODITY", unloading, "PREFIX"));
                part.append(results.getCurrentTrade().NAME);
                if(suffix) part.append(ConfigHandler.getValue(config, "COMMODITY", unloading, "SUFFIX"));
                break;
            case "PROFIT":
                if(prefix) part.append(ConfigHandler.getValue(config, "PROFIT", unloading, "PREFIX"));
                if(unloading) part.append(Settings.unloadingTonProfit / 1000).append("k");
                else part.append(Settings.loadingTonProfit / 1000).append("k");
                if(suffix) part.append(ConfigHandler.getValue(config, "PROFIT", unloading, "SUFFIX"));
                break;
            case "PAD":
                if(prefix) part.append(ConfigHandler.getValue(config, "PAD", unloading, "PREFIX"));
                part.append(station.STATION.PAD);
                if(suffix) part.append(ConfigHandler.getValue(config, "PAD", unloading, "SUFFIX"));
                break;
            case "STAR_DISTANCE":
                if(prefix) part.append(ConfigHandler.getValue(config, "STAR_DISTANCE", unloading, "PREFIX"));
                part.append(station.STATION.DISTANCE_TO_STAR);
                if(suffix) part.append(ConfigHandler.getValue(config, "STAR_DISTANCE", unloading, "SUFFIX"));
                break;
            case "QUANTITY":
                assert Filters.getInstance() != null;
                if(prefix) part.append(ConfigHandler.getValue(config, "QUANTITY", unloading, "PREFIX"));
                part.append(Integer.parseInt(Filters.getInstance().txtQuantity.getText()) / 1000).append("k");
                if(suffix) part.append(ConfigHandler.getValue(config, "QUANTITY", unloading, "SUFFIX"));
                break;
            case "STATION":
                if(prefix) part.append(ConfigHandler.getValue(config, "STATION", unloading, "PREFIX"));
                part.append(station.STATION.NAME);
                if(suffix) part.append(ConfigHandler.getValue(config, "STATION", unloading, "SUFFIX"));
                break;
            case "SYSTEM":
                if(prefix) part.append(ConfigHandler.getValue(config, "SYSTEM", unloading, "PREFIX"));
                part.append(station.STATION.SYSTEM);
                if(suffix) part.append(ConfigHandler.getValue(config, "SYSTEM", unloading, "SUFFIX"));
                break;
            case "STATION_PRICE":
                if(prefix) part.append(ConfigHandler.getValue(config, "STATION_PRICE", unloading, "PREFIX"));
                if(unloading) part.append(station.BUY_PRICE);
                else part.append(station.SELL_PRICE);
                if(suffix) part.append(ConfigHandler.getValue(config, "STATION_PRICE", unloading, "SUFFIX"));
                break;
            case "CARRIER_PRICE":
                if(prefix) part.append(ConfigHandler.getValue(config, "CARRIER_PRICE", unloading, "PREFIX"));
                if(unloading) part.append(Details.carrierSell);
                else part.append(Details.carrierBuy);
                if(suffix) part.append(ConfigHandler.getValue(config, "CARRIER_PRICE", unloading, "SUFFIX"));
                break;
            default:
                part.append(ConfigHandler.getValue(config, key.replace("+", "").toUpperCase(Locale.ROOT), unloading, null));
                break;
        }

        key = key.replace("+", "");

        if(Character.isLowerCase(key.charAt(0))) return part.toString().toLowerCase(Locale.ROOT);
        else if(Character.isUpperCase(key.charAt(1))) return part.toString().toUpperCase(Locale.ROOT);

        return part.toString();
    }
}
