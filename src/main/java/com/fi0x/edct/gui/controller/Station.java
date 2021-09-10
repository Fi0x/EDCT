package com.fi0x.edct.gui.controller;

import com.fi0x.edct.logging.Logger;
import com.fi0x.edct.logic.database.DBHandler;
import com.fi0x.edct.logic.filesystem.BlacklistHandler;
import com.fi0x.edct.logic.helper.ConvertToString;
import com.fi0x.edct.logic.helper.ExternalProgram;
import com.fi0x.edct.logic.structures.TRADE;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class Station implements Initializable
{
    private Results resultsController;

    private boolean isBuying;
    public int stationID;
    private String stationSystem;

    @FXML
    private Label lblAction;
    @FXML
    private Button btnBlacklist;
    @FXML
    private Label lblSystem;
    @FXML
    private Label lblStationName;
    @FXML
    private Label lblType;
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

        btnReddit.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
        {
            TRADE station = isBuying ? resultsController.getCurrentBuyStation() : resultsController.getCurrentSellStation();
            String text;
            if(e.getButton() == MouseButton.SECONDARY) text = ConvertToString.redditContent(resultsController, station, isBuying);
            else text = ConvertToString.redditTitle(resultsController, station, isBuying);

            if(text == null)
            {
                Logger.ERROR(992, "Something went wrong when creating a reddit String");
                return;
            }

            ExternalProgram.copyToClipboard(text);
        });
        btnDiscord.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
        {
            TRADE station = isBuying ? resultsController.getCurrentBuyStation() : resultsController.getCurrentSellStation();
            String text = ConvertToString.discordText(resultsController, station, isBuying);

            if(text == null)
            {
                Logger.ERROR(992, "Something went wrong when creating a discord String");
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
        if(!isBuying && stationID >= resultsController.getCurrentTrade().BUY_PRICES.size())
        {
            stationID = resultsController.getCurrentTrade().BUY_PRICES.size() - 1;
        } else if(isBuying && stationID >= resultsController.getCurrentTrade().SELL_PRICES.size())
        {
            stationID = resultsController.getCurrentTrade().SELL_PRICES.size() - 1;
        }

        if(!isBuying) resultsController.currentBuyStation = stationID;
        else resultsController.currentSellStation = stationID;
        resultsController.displayResults();
    }
    @FXML
    private void previousStation()
    {
        stationID--;
        if(stationID < 0) stationID = 0;

        if(!isBuying) resultsController.currentBuyStation = stationID;
        else resultsController.currentSellStation = stationID;
        resultsController.displayResults();
    }
    @FXML
    private void addToBlacklist()
    {
        TRADE s = isBuying ? resultsController.getCurrentBuyStation() : resultsController.getCurrentSellStation();
        BlacklistHandler.addSystemToBlacklist(s.STATION.SYSTEM);
        resultsController.mainController.updateFilters();
    }
    @FXML
    private void removeStation()
    {
        TRADE s = isBuying ? resultsController.getCurrentBuyStation() : resultsController.getCurrentSellStation();
        int commodityID = DBHandler.getCommodityIDByName(resultsController.getCurrentTrade().NAME);
        DBHandler.removeStationEntry(commodityID, s.STATION.NAME, s.STATION.SYSTEM, !isBuying);

        resultsController.removeStationFromCurrentTrade(s);

        if(!isBuying && stationID >= resultsController.getCurrentTrade().BUY_PRICES.size()) stationID = resultsController.getCurrentTrade().BUY_PRICES.size() - 1;
        else if(isBuying && stationID >= resultsController.getCurrentTrade().SELL_PRICES.size()) stationID = resultsController.getCurrentTrade().SELL_PRICES.size() - 1;

        if(!isBuying) resultsController.currentBuyStation = stationID;
        else resultsController.currentSellStation = stationID;
        resultsController.displayResults();

        if(resultsController.getCurrentTrade().BUY_PRICES.size() == 0 || resultsController.getCurrentTrade().SELL_PRICES.size() == 0) resultsController.removeCurrentTrade();

        resultsController.displayResults();
    }
    @FXML
    private void copySystemToClipboard()
    {
        ExternalProgram.copyToClipboard(stationSystem);
    }

    public void setStation(TRADE station, boolean hasPrev, boolean hasNext)
    {
        stationSystem = station.STATION.SYSTEM;

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        lblSystem.setText("System:\t " + stationSystem);
        lblStationName.setText("Station:\t " + station.STATION.NAME);
        lblType.setText("Type:\t " + station.STATION.TYPE);
        lblPad.setText("Pad:\t\t " + station.STATION.PAD);
        lblPrice.setText("Price:\t " + df.format((isBuying ? station.BUY_PRICE : station.SELL_PRICE)) + " credits");
        lblAmount.setText((isBuying ? "Demand:\t " : "Supply:\t ") + df.format((isBuying ? station.DEMAND : station.SUPPLY)) + " tons");

        df.setMaximumFractionDigits(0);

        lblStarDistance.setText("Star Distance:\t " + (station.STATION.DISTANCE_TO_STAR < 0 ? "---" : df.format(station.STATION.DISTANCE_TO_STAR) + " Ls"));
        lblAge.setText("Data age:\t " + station.getUpdateAge());

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

    public void setResultsController(Results controller, boolean isBuying)
    {
        resultsController = controller;
        this.isBuying = isBuying;
        if(isBuying)
        {
            lblAction.setText("Sell at");
            lblAmount.setText("Demand: ---");
        }
    }
}
