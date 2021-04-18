package com.fi0x.edct.dbconnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class HTMLCleanup
{
    public static Map<String, String> getCommodityIDs(String inputHTML)
    {
        Map<String, String> commodities = new HashMap<>();

        Document doc = Jsoup.parse(inputHTML);
        Element body = doc.body();
        for(Element div : body.getElementsByClass("formelement formselect"))
        {
            Elements options = div.select("select > option");
            for(Element option : options)
            {
                commodities.put(option.attr("value"), option.text());
            }
        }
        return commodities;
    }
}
