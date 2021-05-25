package com.fi0x.edct.data.localstorage;

import com.fi0x.edct.Main;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.util.Out;

import javax.annotation.Nullable;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

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

    public void setStationData(STATION station, int inaraID, boolean isSelling, long downloadTime)
    {
        sendStatement("REPLACE INTO stations VALUES ("
                + inaraID + ", '"
                + station.NAME + "', "
                + station.SYSTEM + ", "
                + isSelling + ", "
                + downloadTime + ", "
                + station.PRICE + ", "
                + station.QUANTITY + ", "
                + station.PAD + ", "
                + station.TYPE + ", "
                + station.STAR_DISTANCE + ")");
    }

    public void updateDownloadTime(String commodityName, int inaraID)
    {
        sendStatement("REPLACE INTO commodities VALUES ("
                + commodityName + ", "
                + inaraID + ", "
                + System.currentTimeMillis() / 1000 + ")");
    }

    public ArrayList<Integer> getMissingCommodityIDs()
    {
        ArrayList<Integer> ids = new ArrayList<>();

        ResultSet results = getQueryResults("SELECT c.inara_id"
                + "FROM commodities c "
                + "LEFT JOIN stations s ON s.inara_id = c.inara_id"
                + "WHERE s.inara_id IS NULL");//TODO: Check if this works
        try
        {
            while(results.next())
            {
                ids.add(results.getInt("inara_id"));
            }
        } catch(Exception ignored)
        {
        }
        return ids;
    }

    public String getCommodityNameByID(int commodityID)
    {
        ResultSet commodity = getQueryResults("SELECT commodity_name "
                + "FROM commodities"
                + "WHERE inara_id = '" + commodityID + "'");
        try
        {
            if(commodity != null && commodity.next())
            {
                return commodity.getString("commodity_name");
            }
        } catch(SQLException ignored)
        {
            Out.newBuilder("Some error occured when requesting the name of commodity " + commodityID).debug().WARNING().print();
        }
        return "";
    }

    private void sendStatement(String command)
    {
        try
        {
            Statement statement = dbConnection.createStatement();
            statement.executeUpdate(command);
            Out.newBuilder("Executed statement\n\t" + command).veryVerbose().print();
        } catch(SQLException ignored)
        {
            Out.newBuilder("Could not execute a statement for the DB\n\t" + command).debug().WARNING().print();
        }
    }
    @Nullable
    private ResultSet getQueryResults(String query)
    {
        try
        {
            Statement statement = dbConnection.createStatement();
            ResultSet results = statement.executeQuery(query);
            Out.newBuilder("Executed query\n\t" + query).veryVerbose().print();
            return results;
        } catch(SQLException ignored)
        {
            Out.newBuilder("Could not execute a query for the DB\n\t" + query).debug().WARNING().print();
        }
        return null;
    }
}
