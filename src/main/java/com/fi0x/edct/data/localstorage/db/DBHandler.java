package com.fi0x.edct.data.localstorage.db;

import com.fi0x.edct.Main;
import com.fi0x.edct.controller.Settings;
import com.fi0x.edct.data.structures.PADSIZE;
import com.fi0x.edct.data.structures.STATIONTYPE;
import com.fi0x.edct.data.structures.STATION_OLD;
import com.fi0x.edct.data.structures.TRADE;
import com.fi0x.edct.util.Logger;

import javax.annotation.Nullable;
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
        String url = "jdbc:sqlite:" + Main.getDBURL();

        try
        {
            dbConnection = DriverManager.getConnection(url);
            Logger.INFO("Connected to local DB");

        } catch(SQLException e)
        {
            Logger.ERROR(998, "Something went wrong when connecting to the local DB", e);
            System.exit(998);
        }
    }
    private static DBHandler getInstance()
    {
        if(instance == null)
        {
            instance = new DBHandler();
            initialize();
        }
        return instance;
    }
    private static void initialize()
    {
        sendStatement(SQLSTATEMENTS.CreateCommodities.getStatement());
        sendStatement(SQLSTATEMENTS.CreateSystems.getStatement());
        sendStatement(SQLSTATEMENTS.CreateDistances.getStatement());
        sendStatement(SQLSTATEMENTS.CreateStations.getStatement());
        sendStatement(SQLSTATEMENTS.CreateTrades.getStatement());

        Logger.INFO("Finished setup of local DB");
    }

    public static void setCommodityData(String commodityName, int inaraID)
    {
        sendStatement("INSERT INTO Commodities " +
                "SELECT " + inaraID + ", " + makeSQLValid(commodityName) + ", " + 0 + ", " + 0 + " " +
                "WHERE NOT EXISTS (" +
                "SELECT * " +
                "FROM Commodities " +
                "WHERE InaraID = " + inaraID + " " +
                "AND CommodityName = " + makeSQLValid(commodityName) + ")");
    }

    public static void setGalacticAverage(String commodityName, int galacticAverage)
    {
        sendStatement("UPDATE Commodities " +
                "SET GalacticAverage = " + galacticAverage + " " +
                "WHERE CommodityName = " + makeSQLValid(commodityName));
    }

    public static void setTradeData(TRADE trade)
    {
        sendStatement("REPLACE INTO Stations VALUES (" +
                makeSQLValid(trade.STATION.NAME) + ", " +
                makeSQLValid(trade.STATION.SYSTEM) + ", " +
                makeSQLValid(trade.STATION.PAD.toString()) + ", " +
                makeSQLValid(trade.STATION.TYPE.toString()) + ")");

        //TODO: Ignore 0 values, only write positive numbers for prices and quantity
        sendStatement("REPLACE INTO Trades VALUES (" +
                makeSQLValid(trade.STATION.NAME) + ", " +
                makeSQLValid(trade.STATION.SYSTEM) + ", " +
                trade.INARA_ID + ", " +
                makeSQLValid(String.valueOf(trade.AGE)) + ", " +
                trade.SUPPLY + ", " +
                trade.DEMAND + ", " +
                trade.BUY_PRICE + ", " +
                trade.SELL_PRICE + ")");
    }

    public static void setSystemDistance(String system1, String system2, double distance)
    {
        sendStatement("REPLACE INTO Distances VALUES ("
                + makeSQLValid(system1) + ", "
                + makeSQLValid(system2) + ", "
                + distance + ")");
    }

    public static void updateDownloadTime(int inaraID)
    {
        sendStatement("UPDATE Commodities " +
                "SET LastUpdated = " + System.currentTimeMillis() / 1000 + " " +
                "WHERE InaraID = " + inaraID);
    }

    public static ArrayList<Integer> getCommodityIDs(boolean onlyMissing)
    {
        ArrayList<Integer> ids = new ArrayList<>();

        ResultSet results;
        if(onlyMissing)
        {
            results = getQueryResults("SELECT c.InaraID "
                    + "FROM Commodities c "
                    + "LEFT JOIN Trades t ON t.InaraID = c.InaraID "
                    + "WHERE t.InaraID IS NULL "
                    + "AND GalacticAverage > 0");
        } else
        {
            results = getQueryResults("SELECT InaraID "
                    + "FROM Commodities");
        }

        try
        {
            while(results != null && results.next())
            {
                ids.add(results.getInt("InaraID"));
            }
        } catch(Exception e)
        {
            Logger.WARNING("Could not get the INARA-ID of a commodity", e);
        }
        return ids;
    }

    public static String getCommodityNameByID(int commodityID)
    {
        ResultSet commodity = getQueryResults("SELECT CommodityName "
                + "FROM Commodities "
                + "WHERE InaraID = " + commodityID);
        try
        {
            if(commodity != null && commodity.next())
            {
                return commodity.getString("CommodityName");
            }
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the name of a commodity", e);
        }
        return "";
    }

    public static int getCommodityIDByName(String name)
    {
        ResultSet commodity = getQueryResults("SELECT InaraID "
                + "FROM Commodities "
                + "WHERE CommodityName = " + makeSQLValid(name));
        try
        {
            if(commodity != null && commodity.next())
            {
                return commodity.getInt("InaraID");
            }
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the id of a commodity", e);
        }
        return -1;
    }

    public static Map<String, Integer> getCommodityNameIDPairs()
    {
        Map<String, Integer> pairs = new HashMap<>();

        ResultSet results = getQueryResults("SELECT * " +
                "FROM Commodities");

        try
        {
            while(results != null && results.next())
            {
                pairs.put(results.getString("CommodityName"), results.getInt("InaraID"));
            }
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get a full list of all stored commodities");
        }

        return pairs;
    }

    public static long getCommodityAverage(String commodityName)
    {
        ResultSet results = getQueryResults("SELECT GalacticAverage " +
                "FROM Commodities " +
                "WHERE CommodityName = " + makeSQLValid(commodityName));

        try
        {
            if(results != null && results.next())
            {
                return results.getInt("GalacticAverage");
            }
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the average price for " + commodityName);
        }

        return 0;
    }

    public static int getOldestCommodityID()
    {
        ResultSet commodity = getQueryResults("SELECT c.* " +
                "FROM Commodities c " +
                "INNER JOIN (" +
                "SELECT InaraID, MIN(LastUpdated) min " +
                "FROM Commodities " +
                "GROUP BY InaraID) c2 " +
                "ON c2.InaraID = c.InaraID " +
                "ORDER BY LastUpdated, GalacticAverage DESC");

        int id = 0;

        try
        {
            if(commodity != null && commodity.next())
            {
                id = commodity.getInt("InaraID");
            }
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the oldest commodity ID", e);
        }

        return id;
    }

    public static int getOldestUpdateAge()
    {
        ResultSet commodity = getQueryResults("SELECT c.* " +
                "FROM Commodities c " +
                "INNER JOIN (" +
                "SELECT InaraID, MIN(LastUpdated) min " +
                "FROM Commodities " +
                "GROUP BY InaraID) c1 " +
                "ON c1.InaraID = c.InaraID " +
                "ORDER BY LastUpdated");

        int time = 0;

        try
        {
            if(commodity != null && commodity.next())
            {
                time = commodity.getInt("LastUpdated");
            }
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the age of the oldest commodity", e);
        }

        return time;
    }

    public static ArrayList<STATION_OLD> newAlternative(int commodityID, boolean isSelling)
    {
        //TODO: Update to use new db-structure
        ArrayList<STATION_OLD> stationList = new ArrayList<>();

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

                stationList.add(new STATION_OLD(system, name, pad, quantity, price, type, updateTime));
            }
        } catch(Exception e)
        {
            Logger.WARNING("Could not get the buy or sell prices for a commodity", e);
        }

        return stationList;
    }

    @Deprecated
    public static ArrayList<STATION_OLD> getCommodityInformation(int commodityID, boolean isSelling)
    {
        //TODO: Use method above instead
        ArrayList<STATION_OLD> stationList = new ArrayList<>();

        ResultSet stations = getQueryResults("SELECT * " +
                "FROM Trades " +
                "WHERE InaraID = " + commodityID + " " +
                "AND " + (isSelling ? "SellPrice" : "BuyPrice") + " > 0");
        if(stations == null) return stationList;

        try
        {
            while(stations.next())
            {
                String system = stations.getString("SystemName");
                String name = stations.getString("StationName");
                PADSIZE pad = PADSIZE.getFromString("L");//TODO: Get correct landing pad
                int quantity = Math.max(stations.getInt("Supply"), stations.getInt("Demand"));
                int price = Math.max(stations.getInt("BuyPrice"), stations.getInt("SellPrice"));
                STATIONTYPE type = STATIONTYPE.getFromString("ORBIT");//TODO: Get correct type
                long updateTime = Long.parseLong(stations.getString("Age"));

                stationList.add(new STATION_OLD(system, name, pad, quantity, price, type, updateTime));
            }
        } catch(Exception e)
        {
            Logger.WARNING("Could not get the buy or sell prices for a commodity", e);
        }

        return stationList;
    }

    public static double getSystemDistance(String system1, String system2)
    {
        ResultSet results = getQueryResults("SELECT Distance " +
                "FROM Distances " +
                "WHERE (System1 = '" + system1 + "' AND System2 = '" + system2 + "') " +
                "OR (System1 = '" + system2 + "' AND System2 = '" + system1 + "')");

        try
        {
            if(results != null && results.next())
            {
                return results.getInt("Distance");
            }
        } catch(SQLException e)
        {
            Logger.WARNING("Could not get the distance between two systems", e);
        }

        return 0;
    }

    public static void removeStationEntry(int commodityID, String stationName, String systemName, boolean isSeller)
    {
        //TODO: Check if buyer / seller is not swapped
        sendStatement("DELETE FROM Trades " +
                "WHERE InaraID = " + commodityID + " " +
                "AND StationName = " + makeSQLValid(stationName) + " " +
                "AND SystemName = " + makeSQLValid(systemName) + " " +
                "AND " + (isSeller ? "Supply" : "Demand") + " > 0");
    }

    public static void removeOldEntries()
    {
        long validTime = System.currentTimeMillis() - Settings.maxDataAge;
        sendStatement("DELETE FROM Trades " +
                "WHERE Age < " + validTime);
    }

    public static void removeTradeData()
    {
        sendStatement("DELETE FROM Trades");
    }

    private static void sendStatement(String command)
    {
        try
        {
            Statement statement = getInstance().dbConnection.createStatement();
            statement.executeUpdate(command);
        } catch(SQLException e)
        {
            Logger.WARNING("Something went wrong when sending an SQL statement", e);
        }
    }
    @Nullable
    private static ResultSet getQueryResults(String query)
    {
        try
        {
            Statement statement = getInstance().dbConnection.createStatement();
            return statement.executeQuery(query);
        } catch(SQLException e)
        {
            Logger.WARNING("Something went wrong when sending a SQL query", e);
        }
        return null;
    }
    private static String makeSQLValid(String s)
    {
        return "'" + s.replace("'", "''") + "'";
    }
}
