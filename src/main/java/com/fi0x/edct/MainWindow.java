package com.fi0x.edct;

import com.fi0x.edct.controller.Interaction;
import com.fi0x.edct.controller.ProgramInfo;
import com.fi0x.edct.controller.Results;
import com.fi0x.edct.util.Out;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application
{
    public static MainWindow instance;

    public ProgramInfo infoController;
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
            Out.newBuilder("Could not load GUI. Shutting down").always().WARNING();
            return;
        }

        loadInfo(loader);
        loadInteraction(loader);
        loadResults(loader);

        primaryStage.setTitle("Elite: Dangerous Carrier Trader");
        primaryStage.getIcons().add(new Image("images/icon.png"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(540);
        primaryStage.setMinWidth(960);
        primaryStage.setResizable(false);
        primaryStage.show();

        Main.updater.start();
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

    private void loadInfo(FXMLLoader parentLoader)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/programinfo.fxml"));
        HBox infoBox;

        try
        {
            infoBox = loader.load();
            infoController = loader.getController();
        } catch(IOException ignored)
        {
            Out.newBuilder("Could not load info GUI elements").always().ERROR();
            return;
        }

        VBox mainBox = (VBox) parentLoader.getNamespace().get("vbMain");
        mainBox.getChildren().add(infoBox);

        infoController.checkForUpdates();
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
            Out.newBuilder("Could not load interaction GUI elements").always().ERROR();
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
            Out.newBuilder("Could not load result GUI elements").always().ERROR();
            return;
        }

        VBox mainBox = (VBox) parentLoader.getNamespace().get("vbMain");
        mainBox.getChildren().add(interactionBox);
    }
}