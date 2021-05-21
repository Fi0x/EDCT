package com.fi0x.edct.data.localstorage;

import com.fi0x.edct.Main;
import com.fi0x.edct.util.Out;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHandler
{
    private static Connection dbConnection;

    public static Connection getDbConnection()
    {
        if(dbConnection == null)
        {
            String url = "jdbc:sqlite:" + Main.localStorage.getPath() + "Commodities.db";

            try
            {
                dbConnection = DriverManager.getConnection(url);
                Out.newBuilder("Connection to local database established").verbose().print();
            } catch(SQLException ignored)
            {
                Out.newBuilder("Something went wrong when creating the local database").always().ERROR().print();
                System.exit(-1);
            }
        }

        return dbConnection;
    }
}
