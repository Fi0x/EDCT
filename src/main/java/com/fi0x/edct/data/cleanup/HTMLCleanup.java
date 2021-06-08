package com.fi0x.edct.data.cleanup;

import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.data.structures.STATIONTYPE;
import com.fi0x.edct.util.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HTMLCleanup
{
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
            } else if(dataAgeString.length == 1 && dataAgeString[0].equals("now"))
            {
                inara_time = System.currentTimeMillis();
            }

            STATION station = new STATION(system, stationName, PADSIZE.getFromString(padSizeName), quantity, price, type, inara_time);
            stations.add(station);
        }

        return stations;
    }

    @Nullable
    public static String getStationID(String inputHTML, String stationName, String systemName)
    {
        String stationID = null;

        Element details = getStationDetails(inputHTML);
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

    @Nullable
    public static PADSIZE getStationPad(String inputHTML)
    {
        PADSIZE padsize = PADSIZE.S;

        Element details = getStationDetails(inputHTML);
        if(details == null) return null;

        for(Element pair : details.getElementsByClass("itempaircontainer"))
        {
            String pairText = pair.toString().toLowerCase();
            if(pairText.contains("landing pad"))
            {
                if(pairText.contains("none")) return null;
                else if(pairText.contains("large")) padsize = PADSIZE.L;
                else if(pairText.contains("medium")) padsize = PADSIZE.M;
                break;
            }
        }

        return padsize;
    }

    @Nullable
    public static STATIONTYPE getStationType(String inputHTML)
    {
        STATIONTYPE type = null;

        Element details = getStationDetails(inputHTML);
        if(details == null) return null;

        for(Element pair : details.getElementsByClass("itempaircontainer"))
        {
            if(pair.toString().toLowerCase().contains("station type"))
            {
                String typeName = pair.getElementsByClass("itempairvalue").first().ownText().toLowerCase();
                if(typeName.contains("odyssey")) type = STATIONTYPE.ODYSSEY;
                else if(typeName.contains("fleet") || typeName.contains("carrier")) type = STATIONTYPE.CARRIER;
                else if(typeName.contains("surface")) type = STATIONTYPE.SURFACE;
                else if(typeName.contains("starport") || typeName.contains("outpost")) type = STATIONTYPE.ORBIT;
            }
        }

        return type;
    }

    @Nullable
    private static Element getStationDetails(String inputHTML)
    {
        Document doc = Jsoup.parse(inputHTML);

        Elements maincons = doc.getElementsByClass("maincon");
        if(maincons.size() == 0) return null;

        Elements containermains = maincons.first().getElementsByClass("containermain");
        if(containermains.size() == 0) return null;

        Elements maincontentcontainers = containermains.first().getElementsByClass("maincontentcontainer");
        if(maincontentcontainers.size() == 0) return null;

        Elements mainconten1s = maincontentcontainers.first().getElementsByClass("maincontent1");
        if(mainconten1s.size() == 0) return null;

        Elements mainblocks = mainconten1s.first().getElementsByClass("mainblock");
        if(mainblocks.size() == 0) return null;

        for(Element block : mainblocks)
        {
            String blockText = block.toString().toLowerCase();
            if(blockText.contains("class=\"mainblock\"") && (blockText.contains("landing pad") || blockText.contains("station type") || blockText.contains("href=\"/station/")))
            return block;

        }
        return null;
    }
}
