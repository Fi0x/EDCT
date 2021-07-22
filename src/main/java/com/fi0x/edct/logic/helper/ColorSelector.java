package com.fi0x.edct.logic.helper;

import com.fi0x.edct.gui.controller.Settings;
import javafx.scene.paint.Color;

public class ColorSelector
{

    public static Color getProfitColor(long profit)
    {
        Color color = new Color(238d/255d, 238d/255d, 238d/255d, 1);

        if(profit < Settings.lowProfitBorder)
        {
            color = new Color(238d/255d, 50d/255d, 50d/255d, 1);
        } else if(profit > Settings.highProfitBorder)
        {
            color = new Color(50d/255d, 238d/255d, 50d/255d, 1);
        }

        return color;
    }
}
