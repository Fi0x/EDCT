package com.fi0x.edct.controller;

import com.fi0x.edct.Main;
import com.fi0x.edct.data.localstorage.TradeReloader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

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
    private Label lblEDDNStatus;
    @FXML
    private Tooltip ttBackgroundStatus;

    @FXML
    private void calculate()
    {
        btnStart.setVisible(false);
        lblDataAge.setText("Loading data from storage");

        Main.reloader = new Thread(new TradeReloader(interactionController));
        Main.reloader.start();
    }

    public void setDataAge(long age, boolean newTradesAvailable)
    {
        btnStart.setVisible(newTradesAvailable);

        if(age == -1) return;

        String text = "Local data age: ";
        if(age < (60 * 1000)) text += age/1000 + "s";
        else if(age < (60 * 60 * 1000)) text += age/(60 * 1000) + "min";
        else if(age < (24 * 60 * 60 * 1000)) text += age/(60 * 60 * 1000) + "h";
        else text += age/(24 * 60 * 60 * 1000) + "d";

        lblDataAge.setText(text);
    }

    public void setUpdateStatus(String status, BACKGROUND_STATUS phase)
    {
        lblUpdateStatus.setText(status);
        switch(phase)
        {
            case INITIALIZING:
                ttBackgroundStatus.setText("How many commodities are updated / need updates");
                break;
            case INITIALIZED:
                ttBackgroundStatus.setText("This indicates if the tool requests updates from INARA or is up-to-date");
                break;
        }
    }
    public void setEDDNStatus(String status)
    {
        lblEDDNStatus.setText(status);
    }

    public void setInteractionController(Interaction controller)
    {
        interactionController = controller;
    }

    public enum BACKGROUND_STATUS
    {
        INITIALIZING,
        INITIALIZED
    }
}
