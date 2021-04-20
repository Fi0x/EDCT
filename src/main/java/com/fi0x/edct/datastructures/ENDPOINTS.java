package com.fi0x.edct.datastructures;

public enum ENDPOINTS
{
    Commodities("https://inara.cz/galaxy-commodities/", "GET"),
    Prices("https://inara.cz/ajaxaction.php", "GET", "act", "refname", "refid", "refid2");

    public final String url;
    public final String type;
    public final String[] parameter;

    private ENDPOINTS(String url, String type, String... parameter)
    {
        this.url = url;
        this.type = type;
        this.parameter = parameter;
    }
}
