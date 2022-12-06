package com.fi0x.edct.gui.controller;

import com.fi0x.edct.Main;
import com.fi0x.edct.gui.visual.CustomAlert;
import com.fi0x.edct.logging.exceptions.MixpanelEvents;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.helper.ConvertToString;
import com.fi0x.edct.logic.threads.TradeReloader;
import io.fi0x.javalogger.mixpanel.MixpanelHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Objects;

public class Datastorage
{
    private Interaction interactionController;

    private final Image loadingGif = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/loading.gif")), 15, 15, false, false);

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
        lblReloadStatus.setGraphic(new ImageView(loadingGif));
        lblReloadStatus.setText("Reloading trade data (0%)");
        lblDataAge.setText("Loading data from storage");

        MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "trade-reloader");}});

        if(Main.reloader != null)
            Main.reloader.interrupt();
        Main.reloader = new Thread(new TradeReloader(interactionController));
        Main.reloader.start();
    }
    @FXML
    private void dataAgeClicked()
    {
        StringBuilder info;
        info = new StringBuilder();
        info.append("Stored Systems\t\t").append(DBHandler.countSystemEntries());
        info.append("\nStored Distances\t\t").append(DBHandler.countDistanceEntries());
        info.append("\nStored Stations\t\t").append(DBHandler.countStationEntries());
        info.append("\nStored Commodities\t").append(DBHandler.countCommodityEntries());
        info.append("\nStored Export Prices\t").append(DBHandler.countExportStationEntries());
        info.append("\nStored Import Prices\t").append(DBHandler.countImportStationEntries());
        Alert alertDataStats = new CustomAlert(Alert.AlertType.INFORMATION, info.toString(), ButtonType.CLOSE);
        alertDataStats.setHeaderText("Information about your local Database");
        alertDataStats.showAndWait();
    }

    public void setDataAge(long age)
    {
        if(age == -1) return;
        lblDataAge.setText(ConvertToString.ageText(age));
    }
    public void setTradeReloaderProgress(float percentage)
    {
        lblReloadStatus.setText("Reloading trade data (" + (int) (percentage * 100) + "%)");
    }

    public void setUpdateStatus(String status, BACKGROUND_STATUS phase, boolean updateGif)
    {
        lblUpdateStatus.setText(status);
        if(updateGif)
            lblUpdateStatus.setGraphic(new ImageView(loadingGif));
        else
            lblUpdateStatus.setGraphic(null);

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
        if(updating)
        {
            lblEDDNStatus.setText("Storing EDDN information");
            lblEDDNStatus.setGraphic(new ImageView(loadingGif));
        }
        else
        {
            lblEDDNStatus.setText("EDDN information stored");
            lblEDDNStatus.setGraphic(null);
        }
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
