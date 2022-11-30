package com.fi0x.edct.logic.cleanup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Objects;

public class HTMLCleanup
{
    public static Element getStationDetails(String inputHTML)
    {
        //TODO: Adjust for new inara
        Elements mainconten1s = getStationNameAndSystem(inputHTML);
        if(mainconten1s == null || mainconten1s.size() == 0) return null;

        Elements mainblocks = Objects.requireNonNull(mainconten1s.first()).getElementsByClass("mainblock");
        if(mainblocks.size() == 0) return null;

        for(Element block : mainblocks)
        {
            String blockText = block.toString().toLowerCase();
            if(blockText.contains("class=\"mainblock\"") && (blockText.contains("landing pad") || blockText.contains("station type") || blockText.contains("href=\"/station/")))
                return block;
        }
        return null;
    }

    public static ArrayList<Element> getStationTrades(String inputHTML)
    {
        //TODO: Adjust for new inara
        ArrayList<Element> stationTrades = new ArrayList<>();

        Elements mainconten1s = getStationNameAndSystem(inputHTML);
        if(mainconten1s == null || mainconten1s.size() == 0) return null;

        Elements mainblocks = Objects.requireNonNull(mainconten1s.first()).getElementsByClass("mainblock maintable");
        if(mainblocks.size() == 0) return null;

        Elements tables = Objects.requireNonNull(mainblocks.first()).getElementsByTag("table");
        if(tables.size() == 0) return null;

        Elements bodies = Objects.requireNonNull(tables.first()).getElementsByTag("tbody");
        if(bodies.size() == 0) return null;

        Elements rows = Objects.requireNonNull(bodies.first()).getElementsByTag("tr");

        for(Element row : rows)
        {
            if(row.hasClass("subheader")) continue;
            stationTrades.add(row);
        }

        return stationTrades;
    }

    public static Elements getStationNameAndSystem(String inputHTML)
    {
        Document doc = Jsoup.parse(inputHTML);

        Elements mainContainers = doc.getElementsByClass("maincontainer");
        if(mainContainers.size() == 0)
            return null;

        Elements mainContents = Objects.requireNonNull(mainContainers.first()).getElementsByClass("maincontent1 fullwidth");
        if(mainContents.size() == 0)
            return null;

        Elements mainBlocks = Objects.requireNonNull(mainContents.first()).getElementsByClass("mainblock");
        if(mainBlocks.size() == 0)
            return null;

        Elements table = Objects.requireNonNull(mainBlocks.first()).getElementsByClass("incontentlist columns3");
        if(table.size() == 0)
            return null;

        return Objects.requireNonNull(table.first()).getElementsByTag("a");
    }
}
