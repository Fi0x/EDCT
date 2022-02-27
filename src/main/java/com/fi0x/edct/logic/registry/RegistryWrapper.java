package com.fi0x.edct.logic.registry;

import java.util.prefs.Preferences;

public class RegistryWrapper
{
    public static String getUserID(String defaultValue)
    {

        return defaultValue;
    }

    public static void storeUserID(String value)
    {
        Preferences.userRoot().put("SOFTWARE\\EDCT", value);
    }
}
