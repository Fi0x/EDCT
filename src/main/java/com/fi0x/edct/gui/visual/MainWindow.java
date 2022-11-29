package com.fi0x.edct.gui.visual;

import com.fi0x.edct.Main;
import com.fi0x.edct.gui.controller.Interaction;
import com.fi0x.edct.gui.controller.ProgramInfo;
import com.fi0x.edct.gui.controller.Results;
import com.fi0x.edct.logging.LogName;
import io.fi0x.javalogger.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application
{
    private static MainWindow instance;
    private ProgressBar progressbar;
    public Stage primaryStage;

    public ProgramInfo infoController;
    public Interaction interactionController;
    public Results resultsController;

    @Override
    public void start(Stage stage)
    {
        instance = this;
        progressbar = new ProgressBar();
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root;
        try
        {
            root = loader.load();
        } catch(IOException e)
        {
            Logger.log("Could not load MainWindow controller", LogName.ERROR, e, 999);
            System.exit(999);
            return;
        }

        loadInfo(loader);
        loadInteraction(loader);
        loadResults(loader);

        primaryStage.setTitle("Elite: Dangerous Carrier Trader");
        primaryStage.getIcons().add(new Image("images/icon.png"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        progressbar.setStage(primaryStage);

        Logger.log("GUI loaded", LogName.VERBOSE);

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

    public void setUpdateStatus(double status)
    {
        if(status < 0) progressbar.setProgressType(ProgressBar.Type.NO_PROGRESS);
        else progressbar.showCustomProgress(status, ProgressBar.Type.NORMAL);
    }

    private void loadInfo(FXMLLoader parentLoader)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/programinfo.fxml"));
        Pane infoBox;

        try
        {
            infoBox = loader.load();
            infoController = loader.getController();
        } catch(IOException e)
        {
            Logger.log("Could not load ProgramInfo controller", LogName.ERROR, e, 999);
            return;
        }

        Pane mainBox = (Pane) parentLoader.getNamespace().get("vbMain");
        mainBox.getChildren().add(infoBox);

        infoController.checkForUpdates();
    }

    private void loadInteraction(FXMLLoader parentLoader)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/interaction.fxml"));
        Pane interactionBox;

        try
        {
            interactionBox = loader.load();
            interactionController = loader.getController();
            interactionController.setMainController(parentLoader.getController());
        } catch(IOException e)
        {
            Logger.log("Could not load Interaction controller", LogName.ERROR, e, 999);
            System.exit(999);
            return;
        }

        Pane mainBox = (Pane) parentLoader.getNamespace().get("vbMain");
        mainBox.getChildren().add(interactionBox);
    }
    private void loadResults(FXMLLoader parentLoader)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/results.fxml"));
        Pane interactionBox;

        try
        {
            interactionBox = loader.load();
            resultsController = loader.getController();
            resultsController.updateResultController(parentLoader.getController());
        } catch(IOException e)
        {
            Logger.log("Could not load Results controller", LogName.ERROR, e, 999);
            System.exit(999);
            return;
        }

        Pane mainBox = (Pane) parentLoader.getNamespace().get("vbMain");
        mainBox.getChildren().add(interactionBox);
    }
}