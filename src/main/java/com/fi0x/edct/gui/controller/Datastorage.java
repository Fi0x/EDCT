package com.fi0x.edct.gui.controller;

import com.fi0x.edct.Main;
import com.fi0x.edct.logging.MixpanelHandler;
import com.fi0x.edct.logic.helper.NumberConverter;
import com.fi0x.edct.logic.threads.TradeReloader;
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
    public Label lblReloadStatus;
    @FXML
    private Tooltip ttBackgroundStatus;

    @FXML
    private void calculate()
    {
        btnStart.setVisible(false);
        lblReloadStatus.setVisible(true);
        lblDataAge.setText("Loading data from storage");

        MixpanelHandler.addMessage(MixpanelHandler.EVENT.BUTTON_CLICKED, MixpanelHandler.getButtonProperty("trade-reloader"));

        if(Main.reloader != null) Main.reloader.interrupt();
        Main.reloader = new Thread(new TradeReloader(interactionController));
        Main.reloader.start();
    }

    public void setDataAge(long age)
    {
        if(age == -1) return;
        lblDataAge.setText(NumberConverter.getAgeText(age));
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
    public void setEDDNStatus(boolean updating)
    {
        lblEDDNStatus.setText(updating ? "Storing EDDN information..." : "EDDN information stored");
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
