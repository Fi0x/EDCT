package com.fi0x.edct.logic.registry;

import com.fi0x.edct.Main;

import java.util.prefs.Preferences;

public class RegistryWrapper
{
    private static final Preferences node = Preferences.userNodeForPackage(Main.class);
    public static String getString(String key, String defaultValue)
    {
        return node.get(key, defaultValue);
    }

    public static void storeString(String key, String value)
    {
        node.put(key, value);
    }

    public static boolean getBool(String key, boolean defaultValue)
    {
        return node.getBoolean(key, defaultValue);
    }
    public static void storeBool(String key, boolean value)
    {
        node.putBoolean(key, value);
    }

    public static int getInt(String key, int defaultValue)
    {
        return node.getInt(key, defaultValue);
    }
    public static void storeInt(String key, int value)
    {
        node.putInt(key, value);
    }
}
