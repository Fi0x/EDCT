package com.fi0x.edct.gui.controller;

import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logging.exceptions.HtmlConnectionException;
import com.fi0x.edct.logging.exceptions.MixpanelEvents;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.filesystem.BlacklistHandler;
import com.fi0x.edct.logic.helper.ConvertToString;
import com.fi0x.edct.logic.helper.ExternalProgram;
import com.fi0x.edct.logic.structures.ENDPOINTS;
import com.fi0x.edct.logic.structures.TRADE;
import com.fi0x.edct.logic.websites.InaraStation;
import io.fi0x.javalogger.logging.Logger;
import io.fi0x.javalogger.mixpanel.MixpanelHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class Station implements Initializable
{
    private Results resultsController;

    private boolean isImportStation;
    public int stationID;
    private String inaraID;
    private String stationSystem;
    private String stationName;

    @FXML
    private Label lblAction;
    @FXML
    private Button btnReloadStation;
    @FXML
    private Button btnBlacklist;
    @FXML
    private Label lblSystem;
    @FXML
    private Label lblStationName;
    @FXML
    private Label lblType;
    @FXML
    private Label lblTypeIcon;
    @FXML
    private Label lblPad;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblAmount;
    @FXML
    private Label lblStarDistance;
    @FXML
    private Label lblAge;
    @FXML
    private Button btnDiscord;
    @FXML
    private Tooltip ttDiscord;
    @FXML
    private Button btnReddit;
    @FXML
    private Tooltip ttReddit;
    @FXML
    private Button btnPrevStation;
    @FXML
    private Button btnNextStation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/blacklist.png")), 20, 20, false, false);
        btnBlacklist.setGraphic(new ImageView(img));
        img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/reload.png")), 20, 20, false, false);
        btnReloadStation.setGraphic(new ImageView(img));
        img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/station_orbit.png")), 20, 20, false, false);
        lblTypeIcon.setGraphic(new ImageView(img));

        btnReddit.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
        {
            MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "copy-reddit-string");}});

            TRADE station = isImportStation ? resultsController.getCurrentImportStation() : resultsController.getCurrentExportStation();
            String text;
            if(e.getButton() == MouseButton.SECONDARY) text = ConvertToString.redditContent(resultsController, station, isImportStation);
            else text = ConvertToString.redditTitle(resultsController, station, isImportStation);

            if(text == null)
            {
                Logger.log("Something went wrong when creating a reddit String", LogName.getError(992), null, 992);
                return;
            }

            ExternalProgram.copyToClipboard(text);
        });
        btnDiscord.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
        {
            MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "copy-discord-string");}});

            TRADE station = isImportStation ? resultsController.getCurrentImportStation() : resultsController.getCurrentExportStation();
            String text = ConvertToString.discordText(resultsController, station, isImportStation);

            if(text == null)
            {
                Logger.log("Something went wrong when creating a discord String", LogName.getError(992), null, 992);
                return;
            }

            ExternalProgram.copyToClipboard(text);
        });

        ttReddit.setText("Left click here to copy a title for your reddit post.\nRight click here to copy a text for your reddit post");
        ttDiscord.setText("Click here to copy a text that you can publish on discord");
    }

    @FXML
    private void nextStation()
    {
        stationID++;
        if(!isImportStation && stationID >= resultsController.getCurrentTrade().EXPORT_PRICES.size())
        {
            stationID = resultsController.getCurrentTrade().EXPORT_PRICES.size() - 1;
        } else if(isImportStation && stationID >= resultsController.getCurrentTrade().IMPORT_PRICES.size())
        {
            stationID = resultsController.getCurrentTrade().IMPORT_PRICES.size() - 1;
        }

        if(!isImportStation) resultsController.currentExportStation = stationID;
        else resultsController.currentImportStation = stationID;
        resultsController.displayResults();
    }
    @FXML
    private void previousStation()
    {
        stationID--;
        if(stationID < 0) stationID = 0;

        if(!isImportStation) resultsController.currentExportStation = stationID;
        else resultsController.currentImportStation = stationID;
        resultsController.displayResults();
    }
    @FXML
    private void reloadStation()
    {
        MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "reload-station-data");}});

        TRADE currentTrade = isImportStation ? resultsController.getCurrentImportStation() : resultsController.getCurrentExportStation();
        InaraStation.updateSingleStationTrades(stationName, stationSystem, currentTrade);
        resultsController.displayResults();
    }
    @FXML
    private void addToBlacklist()
    {
        MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "add-station-to-blacklist");}});

        TRADE s = isImportStation ? resultsController.getCurrentImportStation() : resultsController.getCurrentExportStation();
        BlacklistHandler.addSystemToBlacklist(s.STATION.SYSTEM);
        resultsController.mainController.updateFilters();
    }
    @FXML
    private void removeStation()
    {
        MixpanelHandler.addMessage(MixpanelEvents.BUTTON_CLICKED.name(), new HashMap<>(){{put("buttonName", "remove-station-temporary");}});

        TRADE s = isImportStation ? resultsController.getCurrentImportStation() : resultsController.getCurrentExportStation();
        int commodityID = DBHandler.getCommodityIDByName(resultsController.getCurrentTrade().NAME);
        DBHandler.removeStationEntry(commodityID, s.STATION.NAME, s.STATION.SYSTEM, !isImportStation);

        resultsController.removeStationFromCurrentTrade(s);

        if(!isImportStation && stationID >= resultsController.getCurrentTrade().EXPORT_PRICES.size()) stationID = resultsController.getCurrentTrade().EXPORT_PRICES.size() - 1;
        else if(isImportStation && stationID >= resultsController.getCurrentTrade().IMPORT_PRICES.size()) stationID = resultsController.getCurrentTrade().IMPORT_PRICES.size() - 1;

        if(!isImportStation) resultsController.currentExportStation = stationID;
        else resultsController.currentImportStation = stationID;
        resultsController.displayResults();

        if(resultsController.getCurrentTrade().EXPORT_PRICES.size() == 0 || resultsController.getCurrentTrade().IMPORT_PRICES.size() == 0) resultsController.removeCurrentTrade();

        resultsController.displayResults();
    }
    @FXML
    private void copySystemToClipboard()
    {
        ExternalProgram.copyToClipboard(stationSystem);
    }
    @FXML
    private void openStationOnInara()
    {
        ExternalProgram.openWebsite(ENDPOINTS.StationInfo.url + inaraID);
    }

    public void setStation(TRADE trade, boolean hasPrev, boolean hasNext)
    {
        stationSystem = trade.STATION.SYSTEM;
        stationName = trade.STATION.NAME;
        try
        {
            inaraID = InaraStation.getInaraStationID(stationName, stationSystem);
        } catch(InterruptedException | HtmlConnectionException e)
        {
            Logger.log("Could not get the correct inaraID for a station: " + stationName + " | " + stationSystem, LogName.WARNING);
        }

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        lblSystem.setText("System:\t " + stationSystem);
        lblStationName.setText("Station:\t " + stationName);
        lblType.setText("Type:\t " + trade.STATION.TYPE);
        String typeIcon = "/images/station_orbit.png";
        switch(trade.STATION.TYPE)
        {
            case CARRIER:
                typeIcon = "/images/station_carrier.png";
                break;
            case SURFACE:
            case ODYSSEY:typeIcon = "/images/station_surface.png";
                break;
            default:
                break;
        }
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(typeIcon)), 20, 20, false, false);
        lblTypeIcon.setGraphic(new ImageView(img));
        lblPad.setText("Pad:\t\t " + trade.STATION.PAD);
        lblPrice.setText("Price:\t " + df.format((isImportStation ? trade.IMPORT_PRICE : trade.EXPORT_PRICE)) + " credits");
        lblAmount.setText((isImportStation ? "Demand:\t " : "Supply:\t ") + df.format((isImportStation ? trade.DEMAND : trade.SUPPLY)) + " tons");

        df.setMaximumFractionDigits(0);

        lblStarDistance.setText("Star Distance:\t " + (trade.STATION.DISTANCE_TO_STAR < 0 ? "---" : df.format(trade.STATION.DISTANCE_TO_STAR) + " Ls"));
        lblAge.setText("Data age:\t " + trade.getUpdateAge());

        btnPrevStation.setDisable(!hasPrev);
        btnNextStation.setDisable(!hasNext);
    }
    public void setDetailsVisibility(Settings.Details detailLevel)
    {
        boolean advanced = detailLevel.equals(Settings.Details.Advanced);
        boolean normal = detailLevel.equals(Settings.Details.Normal) || advanced;

        lblType.setVisible(normal);
        lblType.setManaged(normal);

        lblPad.setVisible(normal);
        lblPad.setManaged(normal);

        lblStarDistance.setVisible(normal);
        lblStarDistance.setManaged(normal);

        lblAge.setVisible(normal);
        lblAge.setManaged(normal);

        btnReddit.setVisible(advanced);
        btnReddit.setManaged(advanced);

        btnDiscord.setVisible(advanced);
        btnDiscord.setManaged(advanced);
    }

    public void setResultsController(Results controller, boolean isImportStation)
    {
        resultsController = controller;
        this.isImportStation = isImportStation;
        if(isImportStation)
        {
            lblAction.setText("Sell at");
            lblAmount.setText("Demand: ---");
        }
    }
}
