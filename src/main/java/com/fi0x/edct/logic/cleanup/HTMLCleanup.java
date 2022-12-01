package com.fi0x.edct.logic.cleanup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Objects;

public class HTMLCleanup
{
    public static ArrayList<Element> getStationTrades(String inputHTML)
    {
        ArrayList<Element> stationTrades = new ArrayList<>();

        Document doc = Jsoup.parse(inputHTML);
        Element mainContainer = doc.getElementsByClass("maincontainer").first();

        Elements mainContents = Objects.requireNonNull(mainContainer).getElementsByClass("maincontent0");
        if(mainContents.size() == 0)
            return null;

        Elements mainBlocks = Objects.requireNonNull(mainContents.first()).getElementsByClass("mainblock");
        if(mainBlocks.size() == 0)
            return null;

        Elements tables = Objects.requireNonNull(mainBlocks.last()).getElementsByTag("table");
        if(tables.size() == 0)
            return null;

        Elements bodies = Objects.requireNonNull(tables.first()).getElementsByTag("tbody");
        if(bodies.size() == 0)
            return null;

        Elements rows = Objects.requireNonNull(bodies.first()).getElementsByTag("tr");

        for(Element row : rows)
        {
            if(row.className().contains("subheader"))
                continue;
            stationTrades.add(row);
        }

        return stationTrades;
    }

    public static Element getStationDetails(String inputHTML)
    {
        Document doc = Jsoup.parse(inputHTML);

        Elements mainContainers = doc.getElementsByClass("maincontainer");
        if(mainContainers.size() == 0)
            return null;
        Elements mainContents = Objects.requireNonNull(mainContainers.first()).getElementsByClass("maincontent0");
        if(mainContents.size() == 0)
            return null;
        Elements subContents = Objects.requireNonNull(mainContents.first()).getElementsByClass("subcontent0 smallgaps");
        if(subContents.size() == 0)
            return null;
        Elements mainBlocks = Objects.requireNonNull(subContents.first()).getElementsByClass("mainblock illustrationleftsmall");
        if(mainBlocks.size() == 0)
            return null;
        Elements columns = Objects.requireNonNull(mainBlocks.first()).getElementsByClass("incontent grid2columns columnseparator");
        if(columns.size() == 0)
            return null;

        return Objects.requireNonNull(columns.first()).getElementsByTag("div").first();
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
