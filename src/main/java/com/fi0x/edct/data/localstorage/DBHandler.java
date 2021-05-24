package com.fi0x.edct.data.localstorage;

import com.fi0x.edct.Main;
import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATIONTYPE;
import com.fi0x.edct.util.Out;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHandler
{
    private static DBHandler instance;
    private Connection dbConnection;

    private DBHandler()
    {
        String url = "jdbc:sqlite:" + Main.localStorage.getPath() + File.separator + "Trades.db";

        try
        {
            dbConnection = DriverManager.getConnection(url);
            Out.newBuilder("Connection to local database established").verbose().SUCCESS().print();

        } catch(SQLException ignored)
        {
            Out.newBuilder("Something went wrong when creating the local database").always().ERROR().print();
            System.exit(-1);
        }

        sendStatement(SQLSTATEMENTS.CreateCommodities.getStatement());
        Out.newBuilder("Created Commodity Table").veryVerbose().print();

        sendStatement(SQLSTATEMENTS.CreateStations.getStatement());
        Out.newBuilder("Created Station Table").veryVerbose().print();
    }
    public static DBHandler getInstance()
    {
        if(instance == null) instance = new DBHandler();
        return instance;
    }

    public void setCommodityData(String commodityName, int inaraID)
    {
        sendStatement("INSERT INTO commodities " +
                "SELECT '" + commodityName + "', " + inaraID + ", " + 0 + " " +
                "WHERE NOT EXISTS (" +
                "SELECT * " +
                "FROM commodities " +
                "WHERE inara_id = " + inaraID + " " +
                "AND commodity_name = '" + commodityName + "')");
    }

    public void setStationData(int inaraID, String stationName, boolean isSelling, long downloadTime, int price, int quantity, PADSIZE pad, STATIONTYPE type, String system, int starDistance)
    {
        sendStatement("REPLACE INTO stations VALUES ("
                + inaraID + ", '"
                + stationName + "', "
                + system + ", "
                + isSelling + ", "
                + downloadTime + ", "
                + price + ", "
                + quantity + ", "
                + pad + ", "
                + type + ", "
                + starDistance + ")");
    }

    public void updateDownloadTime(String commodityName, int inaraID)
    {
        sendStatement("REPLACE INTO commodities VALUES ("
                + commodityName + ", "
                + inaraID + ", "
                + System.currentTimeMillis() / 1000 + ")");
    }

    private void sendStatement(String command)
    {
        try
        {
            Statement statement = dbConnection.createStatement();
            statement.executeUpdate(command);
            Out.newBuilder("Executed statement").veryVerbose().print();
        } catch(SQLException e)
        {
            Out.newBuilder("Could not execute a statement for the DB").debug().WARNING().print();
            e.printStackTrace();
        }
    }
}
