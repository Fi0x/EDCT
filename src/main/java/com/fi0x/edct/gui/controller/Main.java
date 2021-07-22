package com.fi0x.edct.gui.controller;

import com.fi0x.edct.logic.helper.FilterUpdater;

public class Main
{
    private Results resultsController;
    public Interaction interactionController;

    public void setResultController(Results controller)
    {
        resultsController = controller;
    }
    public void setInteractionController(Interaction controller)
    {
        interactionController = controller;
    }

    public void updateFilters()
    {
        FilterUpdater.updateFilters(interactionController, resultsController);
    }
}