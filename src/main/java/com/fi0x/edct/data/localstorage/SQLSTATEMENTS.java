package com.fi0x.edct.data.localstorage;

public enum SQLSTATEMENTS
{
    CreateCommodities(
            "CREATE TABLE IF NOT EXISTS Commodities (" +
                    "CommodityName TEXT NOT NULL, " +
                    "InaraID INT PRIMARY KEY)"),
    CreateStations(
            "CREATE TABLE IF NOT EXISTS Stations (" +
                    "InaraID INT NOT NULL, " +
                    "StationName TEXT NOT NULL, " +
                    "IsSeller INT NOT NULL, " +
                    "DownloadTime INT NOT NULL, " +
                    "InaraUpdateTime INT NOT NULL, " +
                    "Price INT NOT NULL, " +
                    "Quantity INT NOT NULL, " +
                    "PadSize TEXT NOT NULL, " +
                    "StationType TEXT NOT NULL, " +
                    "System TEXT NOT NULL, " +
                    "StarDistance INT NOT NULL, " +
                    "PRIMARY KEY (InaraID, StationName, IsSeller), " +
                    "FOREIGN KEY (InaraID) REFERENCES Commodities (InaraID))");

    private final String statement;

    SQLSTATEMENTS(String s)
    {
        statement = s;
    }

    public String getStatement()
    {
        return statement;
    }
}
