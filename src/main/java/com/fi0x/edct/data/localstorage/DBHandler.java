package com.fi0x.edct.data.localstorage;

import com.fi0x.edct.Main;
import com.fi0x.edct.controller.Settings;
import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATION;
import com.fi0x.edct.data.structures.STATIONTYPE;
import com.fi0x.edct.util.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
            Logger.INFO("Connected to local DB");

        } catch(SQLException e)
        {
            Logger.ERROR(998, "Something went wrong when connecting to the local DB", e);
            System.exit(998);
        }

        sendStatement(SQLSTATEMENTS.CreateCommodities.getStatement());
        sendStatement(SQLSTATEMENTS.CreateStations.getStatement());
        sendStatement(SQLSTATEMENTS.CreateDistances.getStatement());

        Logger.INFO("Finished setup of local DB");
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

    public void setStationData(STATION station, int inaraID, boolean isSelling)
    {
        sendStatement("REPLACE INTO stations VALUES ("
                + inaraID + ", "
                + makeSQLValid(station.NAME) + ", "
                + makeSQLValid(station.SYSTEM) + ", "
                + isSelling + ", "
                + makeSQLValid(String.valueOf(station.UPDATE_TIME)) + ", "
                + station.PRICE + ", "
                + station.QUANTITY + ", "
                + makeSQLValid(station.PAD.toString()) + ", "
                + makeSQLValid(station.TYPE.toString()) + ")");
    }

    public void setSystemDistance(String system1, String system2, double distance)
    {
        sendStatement("REPLACE INTO distances VALUES ("
                + makeSQLValid(system1) + ", "
                + makeSQLValid(system2) + ", "
                + distance + ")");
    }

    public void updateDownloadTime(String commodityName, int inaraID)
    {
        sendStatement("REPLACE INTO commodities VALUES ("
                + makeSQLValid(commodityName) + ", "
                + inaraID + ", "
                + System.currentTimeMillis() / 1000 + ")");
    }

    public ArrayList<Integer> getCommodityIDs(boolean onlyMissing)
    {
        ArrayList<Integer> ids = new ArrayList<>();

        ResultSet results;
        if(onlyMissing)
        {
            results = getQueryResults("SELECT c.inara_id "
                    + "FROM commodities c "
                    + "LEFT JOIN stations s ON s.commodity_id = c.inara_id "
                    + "WHERE s.commodity_id IS NULL");
        } else
        {
            results = getQueryResults("SELECT c.inara_id "
                    + "FROM commodities c");
        }

        try
        {
            while(results != null && results.next())
            {
                ids.add(results.getInt("inara_id"));
            }
        } catch(Exception e)
        {
            Logger.WARNING("Could not get the INARA-ID of a commodity", e);
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
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the name of a commodity", e);
        }
        return "";
    }

    public int getCommodityIDByName(String name)
    {
        ResultSet commodity = getQueryResults("SELECT inara_id "
                + "FROM commodities "
                + "WHERE commodity_name = " + makeSQLValid(name));
        try
        {
            if(commodity != null && commodity.next())
            {
                return commodity.getInt("inara_id");
            }
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the id of a commodity", e);
        }
        return -1;
    }

    public Map<String, Integer> getCommodityNameIDPairs()
    {
        Map<String, Integer> pairs = new HashMap<>();

        ResultSet results = getQueryResults("SELECT * " +
                "FROM commodities");

        try
        {
            while(results != null && results.next())
            {
                pairs.put(results.getString("commodity_name"), results.getInt("inara_id"));
            }
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get a full list of all stored commodities");
        }

        return pairs;
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
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the oldest commodity ID", e);
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
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the age of the oldest commodity", e);
        }

        return time;
    }

    public ArrayList<STATION> getCommodityInformation(int commodityID, boolean isSelling)
    {
        ArrayList<STATION> stationList = new ArrayList<>();

        ResultSet stations = getQueryResults("SELECT * " +
                "FROM stations " +
                "WHERE commodity_id = " + commodityID + " " +
                "AND is_seller = " + (isSelling ? 0 : 1));
        if(stations == null) return stationList;

        try
        {
            while(stations.next())
            {
                String system = stations.getString("star_system");
                String name = stations.getString("station_name");
                PADSIZE pad = PADSIZE.getFromString(stations.getString("pad_size"));
                int quantity = stations.getInt("quantity");
                int price = stations.getInt("price");
                STATIONTYPE type = STATIONTYPE.getFromString(stations.getString("station_type"));
                long updateTime = Long.parseLong(stations.getString("inara_time"));

                stationList.add(new STATION(system, name, pad, quantity, price, type, updateTime));
            }
        } catch(Exception e)
        {
            Logger.WARNING("Could not get the buy or sell prices for a commodity", e);
        }

        return stationList;
    }

    public double getSystemDistance(String system1, String system2)
    {
        ResultSet results = getQueryResults("SELECT distance " +
                "FROM distances " +
                "WHERE (star1 = '" + system1 + "' AND star2 = '" + system2 + "') " +
                "OR (star1 = '" + system2 + "' AND star2 = '" + system1 + "')");

        try
        {
            if(results != null && results.next())
            {
                return results.getInt("distance");
            }
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the distance between two systems", e);
        }

        return 0;
    }

    public void removeStationEntry(int commodityID, String stationName, String systemName, boolean isSeller)
    {
        sendStatement("DELETE FROM stations " +
                "WHERE commodity_id = " + commodityID + " " +
                "AND station_name = " + makeSQLValid(stationName) + " " +
                "AND star_system = " + makeSQLValid(systemName) + " " +
                "AND is_seller = " + (isSeller ? 0 : 1));
    }

    public void removeOldEntries()
    {
        long validTime = System.currentTimeMillis() - Settings.maxDataAge;
        sendStatement("DELETE FROM stations " +
                "WHERE inara_time < " + validTime);
    }

    public void removeTradeData()
    {
        sendStatement("DELETE FROM stations");
    }

    private void sendStatement(String command)
    {
        try
        {
            Statement statement = dbConnection.createStatement();
            statement.executeUpdate(command);
        } catch(SQLException e)
        {
            Logger.WARNING("Something went wrong when sending an SQL statement", e);
        }
    }
    @Nullable
    private ResultSet getQueryResults(String query)
    {
        try
        {
            Statement statement = dbConnection.createStatement();
            return statement.executeQuery(query);
        } catch(SQLException e)
        {
            Logger.WARNING("Something went wrong when sending a SQL query", e);
        }
        return null;
    }
    private String makeSQLValid(String s)
    {
        return "'" + s.replace("'", "''") + "'";
    }
}
