package com.fi0x.edct.controller;

import com.fi0x.edct.MainWindow;
import com.fi0x.edct.dbconnection.RequestThread;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Datastorage
{
    private Interaction interactionController;

    @FXML
    public Button btnStart;
    @FXML
    private Label lblDataAge;
    @FXML
    public Button btnUpdateLocalFiles;

    @FXML
    private void calculate()
    {
        btnStart.setVisible(false);
        btnUpdateLocalFiles.setVisible(false);
        lblDataAge.setText("Loading data from storage");

        MainWindow.downloadThread = new Thread(new RequestThread(interactionController, 1, false));
        MainWindow.downloadThread.start();
    }
    @FXML
    private void updateStorage()
    {
        btnStart.setVisible(false);
        btnUpdateLocalFiles.setVisible(false);
        lblDataAge.setText("Downloading data from INARA");

        Thread threadReq = new Thread(new RequestThread(interactionController, 1, true));
        threadReq.start();
    }

    public void setDataAge(long age)
    {
        String text = "Local data age: ";
        if(age < (60 * 1000)) text += age/1000 + "s";
        else if(age < (60 * 60 * 1000)) text += age/(60 * 1000) + "min";
        else if(age < (24 * 60 * 60 * 1000)) text += age/(60 * 60 * 1000) + "h";
        else text += age/(24 * 60 * 60 * 1000) + "d";
        lblDataAge.setText(text);
    }

    public void setInteractionController(Interaction controller)
    {
        interactionController = controller;
    }
}
