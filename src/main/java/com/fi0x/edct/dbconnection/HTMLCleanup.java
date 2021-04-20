package com.fi0x.edct.dbconnection;

import com.fi0x.edct.datastructures.PADSIZE;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.datastructures.STATIONTYPE;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HTMLCleanup
{
    public static Map<String, String> getCommodityIDs(String inputHTML)
    {
        Map<String, String> commodities = new HashMap<>();
        Document doc = Jsoup.parse(inputHTML);

        Element body = doc.body();
        Element div = body.getElementsByClass("formelement formselect").first();
        if(div == null) return commodities;

        Elements options = div.select("select > option");
        for(Element option : options)
        {
            commodities.put(option.attr("value"), option.text());
        }

        return commodities;
    }

    public static ArrayList<STATION> getCommodityPrices(String inputHTML)
    {
        ArrayList<STATION> stations = new ArrayList<>();
        Document doc = Jsoup.parse(inputHTML);

        Element table = doc.getElementsByClass("tablesorterintab").first();
        if(table == null) return null;
        Element tbody = table.getElementsByTag("tbody").first();
        Elements entries = tbody.getElementsByTag("tr");

        for(Element entry : entries)
        {
            Element stationDescriptor = entry.getElementsByClass("wrap").first().getElementsByClass("inverse").first();
            String stationName = stationDescriptor.getElementsByClass("normal").first().ownText();

            String padSizeName = entry.getElementsByClass("minor").first().ownText();

            Elements tradeElements = entry.getElementsByClass("alignright lineright");
            Element quantityElement = tradeElements.first().getAllElements().first();

            String quantityText = quantityElement.getAllElements().first().ownText().replace(",", "");
            int quantity = 0;
            if(quantityText.length() > 0) quantity = Integer.parseInt(quantityText);

            String priceText = tradeElements.last().ownText().replace(",", "");
            int price = 0;
            if(priceText.length() > 0) price = Integer.parseInt(priceText);

            STATIONTYPE type = STATIONTYPE.ORBIT;
            if(entry.hasClass("filterable1")) type = STATIONTYPE.CARRIER;
            else if(entry.hasClass("filterable3")) type = STATIONTYPE.SURFACE;

            STATION station = new STATION(stationName, PADSIZE.getFromString(padSizeName), quantity, price, type);
            stations.add(station);
        }

        return stations;
    }
}
