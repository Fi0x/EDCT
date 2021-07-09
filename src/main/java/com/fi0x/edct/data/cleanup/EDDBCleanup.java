package com.fi0x.edct.data.cleanup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class EDDBCleanup
{
    public static Map<String, Integer> getCommodityAveragePrice(String inputHTML)
    {
        Map<String, Integer> nameAverageMap = new HashMap<>();
        Document doc = Jsoup.parse(inputHTML);
        Element body = doc.body();

        Element siteContent = body.getElementsByClass("site-wrap").first();
        if(siteContent == null) return nameAverageMap;

        Element mainContent = siteContent.getElementsByClass("container-fluid container-main").first();
        if(mainContent == null) return nameAverageMap;

        Element tableWrap = mainContent.getElementsByClass("panel panel-eddb").first().getElementsByClass("table-wrap").get(1);
        if(tableWrap == null) return nameAverageMap;

        Element table = tableWrap.getElementsByClass("table panel-table table-striped").first().getElementsByTag("tbody").first();
        if(table == null) return nameAverageMap;

        Elements entries = table.getElementsByTag("tr");
        for(Element entry : entries)
        {
            Elements columns = entry.getElementsByTag("td");
            String commodityName = columns.get(1).text();
            String averagePriceText = columns.get(3).text().replace(",", "").replace(" ", "");

            if(averagePriceText.length() <= 0) continue;

            int averagePrice = Integer.parseInt(averagePriceText);

            nameAverageMap.put(commodityName, averagePrice);
        }

        return nameAverageMap;
    }
}
