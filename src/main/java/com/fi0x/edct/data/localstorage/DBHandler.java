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
            Out.newBuilder("Connection to local database established").verbose().SUCCESS();

        } catch(SQLException ignored)
        {
            Out.newBuilder("Something went wrong when creating the local database").always().ERROR();
            System.exit(-1);
        }

        sendStatement(SQLSTATEMENTS.CreateCommodities.getStatement());
        Out.newBuilder("Created Commodity Table").veryVerbose().INFO();

        sendStatement(SQLSTATEMENTS.CreateStations.getStatement());
        Out.newBuilder("Created Station Table").veryVerbose().INFO();
    }
    public static DBHandler getInstance()
    {
        if(instance == null) instance = new DBHandler();
        return instance;
    }

    public void setCommodityData(String commodityName, int inaraID)
    {
        sendStatement("INSERT INTO commodities " +
                "SELECT " + makeSQLValid(commodityName) + ", " + inaraID + ", " + 0 + " " +
                "WHERE NOT EXISTS (" +
                "SELECT * " +
                "FROM commodities " +
                "WHERE inara_id = " + inaraID + " " +
                "AND commodity_name = " + makeSQLValid(commodityName) + ")");
    }

    public void setStationData(STATION station, int inaraID, boolean isSelling, long downloadTime)
    {
        sendStatement("REPLACE INTO stations VALUES ("
                + inaraID + ", "
                + makeSQLValid(station.NAME) + ", "
                + makeSQLValid(station.SYSTEM) + ", "
                + isSelling + ", "
                + downloadTime + ", "
                + station.PRICE + ", "
                + station.QUANTITY + ", "
                + makeSQLValid(station.PAD.toString()) + ", "
                + makeSQLValid(station.TYPE.toString()) + ", "
                + station.STAR_DISTANCE + ")");
    }

    public void updateDownloadTime(String commodityName, int inaraID)
    {
        sendStatement("REPLACE INTO commodities VALUES ("
                + makeSQLValid(commodityName) + ", "
                + inaraID + ", "
                + System.currentTimeMillis() / 1000 + ")");
    }

    public ArrayList<Integer> getMissingCommodityIDs()
    {
        ArrayList<Integer> ids = new ArrayList<>();

        ResultSet results = getQueryResults("SELECT c.inara_id "
                + "FROM commodities c "
                + "LEFT JOIN stations s ON s.commodity_id = c.inara_id "
                + "WHERE s.commodity_id IS NULL");
        try
        {
            while(results != null && results.next())
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
                + "FROM commodities "
                + "WHERE inara_id = " + commodityID);
        try
        {
            if(commodity != null && commodity.next())
            {
                return commodity.getString("commodity_name");
            }
        } catch(SQLException ignored)
        {
            Out.newBuilder("Some error occurred when requesting the name of commodity " + commodityID).debug().WARNING();
        }
        return "";
    }

    public int getOldestCommodityID()
    {
        ResultSet commodity = getQueryResults("SELECT tbl.* " +
                "FROM commodities tbl " +
                "INNER JOIN (" +
                "SELECT inara_id, MIN(last_update_time) min " +
                "FROM commodities " +
                "GROUP BY inara_id) tbl1 " +
                "ON tbl1.inara_id = tbl.inara_id " +
                "ORDER BY last_update_time");

        int id = 0;

        try
        {
            if(commodity != null && commodity.next())
            {
                id = commodity.getInt("inara_id");
            }
        } catch(SQLException ignored)
        {
            Out.newBuilder("Some error occurred when requesting the oldest commodity id").debug().WARNING();
        }

        return id;
    }

    public int getOldestUpdateAge()
    {
        ResultSet commodity = getQueryResults("SELECT tbl.* " +
                "FROM commodities tbl " +
                "INNER JOIN (" +
                "SELECT inara_id, MIN(last_update_time) min " +
                "FROM commodities " +
                "GROUP BY inara_id) tbl1 " +
                "ON tbl1.inara_id = tbl.inara_id " +
                "ORDER BY last_update_time");

        int time = 0;

        try
        {
            if(commodity != null && commodity.next())
            {
                time = commodity.getInt("last_update_time");
            }
        } catch(SQLException ignored)
        {
            Out.newBuilder("Some error occurred when requesting the oldest commodity age").debug().WARNING();
        }

        return time;
    }

    private void sendStatement(String command)
    {
        try
        {
            Statement statement = dbConnection.createStatement();
            statement.executeUpdate(command);
            Out.newBuilder("Executed statement\n\t" + command).veryVerbose().INFO();
        } catch(SQLException ignored)
        {
            Out.newBuilder("Could not execute a statement for the DB\n\t" + command).debug().WARNING();
        }
    }
    @Nullable
    private ResultSet getQueryResults(String query)
    {
        try
        {
            Statement statement = dbConnection.createStatement();
            ResultSet results = statement.executeQuery(query);
            Out.newBuilder("Executed query\n\t" + query).veryVerbose().INFO();
            return results;
        } catch(SQLException ignored)
        {
            Out.newBuilder("Could not execute a query for the DB\n\t" + query).debug().WARNING();
        }
        return null;
    }
    private String makeSQLValid(String s)
    {
        return "'" + s.replace("'", "''") + "'";
    }
}
