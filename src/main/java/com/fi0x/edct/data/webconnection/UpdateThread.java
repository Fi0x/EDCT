package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.Main;
import com.fi0x.edct.MainWindow;
import com.fi0x.edct.util.Out;
import javafx.application.Platform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpRetryException;
import java.util.*;

public class UpdateThread implements Runnable
{
    ArrayList<File> files;

    @Override
    public void run()
    {
        Out.newBuilder("Updater thread started").verbose().SUCCESS().print();
        while(true)
        {
            updateFiles();
            sortFileAge();

            if(files.size() > 0)
            {
                while(System.currentTimeMillis() - files.get(0).lastModified() < 1000 * 60 * 30)
                {
                    Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setDataAge(System.currentTimeMillis() - files.get(0).lastModified(), false));
                    wait((int) (Math.random() * 5000));
                }

                setCommodities(InaraCalls.getAllCommodities());
                try
                {
                    File file = files.get(0);
                    InaraCalls.getCommodityPrices(null, file.getName(), file.getParentFile().getName().equals("CommoditySells"), true);
                    files.remove(0);
                    if(files.size() > 0)
                    {
                        long age = System.currentTimeMillis() - files.get(0).lastModified();
                        Platform.runLater(() -> MainWindow.getInstance().interactionController.storageController.setDataAge(age, true));
                    }
                    Out.newBuilder("Updated oldest file").verbose().print();
                } catch(HttpRetryException ignored)
                {
                    Out.newBuilder("Could not update a commodity file").WARNING().debug().print();
                }
            }
            wait(500);
        }
    }

    private void updateFiles()
    {
        files = new ArrayList<>();

        File folder = new File(Main.localStorage + File.separator + "CommoditySells");
        if(folder.exists()) files.addAll(Arrays.asList(Objects.requireNonNull(folder.listFiles())));
        folder = new File(Main.localStorage + File.separator + "CommodityBuys");
        if(folder.exists()) files.addAll(Arrays.asList(Objects.requireNonNull(folder.listFiles())));
    }

    private void sortFileAge()
    {

        for(int i = 1; i < files.size(); i++)
        {
            int j = i - 1;
            while(files.get(j + 1).lastModified() < files.get(j).lastModified())
            {
                Collections.swap(files, j + 1, j);

                if(j == 0) break;
                j--;
            }
        }
    }

    private void setCommodities(Map<String, Map.Entry<String, Integer>> newCommodities)
    {
        if(newCommodities == null || newCommodities.size() == 0) return;

        try
        {
            FileWriter writer = new FileWriter(Main.commodityList.toString());

            for(Map.Entry<String, Map.Entry<String, Integer>> entry : newCommodities.entrySet())
            {
                String commodityEntry = entry.getKey() + "___" + entry.getValue().getKey() + "___" + entry.getValue().getValue();
                writer.write(commodityEntry + "\n");
            }

            writer.close();
            Out.newBuilder("Successfully wrote commodity-entries to file").verbose().SUCCESS().print();
        } catch(IOException e)
        {
            Out.newBuilder("Something went wrong when writing commodity data to local storage").debug().ERROR().print();
        }
    }

    private void wait(int millis)
    {
        try
        {
            Thread.sleep(millis);
        } catch(InterruptedException ignored)
        {
        }
    }
}
