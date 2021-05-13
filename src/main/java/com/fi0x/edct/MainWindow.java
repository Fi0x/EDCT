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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainWindow extends Application
{
    public static MainWindow instance;

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

        Main.updaterThread.start();
    }
    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void stop() throws Exception
    {
        Main.stopProgram();
        super.stop();
    }

    public static MainWindow getInstance()
    {
        if(instance == null) instance = new MainWindow();
        return instance;
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