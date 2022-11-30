package com.fi0x.edct.logic.structures;

public enum ENDPOINTS
{
    Releases("https://api.github.com/repos/Fi0x/EDCT/releases", "GET"),
    Commodities("https://inara.cz/elite/commodities-list/", "GET"),
    EDDNPrices("https://eddb.io/commodity", "GET"),
    Prices("https://inara.cz/commodities/", "GET", "pi1", "pi2", "pi3", "pi4", "pi5", "pi7", "pi8", "pi9", "pi10", "pi11", "pi12"),
    StationSearch("https://inara.cz/elite/stations/", "GET", "search"),
    StationInfo("https://inara.cz/elite/station/", "GET"),
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
