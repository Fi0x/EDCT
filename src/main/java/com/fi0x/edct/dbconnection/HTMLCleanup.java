package com.fi0x.edct.dbconnection;

import com.fi0x.edct.enums.PADSIZE;
import com.fi0x.edct.enums.STATIONTYPE;
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
        Element tbody = table.getElementsByTag("tbody").first();
        Elements entries = tbody.getElementsByTag("tr");

        for(Element entry : entries)
        {
            Element stationDescriptor = entry.getElementsByClass("wrap").first().getElementsByClass("inverse").first();
            String stationName = stationDescriptor.getElementsByClass("normal").first().ownText();

            String padSizeName = entry.getElementsByClass("minor").first().ownText();

            Elements tradeElements = entry.getElementsByClass("alignright lineright");
            Element quantityElement = tradeElements.first().getAllElements().first();
            int quantity = Integer.parseInt(quantityElement.getAllElements().first().ownText());
            int price = Integer.parseInt(tradeElements.last().ownText().replace(',', '.'));

            STATIONTYPE type = STATIONTYPE.ORBIT;
            if(entry.hasClass("filterable1")) type = STATIONTYPE.CARRIER;
            else if(entry.hasClass("filterable3")) type = STATIONTYPE.SURFACE;

            STATION station = new STATION(stationName, PADSIZE.getFromString(padSizeName), quantity, price, type);
            stations.add(station);
        }

        return stations;
    }
}
