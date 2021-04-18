package com.fi0x.edct.dbconnection;

import com.sun.deploy.net.HttpRequest;

public enum ENDPOINTS
{
    Commodities("https://inara.cz/galaxy-commodities/", "GET"),
    Prices("https://inara.cz/ajaxaction.php", "GET", "act", "refname", "refid", "refid2");

    private final String url;
    private final String type;
    private final String[] parameter;

    private ENDPOINTS(String url, String type, String... parameter)
    {
        this.url = url;
        this.type = type;
        this.parameter = parameter;
    }
}
