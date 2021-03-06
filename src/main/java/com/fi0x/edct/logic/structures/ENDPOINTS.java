package com.fi0x.edct.logic.structures;

public enum ENDPOINTS
{
    Releases("https://api.github.com/repos/Fi0x/EDCT/releases", "GET"),
    Commodities("https://inara.cz/galaxy-commodities/", "GET"),
    EDDNPrices("https://eddb.io/commodity", "GET"),
    Prices("https://inara.cz/ajaxaction.php", "GET", "act", "refname", "refid", "refid2"),
    StationSearch("https://inara.cz/station/", "GET", "search"),
    EDSMCoordinates("https://www.edsm.net/api-v1/system", "GET", "systemName", "showCoordinates");

    public final String url;
    public final String type;
    public final String[] parameter;

    ENDPOINTS(String url, String type, String... parameter)
    {
        this.url = url;
        this.type = type;
        this.parameter = parameter;
    }
}
