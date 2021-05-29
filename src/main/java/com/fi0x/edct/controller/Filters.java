package com.fi0x.edct.controller;

import com.fi0x.edct.util.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Filters implements Initializable
{
    private Main mainController;

    @FXML
    private TextField txtQuantity;
    @FXML
    private CheckBox cbCarrier;
    @FXML
    private CheckBox cbSurface;
    @FXML
    private CheckBox cbLandingPad;
    @FXML
    private CheckBox cbDemand;
    @FXML
    private CheckBox cbOdyssey;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        loadFilters();

        txtQuantity.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 9) txtQuantity.setText(oldValue);
            else if(!newValue.matches("\\d*")) txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
            else updateFilters();

        });
        cbCarrier.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbSurface.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbLandingPad.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbDemand.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbOdyssey.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
    }

    public void updateFilters()
    {
        int amount = Integer.parseInt(txtQuantity.getText().length() > 0 ? txtQuantity.getText() : "0");
        mainController.updateFilters(amount, cbDemand.isSelected(), !cbLandingPad.isSelected(), !cbCarrier.isSelected(), !cbSurface.isSelected(), !cbOdyssey.isSelected());
        saveFilters();
    }

    public void setMainController(Main controller)
    {
        mainController = controller;
    }

    private void loadFilters()
    {
        try
        {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(com.fi0x.edct.Main.settings.toPath(), StandardCharsets.UTF_8));
            for(String line : fileContent)
            {
                String[] setting = line.split("=");
                if(setting.length < 2) continue;

                switch(setting[0])
                {
                    case "quantity":
                        txtQuantity.setText(setting[1]);
                        break;
                    case "carrier":
                        cbCarrier.setSelected(Boolean.parseBoolean(setting[1]));
                        break;
                    case "surface":
                        cbSurface.setSelected(Boolean.parseBoolean(setting[1]));
                        break;
                    case "pad":
                        cbLandingPad.setSelected(Boolean.parseBoolean(setting[1]));
                        break;
                    case "demand":
                        cbDemand.setSelected(Boolean.parseBoolean(setting[1]));
                        break;
                    case "odyssey":
                        cbOdyssey.setSelected(Boolean.parseBoolean(setting[1]));
                        break;
                }
            }
        } catch(IOException e)
        {
            Logger.WARNING("Could not read filter settings from local file", e);
        }
    }

    private void saveFilters()
    {
        try
        {
            List<String> fileContent = new ArrayList<>();

            fileContent.add("quantity=" + txtQuantity.getText());
            fileContent.add("carrier=" + cbCarrier.isSelected());
            fileContent.add("surface=" + cbSurface.isSelected());
            fileContent.add("pad=" + cbLandingPad.isSelected());
            fileContent.add("demand=" + cbDemand.isSelected());
            fileContent.add("odyssey=" + cbOdyssey.isSelected());

            Files.write(com.fi0x.edct.Main.settings.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            Logger.WARNING("Could not write filter settings to local file", e);
        }
    }
}