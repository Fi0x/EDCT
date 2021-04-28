package com.fi0x.edct.dbconnection;

import com.fi0x.edct.MainWindow;
import com.fi0x.edct.datastructures.ENDPOINTS;
import com.fi0x.edct.datastructures.STATION;
import com.fi0x.edct.util.Out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class InaraCalls
{
    public static Map<String, Map.Entry<String, Integer>> getAllCommodities()
    {
        Map<String, String> parameters = new HashMap<>();
        Map<String, Map.Entry<String, Integer>> commodities = new HashMap<>();

        try
        {
            String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Commodities.url, ENDPOINTS.Commodities.type, parameters);
            commodities = HTMLCleanup.getCommodityIDs(html);
            Out.newBuilder("Commodity list loaded from INARA").verbose().SUCCESS().print();
        } catch(Exception ignored)
        {
            Out.newBuilder("Could not get commodity-list").always().ERROR().print();
        }

        return commodities;
    }

    public static ArrayList<STATION> getCommodityPrices(RequestThread caller, String commodityRefID, boolean sell, boolean forceHTTP) throws HttpRetryException
    {
        Map<String, String> parameters = new HashMap<>();
        for(String param : ENDPOINTS.Prices.parameter)
        {
            switch(param)
            {
                case "act":
                    parameters.put(param, "goodsdata");
                    break;
                case "refname":
                    parameters.put(param, sell ? "sellmax" : "buymin");
                    break;
                case "refid":
                    parameters.put(param, commodityRefID);
                    break;
                case "refid2":
                    parameters.put(param, "1261");
                    break;
                default:
                    break;
            }
        }

        ArrayList<STATION> stationList = new ArrayList<>();
        try
        {
            String folder = sell ? "CommoditySells" : "CommodityBuys";
            File commodityFile = new File(MainWindow.localStorage.getPath() + File.separator + folder + File.separator + commodityRefID);

            if(commodityFile.exists())
            {
                long ageInMillis = System.currentTimeMillis() - commodityFile.lastModified();
                if(caller != null && ageInMillis > caller.oldestFileAge) caller.oldestFileAge = ageInMillis;
            }

            if(forceHTTP || !commodityFile.exists())
            {
                String html = RequestHandler.sendHTTPRequest(ENDPOINTS.Prices.url, ENDPOINTS.Prices.type, parameters);
                stationList = HTMLCleanup.getCommodityPrices(html);

                if(!commodityFile.exists())
                {
                    try
                    {
                        if(commodityFile.createNewFile())
                        {
                            writeCommodityDataToFile(commodityFile, stationList);
                            Out.newBuilder("Created commodity-file: " + commodityRefID).SUCCESS().verbose().print();
                            return stationList;
                        }
                    } catch(IOException ignored)
                    {
                    }
                    Out.newBuilder("Could not create commodity-file " + commodityRefID).origin("InaraCalls").WARNING().debug().print();
                } else writeCommodityDataToFile(commodityFile, stationList);
            } else stationList = readCommodityDataFromFile(commodityFile);

        } catch(HttpRetryException e)
        {
            throw e;
        } catch(IOException ignored)
        {
            Out.newBuilder("Could not get commodity-prices for " + commodityRefID).always().ERROR().print();
        }
        return stationList;
    }

    private static void writeCommodityDataToFile(File file, ArrayList<STATION> stations)
    {
        try
        {
            FileWriter writer = new FileWriter(file.toString());

            for(STATION station : stations)
            {
                String stationEntry = station.SYSTEM + "___" + station.NAME + "___" + station.PAD + "___" + station.QUANTITY + "___" + station.PRICE + "___" + station.TYPE + "___" + station.STAR_DISTANCE;
                writer.write(stationEntry + "\n");
            }

            writer.close();
            Out.newBuilder("Successfully wrote commodity to file").veryVerbose().SUCCESS().print();
        } catch(IOException e)
        {
            Out.newBuilder("Something went wrong when writing commodity data to local storage").origin("InaraCalls").debug().ERROR().print();
        }
    }

    private static ArrayList<STATION> readCommodityDataFromFile(File file)
    {
        ArrayList<STATION> stations = new ArrayList<>();

        try
        {
            Scanner fileReader = new Scanner(file);
            while(fileReader.hasNextLine())
            {
                String line = fileReader.nextLine();
                String[] parts = line.split("___");
                if(parts.length != 7) continue;

                STATION newStation = STATION.getStationFromParts(parts);
                stations.add(newStation);
            }
            Out.newBuilder("Commodity data loaded from local file").veryVerbose().SUCCESS().print();
        } catch(FileNotFoundException ignored)
        {
            Out.newBuilder("Could not load commodity data from local file").debug().ERROR().print();
        }

        return stations;
    }
}