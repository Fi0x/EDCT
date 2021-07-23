package com.fi0x.edct.logic.helper;

import com.fi0x.edct.gui.controller.Details;
import com.fi0x.edct.gui.controller.Results;
import com.fi0x.edct.gui.controller.Settings;
import com.fi0x.edct.logic.filesystem.RedditHandler;
import com.fi0x.edct.logic.structures.TRADE;
import org.json.simple.JSONObject;

import javax.annotation.Nullable;
import java.util.ArrayList;

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

    @Nullable
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
            switch(part)
            {
                //TODO: Add additional text if necessary
                //TODO: Use upper/lower/pascal case
                case "COMMODITY":
                    title.append(results.getCurrentTrade().NAME);
                    break;
                case "PROFIT":
                    if(unloading) title.append(Settings.unloadingTonProfit);
                    else title.append(Settings.loadingTonProfit);
                    break;
                case "TYPE":
                    break;
                case "LINE":
                    title.append(System.lineSeparator());
                    break;
                case "CARRIER":
                    break;
                case "PAD":
                    title.append("Pad ").append(station.STATION.PAD);
                    break;
                case "QUANTITY":
                    if(unloading) title.append(station.DEMAND);
                    else title.append(station.SUPPLY);
                    break;
                case "STATION":
                    title.append(station.STATION.NAME);
                    break;
                case "STATION_PRICE":
                    if(unloading) title.append(station.BUY_PRICE);
                    else title.append(station.SELL_PRICE);
                    break;
                case "CARRIER_PRICE":
                    if(unloading) title.append(Details.carrierSell);
                    else title.append(Details.carrierBuy);
                    break;
            }
        }

        return title.toString();
    }
    @Nullable
    public static String redditContent()
    {
        ArrayList<String> lines = new ArrayList<>();

        //TODO: Add lines to reddit content

        StringBuilder content = new StringBuilder();
        for(String line : lines)
        {
            content.append(line).append("\n");
        }
        return content.toString();
    }
}
