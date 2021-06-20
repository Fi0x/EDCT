package com.fi0x.edct.data.localstorage;

public enum SQLSTATEMENTS
{
    CreateCommodities(
            "CREATE TABLE IF NOT EXISTS commodities (" +
                    "commodity_name TEXT NOT NULL, " +
                    "inara_id INT PRIMARY KEY, " +
                    "last_update_time INT NOT NULL)"),
    CreateStations(
            "CREATE TABLE IF NOT EXISTS stations (" +
                    "commodity_id INT NOT NULL, " +
                    "station_name TEXT NOT NULL, " +
                    "star_system TEXT NOT NULL, " +
                    "is_seller INT NOT NULL, " +
                    "inara_time TEXT NOT NULL, " +
                    "price INT NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "pad_size TEXT NOT NULL, " +
                    "station_type TEXT NOT NULL, " +
                    "PRIMARY KEY (commodity_id, station_name, star_system, is_seller), " +
                    "FOREIGN KEY (commodity_id) REFERENCES commodities (inara_id))"),
    CreateDistances(
            "CREATE TABLE IF NOT EXISTS distances (" +
                    "star1 TEXT NOT NULL, " +
                    "star2 TEXT NOT NULL, " +
                    "distance INT NOT NULL, " +
                    "PRIMARY KEY (star1, star2))"),
    UpdateCommodities(
            "ALTER TABLE commodities " +
                    "ADD average_price INT DEFAULT 0"
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
