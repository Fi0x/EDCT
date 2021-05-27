package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.data.structures.STATIONTYPE;
import com.fi0x.edct.util.Out;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HTMLCleanup
{
    @Deprecated
    public static Map<String, Map.Entry<String, Integer>> getCommodityIDsOLD(String inputHTML)
    {
        Map<String, Map.Entry<String, Integer>> commodities = new HashMap<>();
        Document doc = Jsoup.parse(inputHTML);

        Element body = doc.body();
        Element table = body.getElementsByClass("tablesorter").first();
        if(table == null) return commodities;
        Elements entries = table.getElementsByTag("tbody").first().getElementsByTag("tr");
        entries.remove(0);

        for(Element entry : entries)
        {
            Element commodityInfo = entry.getElementsByClass("lineright paddingleft wrap").first();
            if(commodityInfo == null) continue;
            String commodityID = commodityInfo.getElementsByTag("a").first().attr("href").replace("commodity", "").replace("/", "");
            String commodityName = commodityInfo.text();

            int maxProfit = Integer.parseInt(entry.getElementsByTag("td").last().ownText().replace(",", ""));

            commodities.put(commodityID, new AbstractMap.SimpleEntry<>(commodityName, maxProfit));
        }

        Out.newBuilder("Found " + commodities.size() + " commodities");
        return commodities;
    }
    public static Map<String, Integer> getCommodityIDs(String inputHTML)
    {
        Map<String, Integer> commodities = new HashMap<>();
        Document doc = Jsoup.parse(inputHTML);

        Element body = doc.body();
        Element table = body.getElementsByClass("tablesorter").first();
        if(table == null) return commodities;
        Elements entries = table.getElementsByTag("tbody").first().getElementsByTag("tr");
        entries.remove(0);

        for(Element entry : entries)
        {
            Element commodityInfo = entry.getElementsByClass("lineright paddingleft wrap").first();
            if(commodityInfo == null) continue;

            String commodityName = commodityInfo.text();
            String commodityIDText = commodityInfo.getElementsByTag("a").first().attr("href").replace("commodity", "").replace("/", "");
            int commodityID = Integer.parseInt(commodityIDText);

            commodities.put(commodityName, commodityID);
        }

        Out.newBuilder("Found " + commodities.size() + " commodities");
        return commodities;
    }

    public static ArrayList<STATION> getCommodityPrices(String inputHTML)
    {
        ArrayList<STATION> stations = new ArrayList<>();
        Document doc = Jsoup.parse(inputHTML);

        Element table = doc.getElementsByClass("tablesorterintab").first();
        if(table == null) return stations;
        Element tbody = table.getElementsByTag("tbody").first();
        Elements entries = tbody.getElementsByTag("tr");

        for(Element entry : entries)
        {
            Element stationDescriptor = entry.getElementsByClass("wrap").first().getElementsByClass("inverse").first();
            String system = stationDescriptor.getElementsByClass("uppercase").first().ownText();
            String stationName = stationDescriptor.getElementsByClass("normal").first().ownText();
            if(stationName.length() > 2) stationName = stationName.substring(0, stationName.length() - 2);

            String padSizeName = entry.getElementsByClass("minor").first().ownText();

            Elements tradeElements = entry.getElementsByClass("alignright lineright");
            Element quantityElement = tradeElements.first().getAllElements().first();

            String quantityText = quantityElement.getAllElements().first().ownText().replace(",", "");
            int quantity = 0;
            if(quantityText.length() > 0) quantity = Integer.parseInt(quantityText);

            String priceText = tradeElements.last().ownText().replace(",", "");
            int price = 0;
            if(priceText.length() > 0) price = Integer.parseInt(priceText);

            STATIONTYPE type;
            if(entry.hasClass("filterable1")) type = STATIONTYPE.CARRIER;
            else if(entry.hasClass("filterable3")) type = STATIONTYPE.SURFACE;
            else if(entry.hasClass("filterable4")) type = STATIONTYPE.ODYSSEY;
            else type = STATIONTYPE.ORBIT;

            String starDistanceText = entry.getElementsByClass("minor alignright lineright").first().ownText().replace(",", "").replace(" Ls", "");
            int starDistance = 0;
            if(starDistanceText.length() > 0 && !starDistanceText.equals("---")) starDistance = Integer.parseInt(starDistanceText);

            long inara_time = 0;
            String[] dataAgeString = entry.getElementsByClass("minor alignright smaller").first().ownText().split(" ");
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
            }

            STATION station = new STATION(system, stationName, PADSIZE.getFromString(padSizeName), quantity, price, type, starDistance, inara_time);
            stations.add(station);
        }

        return stations;
    }
}
