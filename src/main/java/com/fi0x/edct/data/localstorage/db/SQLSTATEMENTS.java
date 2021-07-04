package com.fi0x.edct.data.localstorage.db;

public enum SQLSTATEMENTS
{
    CreateCommodities(
            "CREATE TABLE IF NOT EXISTS Commodities (" +
                    "InaraID INT PRIMARY KEY, " +
                    "CommodityName TEXT NOT NULL, " +
                    "GalacticAverage INT DEFAULT 0, " +
                    "LastUpdated TEXT DEFAULT '0')"
    ),
    CreateSystems(
            "CREATE TABLE IF NOT EXISTS Systems (" +
                    "SystemName TEXT PRIMARY KEY)"
    ),
    CreateDistances(
            "CREATE TABLE IF NOT EXISTS Distances (" +
                    "System1 TEXT NOT NULL, " +
                    "System2 TEXT NOT NULL, " +
                    "Distance INT NOT NULL, " +
                    "PRIMARY KEY (System1, System2))"
    ),
    CreateStations(
            "CREATE TABLE IF NOT EXISTS Stations (" +
                    "StationName TEXT NOT NULL, " +
                    "SystemName TEXT NOT NULL, " +
                    "PadSize TEXT, " +
                    "StationType TEXT, " +
                    "PRIMARY KEY (StationName, SystemName), " +
                    "FOREIGN KEY (SystemName) REFERENCES Systems (SystemName))"
    ),
    CreateTrades(
            "CREATE TABLE IF NOT EXISTS Trades (" +
                    "StationName TEXT NOT NULL, " +
                    "SystemName TEXT NOT NULL, " +
                    "InaraID INT NOT NULL, " +
                    "Age TEXT DEFAULT '0', " +
                    "Supply INT DEFAULT 0, " +
                    "Demand INT DEFAULT 0, " +
                    "BuyPrice INT DEFAULT 0, " +
                    "SellPrice INT DEFAULT 0, " +
                    "FOREIGN KEY (StationName, SystemName) REFERENCES Stations (StationName, SystemName), " +
                    "FOREIGN KEY (InaraID) REFERENCES Commodities (InaraID), " +
                    "UNIQUE(StationName, SystemName, InaraID))"
    );

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
