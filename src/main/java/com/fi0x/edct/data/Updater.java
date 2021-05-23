package com.fi0x.edct.data;

import com.fi0x.edct.util.Out;

public class Updater implements Runnable
{
    @Override
    public void run()
    {
        Out.newBuilder("Updater thread started").verbose().SUCCESS().print();

        while(!Thread.interrupted())
        {
        }
    }
}
