package com.fi0x.edct;

import com.fi0x.edct.controller.Interaction;
import com.fi0x.edct.controller.Results;
import com.fi0x.edct.dbconnection.UpdateThread;
import com.fi0x.edct.util.Out;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainWindow extends Application
{
    public static MainWindow instance;
    public Thread updaterThread;
    public static Thread downloadThread;

    public static File localStorage;
    public static File commodityList;

    public Interaction interactionController;
    public Results resultsController;

    @Override
    public void start(Stage primaryStage)
    {
        instance = this;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root;
        try
        {
            root = loader.load();
        } catch(IOException ignored)
        {
            Out.newBuilder("Could not load GUI. Shutting down").always().WARNING().print();
            return;
        }

        loadInteraction(loader);
        loadResults(loader);

        primaryStage.setTitle("Elite: Dangerous Carrier Trader");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        updaterThread = new Thread(new UpdateThread());
        updaterThread.start();
    }

    @Override
    public void stop() throws Exception
    {
        if(updaterThread != null) updaterThread.interrupt();
        if(downloadThread != null) downloadThread.interrupt();
        super.stop();
    }
    public static void main(String[] args)
    {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        if(arguments.contains("-v")) Out.v = true;
        if(arguments.contains("-vv")) Out.vv = true;
        if(arguments.contains("-d")) Out.d = true;

        setupLocalFiles();

        Out.newBuilder("Starting Program").verbose().print();
        launch(args);
    }

    public static MainWindow getInstance()
    {
        if(instance == null) instance = new MainWindow();
        return instance;
    }

    private static void setupLocalFiles()
    {
        Out.newBuilder("Setting up local storage").veryVerbose().print();
        localStorage = new File(System.getenv("APPDATA") + File.separator + "EDCT");

        if(!localStorage.exists())
        {
            if(localStorage.mkdir()) Out.newBuilder("Created local storage directory").SUCCESS().verbose().print();
            else
            {
                Out.newBuilder("Could not create local storage directory").origin("MainWindow").WARNING().debug().print();
                return;
            }
        }

        File commodityFolder1 = new File(localStorage.getPath() + File.separator + "CommoditySells");
        if(!commodityFolder1.exists())
        {
            if(commodityFolder1.mkdir()) Out.newBuilder("Created directory for commodity data").SUCCESS().verbose().print();
            else
            {
                Out.newBuilder("Could not create directory for commodity data").origin("MainWindow").WARNING().debug().print();
                return;
            }
        }
        File commodityFolder2 = new File(localStorage.getPath() + File.separator + "CommodityBuys");
        if(!commodityFolder2.exists())
        {
            if(commodityFolder2.mkdir()) Out.newBuilder("Created directory for commodity data").SUCCESS().verbose().print();
            else
            {
                Out.newBuilder("Could not create directory for commodity data").origin("MainWindow").WARNING().debug().print();
                return;
            }
        }

        commodityList = new File(localStorage.getPath() + File.separator + "CommodityList");
        if(!commodityList.exists())
        {
            try
            {
                if(commodityList.createNewFile())
                {
                    Out.newBuilder("Created commodityList-file").SUCCESS().verbose().print();
                    return;
                }
            } catch(IOException ignored)
            {
            }
            Out.newBuilder("Could not create commodityList-file").origin("MainWindow").WARNING().debug().print();
        }
    }

    private void loadInteraction(FXMLLoader parentLoader)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/interaction.fxml"));
        HBox interactionBox;

        try
        {
            interactionBox = loader.load();
            interactionController = loader.getController();
            interactionController.setMainController(parentLoader.getController());
        } catch(IOException ignored)
        {
            Out.newBuilder("Could not load interaction GUI elements").always().ERROR().print();
            return;
        }

        VBox mainBox = (VBox) parentLoader.getNamespace().get("vbMain");
        mainBox.getChildren().add(interactionBox);
    }
    private void loadResults(FXMLLoader parentLoader)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/results.fxml"));
        VBox interactionBox;

        try
        {
            interactionBox = loader.load();
            resultsController = loader.getController();
            resultsController.updateResultController(parentLoader.getController());
        } catch(IOException ignored)
        {
            Out.newBuilder("Could not load result GUI elements").always().ERROR().print();
            return;
        }

        VBox mainBox = (VBox) parentLoader.getNamespace().get("vbMain");
        mainBox.getChildren().add(interactionBox);
    }
}