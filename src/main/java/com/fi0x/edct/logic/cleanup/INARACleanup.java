package com.fi0x.edct.logic.cleanup;

import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.structures.*;
import io.fi0x.javalogger.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class INARACleanup
{
    public static Map<String, Integer> getCommodityIDs(String inputHTML)
    {
        Map<String, Integer> commodities = new HashMap<>();
        Document doc = Jsoup.parse(inputHTML);

        Element body = doc.body();
        Element table = body.getElementsByClass("tablesortercollapsed").first();
        if(table == null) return commodities;
        Elements entries = Objects.requireNonNull(table.getElementsByTag("tbody").first()).getElementsByTag("tr");
        entries.remove(0);

        for(Element entry : entries)
        {
            Element commodityInfo = entry.getElementsByClass("lineright wrap").first();
            if(commodityInfo == null) continue;

            String commodityName = commodityInfo.text();
            String commodityIDText = Objects.requireNonNull(commodityInfo.getElementsByTag("a").first()).attr("href").replace("elite/commodity", "").replace("/", "");
            int commodityID = Integer.parseInt(commodityIDText);

            commodities.put(commodityName, commodityID);
        }

        return commodities;
    }

    public static ArrayList<TRADE> getCommodityPrices(int commodityID, String inputHTML, boolean isExportStation)
    {
        ArrayList<TRADE> stations = new ArrayList<>();
        Document doc = Jsoup.parse(inputHTML);

        Element table = doc.getElementsByClass("tablesortercollapsed").first();
        if(table == null)
            return stations;

        Element tbody = table.getElementsByTag("tbody").first();
        Elements entries = Objects.requireNonNull(tbody).getElementsByTag("tr");

        for(Element entry : entries)
        {
            Element stationDescriptor = Objects.requireNonNull(entry.getElementsByClass("wrap").first()).getElementsByTag("a").first();
            String system = Objects.requireNonNull(Objects.requireNonNull(stationDescriptor).getElementsByClass("uppercase nowrap").first()).ownText();
            String stationName = Objects.requireNonNull(stationDescriptor.getElementsByClass("standardcase standardcolor nowrap").first()).ownText();
            if(stationName.length() > 2)
                stationName = stationName.substring(0, stationName.length() - 2);

            String padSizeName = Objects.requireNonNull(entry.getElementsByClass("alignright minor").first()).ownText();

            int q = 0;
            int p = 0;
            Elements tradeElements = entry.getElementsByClass("alignright lineright");
            if(tradeElements.size() < 2)
                tradeElements.addAll(entry.getElementsByClass("alignright lineright wrap"));
            if(tradeElements.size() == 2)
            {
                String quantityText = Objects.requireNonNull(tradeElements.first()).ownText().replace(",", "");
                String priceText = Objects.requireNonNull(tradeElements.last()).ownText().replace(",", "");
                q = quantityText.length() > 0 ? Integer.parseInt(quantityText) : 0;
                p = priceText.length() > 0 ? Integer.parseInt(priceText) : 0;
            } else
                Logger.log("Trade elements for commodity " + DBHandler.getCommodityNameByID(commodityID) + " are not 2 but: " + tradeElements.size() + " in entry: " + entry, LogName.WARNING);

            int supply = isExportStation ? q : 0;
            int demand = isExportStation ? 0 : q;
            int importStationPrice = isExportStation ? 0 : p;
            int exportStationPrice = isExportStation ? p : 0;

            STATIONTYPE type = STATIONTYPE.ORBIT;

            Element starDistanceElement = entry.getElementsByClass("minor alignright lineright").first();
            String starDistanceText = Objects.requireNonNull(starDistanceElement).ownText();
            double starDistance = -1;
            try
            {
                starDistance = Double.parseDouble(starDistanceText.replace(",", "").replace(" Ls", "").replace("-", "-1"));
            } catch(NumberFormatException e)
            {
                Logger.log("Could not parse the star-distance for station '" + stationName + "'", LogName.WARNING);
            }

            long inara_time = 0;
            String[] dataAgeString = Objects.requireNonNull(entry.getElementsByClass("minor alignright small").first()).ownText().split(" ");
            if(dataAgeString.length == 3)
            {
                inara_time = Long.parseLong(dataAgeString[0]);
                switch(dataAgeString[1])
                {
                    case "days":
                        inara_time *= 24;
                    case "hours":
                        inara_time *= 60;
                    case "minutes":
                        inara_time *= 60;
                    default:
                        inara_time *= 1000;
                }
                inara_time = System.currentTimeMillis() - inara_time;
            } else if(dataAgeString.length == 1 && dataAgeString[0].equals("now"))
            {
                inara_time = System.currentTimeMillis();
            }

            STATION station = new STATION(system, stationName, PADSIZE.getFromString(padSizeName), type, starDistance);
            TRADE trade = new TRADE(station, commodityID, inara_time, supply, demand, importStationPrice, exportStationPrice);
            stations.add(trade);
        }

        return stations;
    }

    public static String getStationID(String inputHTML, String stationName, String systemName)
    {
        String stationID = null;

        Elements stations = HTMLCleanup.getStationNameAndSystem(inputHTML);
        if(stations == null)
            return null;

        for(Element station : stations)
        {
            String stationEntry = station.toString();
            if(stationEntry.contains(stationName) && stationEntry.contains(systemName))
            {
                stationID = station.attr("href").replace("elite/station", "").replace("/", "");
                break;
            }
        }
        return stationID;
    }

    public static ArrayList<TRADE> getCommodityTradesForStation(String inputHTML, String systemName, String stationName)
    {
        ArrayList<TRADE> results = new ArrayList<>();

        ArrayList<Element> stationCommodities = HTMLCleanup.getStationTrades(inputHTML);
        if(stationCommodities == null)
            return results;

        String marketTime = HTMLCleanup.getStationAge(inputHTML);
        long updateTime = System.currentTimeMillis();
        if(marketTime != null && !marketTime.contains("now"))
        {
            updateTime = Long.parseLong(marketTime.split(" ")[0]);
            switch(marketTime.split(" ")[1])
            {
                case "days":
                    updateTime *= 24;
                case "hours":
                    updateTime *= 60;
                case "minutes":
                    updateTime *= 60;
                default:
                    updateTime *= 1000;
            }
            updateTime = System.currentTimeMillis() - updateTime;
        }
        else
            Logger.log("Did not receive a maket update time from station", LogName.WARNING);

        for(Element commodity : stationCommodities)
        {
            Elements cols = commodity.getElementsByTag("td");
            int inaraID = getIntFromString(Objects.requireNonNull(cols.get(0).getElementsByTag("a").first()).attr("href"));
            int importPrice = getIntFromString(Objects.requireNonNull(cols.get(1).getElementsByTag("span").first()).ownText());
            int demand = getIntFromString(cols.get(2).ownText());
            int exportPrice = getIntFromString(Objects.requireNonNull(cols.get(3).getElementsByTag("span").first()).ownText());
            int supply = getIntFromString(cols.get(4).ownText());

            STATION s = DBHandler.getStation(systemName, stationName);
            results.add(new TRADE(s, inaraID, updateTime, supply, demand, importPrice, exportPrice));
        }

        return results;
    }

    private static int getIntFromString(String input)
    {
        String cleanString = input.replace("elite/commodity", "").replace("/", "");
        cleanString = cleanString.replace("-", "0").replace(",", "");
        cleanString = cleanString.replace("Cr", "");

        if(cleanString.equals("")) return 0;
        return Integer.parseInt(cleanString);
    }
}
