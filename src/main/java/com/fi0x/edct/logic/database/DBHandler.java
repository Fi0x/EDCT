package com.fi0x.edct.logic.database;

import com.fi0x.edct.Main;
import com.fi0x.edct.gui.controller.Settings;
import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logic.structures.PADSIZE;
import com.fi0x.edct.logic.structures.STATION;
import com.fi0x.edct.logic.structures.STATIONTYPE;
import com.fi0x.edct.logic.structures.TRADE;
import com.sun.javafx.geom.Vec3d;
import io.fi0x.javalogger.logging.Logger;

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
        String url = "jdbc:sqlite:" + Main.getDBURL();

        try
        {
            dbConnection = DriverManager.getConnection(url);
            Logger.log("Connected to local DB", LogName.INFO);

        } catch(SQLException e)
        {
            Logger.log("Something went wrong when connecting to the local DB", LogName.getError(998), e, 998);
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

        Logger.log("Finished setup of local DB", LogName.INFO);
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

    public static void setStationData(STATION station)
    {
        sendStatement("REPLACE INTO Stations VALUES (" +
                makeSQLValid(station.NAME) + ", " +
                makeSQLValid(station.SYSTEM) + ", " +
                makeSQLValid(station.PAD.toString()) + ", " +
                makeSQLValid(station.TYPE.toString()) + ", " +
                station.DISTANCE_TO_STAR + ")");
    }

    public static void setTradeData(TRADE trade)
    {
        sendStatement("INSERT INTO Trades VALUES (" +
                makeSQLValid(trade.STATION.NAME) + ", " +
                makeSQLValid(trade.STATION.SYSTEM) + ", " +
                trade.INARA_ID + ", " +
                makeSQLValid(String.valueOf(trade.AGE)) + ", " +
                trade.SUPPLY + ", " +
                trade.DEMAND + ", " +
                trade.BUY_PRICE + ", " +
                trade.SELL_PRICE + ") " +
                "ON CONFLICT (StationName, SystemName, InaraID) " +
                "DO UPDATE SET " +
                "Age = " + makeSQLValid(String.valueOf(trade.AGE)) + ", " +
                (trade.SELL_PRICE > 0
                        ? "Supply = " + trade.SUPPLY + ", SellPrice = " + trade.SELL_PRICE
                        : "Demand = " + trade.DEMAND + ", BuyPrice = " + trade.BUY_PRICE));
    }

    public static void setSystemCoordinates(String systemName, Vec3d coords)
    {
        sendStatement("INSERT INTO Systems VALUES (" +
                makeSQLValid(systemName) + ", " +
                coords.x + ", " +
                coords.y + ", " +
                coords.z + ") " +
                "ON CONFLICT (SystemName) " +
                "DO UPDATE SET " +
                "CoordsX = " + coords.x + ", CoordsY = " + coords.y + ", CoordsZ = " + coords.z);
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

    public static ArrayList<Integer> getCommodityIDs(boolean onlyMissing, long minAverage)
    {
        ArrayList<Integer> ids = new ArrayList<>();

        ResultSet results;
        if(onlyMissing)
        {
            results = getQueryResults("SELECT c.InaraID "
                    + "FROM Commodities c "
                    + "LEFT JOIN Trades t ON t.InaraID = c.InaraID "
                    + "WHERE t.InaraID IS NULL "
                    + "AND GalacticAverage > " + minAverage);
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
            Logger.log("Could not get the INARA-ID of a commodity", LogName.WARNING, e);
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
            Logger.log("Could not get the name of a commodity", LogName.WARNING, e);
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
            Logger.log("Could not get the id of a commodity", LogName.WARNING, e);
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
            Logger.log("Could not get a full list of all stored commodities", LogName.WARNING, e);
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
            Logger.log("Could not get the average price for " + commodityName, LogName.WARNING, e);
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
            Logger.log("Could not get the oldest commodity ID", LogName.WARNING, e);
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
            Logger.log("Could not get the age of the oldest commodity", LogName.WARNING, e);
        }

        return time;
    }

    public static ArrayList<TRADE> getTradeInformation(int commodityID, boolean isSelling)
    {
        ArrayList<TRADE> stationList = new ArrayList<>();

        ResultSet trades = getQueryResults("SELECT t.*, s.* " +
                "FROM Trades t " +
                "INNER JOIN Stations s ON s.SystemName = t.SystemName AND s.StationName = t.StationName " +
                "WHERE InaraID = " + commodityID + " " +
                "AND " + (isSelling ? "SellPrice" : "BuyPrice") + " > 0 " +
                "ORDER BY " + (isSelling ? "SellPrice" : "BuyPrice DESC"));
        if(trades == null) return stationList;

        try
        {
            while(trades.next())
            {
                String systemName = trades.getString("SystemName");
                String stationName = trades.getString("StationName");
                PADSIZE pad = PADSIZE.getFromString(trades.getString("PadSize"));
                STATIONTYPE type = STATIONTYPE.getFromString(trades.getString("StationType"));
                double starDistance = trades.getDouble("StarDistance");

                STATION station = new STATION(systemName, stationName, pad, type, starDistance);

                int supply = trades.getInt("Supply");
                int demand = trades.getInt("Demand");
                int buyPrice = trades.getInt("BuyPrice");
                int sellPrice = trades.getInt("SellPrice");
                long updateTime = Long.parseLong(trades.getString("Age"));

                stationList.add(new TRADE(station, commodityID, updateTime, supply, demand, buyPrice, sellPrice));
            }
        } catch(Exception e)
        {
            Logger.log("Could not get the buy or sell prices for a commodity", LogName.WARNING, e);
        }

        return stationList;
    }

    public static STATION getStation(String systemName, String stationName)
    {
        ResultSet stations = getQueryResults("SELECT * " +
                "FROM Stations " +
                "WHERE SystemName = " + makeSQLValid(systemName) + " " +
                "AND StationName = " + makeSQLValid(stationName));

        STATION station = null;
        try
        {
            if(stations != null && stations.next())
            {
                PADSIZE pad = PADSIZE.getFromString(stations.getString("PadSize"));
                STATIONTYPE type = STATIONTYPE.getFromString(stations.getString("StationType"));
                double starDistance = stations.getDouble("StarDistance");

                station = new STATION(systemName, stationName, pad, type, starDistance);
            }
        } catch(SQLException e)
        {
            Logger.log("Could not get some station information", LogName.WARNING, e);
        }

        return station;
    }

    public static Vec3d getSystemCoords(String systemName)
    {
        ResultSet system = getQueryResults("SELECT * " +
                "FROM Systems " +
                "WHERE SystemName = " + makeSQLValid(systemName));

        Vec3d coordinates = null;

        try
        {
            if(system != null && system.next())
            {
                double x = Double.parseDouble(system.getString("CoordsX"));
                double y = Double.parseDouble(system.getString("CoordsY"));
                double z = Double.parseDouble(system.getString("CoordsZ"));

                coordinates = new Vec3d(x, y, z);
            }
        } catch(SQLException e)
        {
            Logger.log("Could not get the age of the oldest commodity", LogName.WARNING, e);
        }

        return coordinates;
    }

    public static double getSystemDistance(String system1, String system2)
    {
        ResultSet results = getQueryResults("SELECT Distance " +
                "FROM Distances " +
                "WHERE (System1 = " + makeSQLValid(system1) + " AND System2 = " + makeSQLValid(system2) + ") " +
                "OR (System1 = " + makeSQLValid(system2) + " AND System2 = " + makeSQLValid(system1) + ")");

        try
        {
            if(results != null && results.next())
            {
                return results.getInt("Distance");
            }
        } catch(SQLException e)
        {
            Logger.log("Could not get the distance between two systems", LogName.WARNING, e);
        }

        return 0;
    }

    public static void removeStationEntry(int commodityID, String stationName, String systemName, boolean isSeller)
    {
        sendStatement("DELETE FROM Trades " +
                "WHERE InaraID = " + commodityID + " " +
                "AND StationName = " + makeSQLValid(stationName) + " " +
                "AND SystemName = " + makeSQLValid(systemName) + " " +
                "AND " + (isSeller ? "SellPrice" : "BuyPrice") + " > 0");
    }

    public static void removeOldEntries()
    {
        long validTime = System.currentTimeMillis() - Settings.maxDataAge;
        sendStatement("DELETE FROM Trades " +
                "WHERE Age < " + validTime);
    }

    public static int countStationEntries()
    {
        ResultSet results = getQueryResults("SELECT COUNT(StarDistance) " +
                "FROM Stations");

        try
        {
            if(results != null && results.next())
                return results.getInt(1);
        } catch(SQLException e)
        {
            Logger.log("Could not count the rows of the Stations table", LogName.WARNING, e);
        }

        return -1;
    }
    public static int countTradeEntries()
    {
        ResultSet results = getQueryResults("SELECT COUNT(InaraID) " +
                "FROM Trades");

        try
        {
            if(results != null && results.next())
                return results.getInt(1);
        } catch(SQLException e)
        {
            Logger.log("Could not count the rows of the Trades table", LogName.WARNING, e);
        }

        return -1;
    }

    public static void removeTradeData()
    {
        sendStatement("DELETE FROM Trades");
    }
    public static void removeStationDATA()
    {
        sendStatement("DELETE FROM Stations");
    }

    private static synchronized void sendStatement(String command)
    {
        try
        {
            dbLockCheck();
            Statement statement = getInstance().dbConnection.createStatement();
            statement.executeUpdate(command);
        } catch(SQLException e)
        {
            if(e.toString().contains("The database file is locked"))
                Logger.log("Could not access database file because of locking", LogName.getError(993), e, 993);
            else
                Logger.log("Something went wrong when sending an SQL statement. Statement: " + command, LogName.WARNING, e, 994);
        }
    }
    private static synchronized ResultSet getQueryResults(String query)
    {
        try
        {
            dbLockCheck();
            Statement statement = getInstance().dbConnection.createStatement();
            return statement.executeQuery(query);
        } catch(SQLException e)
        {
            if(e.toString().contains("The database file is locked"))
                Logger.log("Could not access database file because of locking", LogName.getError(993), e, 993);
            else
                Logger.log("Something went wrong when sending a SQL query. Query: " + query, LogName.WARNING, e, 994);
        }
        return null;
    }
    private static void dbLockCheck()
    {
        if(!(new File(Main.getDBURL()).canWrite()))
        {
            try
            {
                Thread.sleep(250);
            } catch(InterruptedException e)
            {
                Thread.currentThread().stop();
            }
        }
    }
    private static String makeSQLValid(String s)
    {
        return "'" + s.replace("'", "''") + "'";
    }
}
