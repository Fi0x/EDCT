package com.fi0x.edct.logic.cleanup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.annotation.Nullable;

public class HTMLCleanup
{
    @Nullable
    public static Element getStationDetails(String inputHTML)
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
