package com.fi0x.edct.controller;

import com.fi0x.edct.dbconnection.RequestThread;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Datastorage
{
    private Interaction interactionController;

    @FXML
    public Button btnStart;

    @FXML
    private void calculate()
    {
        btnStart.setVisible(false);

        Thread threadReq = new Thread(new RequestThread(interactionController, 1));
        threadReq.start();
    }

    public void setInteractionController(Interaction controller)
    {
        interactionController = controller;
    }
}
