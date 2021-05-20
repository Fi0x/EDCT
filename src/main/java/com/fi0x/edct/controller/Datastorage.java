package com.fi0x.edct.controller;

import com.fi0x.edct.Main;
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
    private Label lblUpdateStatus;

    @FXML
    private void calculate()
    {
        btnStart.setVisible(false);
        lblDataAge.setText("Loading data from storage");

        Main.downloadThread = new Thread(new RequestThread(interactionController, false));
        Main.downloadThread.start();
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
    public void setDataAge(long age, boolean isUpdating)
    {
        lblUpdateStatus.setText(isUpdating ? "Updating local files..." : "Local files are updated");
        setDataAge(age);
    }

    public void setInteractionController(Interaction controller)
    {
        interactionController = controller;
    }
}
