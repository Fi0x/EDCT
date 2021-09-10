package com.fi0x.edct.logic.cleanup;

import com.fi0x.edct.logic.structures.PADSIZE;
import com.fi0x.edct.logic.structures.STATION;
import com.fi0x.edct.logic.structures.STATIONTYPE;
import com.fi0x.edct.logic.structures.TRADE;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.annotation.Nullable;
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
        Element table = body.getElementsByClass("tablesorter").first();
        if(table == null) return commodities;
        Elements entries = Objects.requireNonNull(table.getElementsByTag("tbody").first()).getElementsByTag("tr");
        entries.remove(0);

        for(Element entry : entries)
        {
            Element commodityInfo = entry.getElementsByClass("lineright paddingleft wrap").first();
            if(commodityInfo == null) continue;

            String commodityName = commodityInfo.text();
            String commodityIDText = Objects.requireNonNull(commodityInfo.getElementsByTag("a").first()).attr("href").replace("commodity", "").replace("/", "");
            int commodityID = Integer.parseInt(commodityIDText);

            commodities.put(commodityName, commodityID);
        }

        return commodities;
    }

    public static ArrayList<TRADE> getCommodityPrices(int commodityID, String inputHTML, boolean isSeller)
    {
        ArrayList<TRADE> stations = new ArrayList<>();
        Document doc = Jsoup.parse(inputHTML);

        Element table = doc.getElementsByClass("tablesorterintab").first();
        if(table == null) return stations;
        Element tbody = table.getElementsByTag("tbody").first();
        Elements entries = Objects.requireNonNull(tbody).getElementsByTag("tr");

        for(Element entry : entries)
        {
            Element stationDescriptor = Objects.requireNonNull(entry.getElementsByClass("wrap").first()).getElementsByClass("inverse").first();
            String system = Objects.requireNonNull(Objects.requireNonNull(stationDescriptor).getElementsByClass("uppercase").first()).ownText();
            String stationName = Objects.requireNonNull(stationDescriptor.getElementsByClass("normal").first()).ownText();
            if(stationName.length() > 2) stationName = stationName.substring(0, stationName.length() - 2);

            String padSizeName = Objects.requireNonNull(entry.getElementsByClass("minor").first()).ownText();

            Elements tradeElements = entry.getElementsByClass("alignright lineright");
            Element quantityElement = Objects.requireNonNull(tradeElements.first()).getAllElements().first();

            String quantityText = Objects.requireNonNull(Objects.requireNonNull(quantityElement).getAllElements().first()).ownText().replace(",", "");

            String priceText = Objects.requireNonNull(tradeElements.last()).ownText().replace(",", "");

            var q = quantityText.length() > 0 ? Integer.parseInt(quantityText) : 0;
            var p = priceText.length() > 0 ? Integer.parseInt(priceText) : 0;
            int supply = isSeller ? q : 0;
            int demand = isSeller ? 0 : q;
            int buyPrice = isSeller ? 0 : p;
            int sellPrice = isSeller ? p : 0;

            STATIONTYPE type;
            if(entry.hasClass("filterable1")) type = STATIONTYPE.CARRIER;
            else if(entry.hasClass("filterable3")) type = STATIONTYPE.SURFACE;
            else if(entry.hasClass("filterable4")) type = STATIONTYPE.ODYSSEY;
            else type = STATIONTYPE.ORBIT;

            Element starDistanceElement = entry.getElementsByClass("minor alignright lineright").first();
            String starDistanceText = Objects.requireNonNull(starDistanceElement).ownText();
            double starDistance = Double.parseDouble(starDistanceText.replace(",", "").replace(" Ls", "").replace("---", "-1"));

            long inara_time = 0;
            String[] dataAgeString = Objects.requireNonNull(entry.getElementsByClass("minor alignright smaller").first()).ownText().split(" ");
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
            TRADE trade = new TRADE(station, commodityID, inara_time, supply, demand, buyPrice, sellPrice);
            stations.add(trade);
        }

        return stations;
    }

    @Nullable
    public static String getStationID(String inputHTML, String stationName, String systemName)
    {
        String stationID = null;

        Element details = HTMLCleanup.getStationDetails(inputHTML);
        if(details == null) return null;

        for(Element station : details.getElementsByTag("a"))
        {
            String stationEntry = station.toString();
            if(stationEntry.contains(stationName) && stationEntry.contains(systemName))
            {
                stationID = station.attr("href").replace("station", "").replace("/", "");
                break;
            }
        }
        return stationID;
    }
}
