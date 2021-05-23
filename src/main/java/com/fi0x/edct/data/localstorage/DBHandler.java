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
        String url = "jdbc:sqlite:" + Main.localStorage.getPath() + File.separator + "Commodities.db";

        try
        {
            dbConnection = DriverManager.getConnection(url);
            Out.newBuilder("Connection to local database established").verbose().print();

        } catch(SQLException ignored)
        {
            Out.newBuilder("Something went wrong when creating the local database").always().ERROR().print();
            System.exit(-1);
        }

        sendStatement(SQLSTATEMENTS.CreateCommodities.getStatement());
        sendStatement(SQLSTATEMENTS.CreateStations.getStatement());
    }
    public static DBHandler getInstance()
    {
        if(instance == null) instance = new DBHandler();
        return instance;
    }

    public void setCommodityData(String commodityName, int inaraID)
    {
        sendStatement("IF NOT EXISTS (SELECT * FROM Commodities WHERE InaraID = " + inaraID + ") " +
                "BEGIN INSERT INTO Commodities VALUES (" + commodityName + ", " + inaraID + ") END");
    }

    public void setStationData(int inaraID, String stationName, boolean isSelling, long downloadTime, long inaraUpdateTime, int price, int quantity, PADSIZE pad, STATIONTYPE type, String system, int starDistance)
    {
        sendStatement("INSERT INTO Stations VALUES ("
                + inaraID + ", "
                + stationName + ", "
                + isSelling + ", "
                + downloadTime + ", "
                + inaraUpdateTime + ", "
                + price + ", "
                + quantity + ", "
                + pad + ", "
                + type + ", "
                + system + ", "
                + starDistance + ") " +
                "ON DUPLICATE KEY UPDATE" +
                " DownloadTime = " + downloadTime +
                " InaraUpdateTime = " + inaraUpdateTime +
                " Price = " + price +
                " Quantity = " + quantity +
                " PadSize = " + pad +
                " StationType = " + type +
                " System = " + system +
                " StarDistance = " + starDistance);
    }

    private void sendStatement(String command)
    {
        try
        {
            Statement statement = dbConnection.createStatement();
            statement.executeUpdate(command);
        } catch(SQLException e)
        {
            Out.newBuilder("Could not execute a statement for the DB").debug().WARNING().print();
            e.printStackTrace();
        }
    }
}
