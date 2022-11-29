package com.fi0x.edct.logic.filesystem;

import org.json.simple.JSONObject;

public class ConfigHandler
{
    public static String getValue(JSONObject json, String key, boolean unloading, String type)
    {
        JSONObject keyArea = null;
        if(json.containsKey("Required Variables") && ((JSONObject) json.get("Required Variables")).containsKey(key))
        {
            keyArea = (JSONObject) json.get("Required Variables");
        } else if(json.containsKey("Custom Variables") && ((JSONObject) json.get("Custom Variables")).containsKey(key))
        {
            keyArea = (JSONObject) json.get("Custom Variables");
        }
        if(keyArea == null) return "";

        String value = keyArea.get(key).toString();
        if(value.charAt(0) != '{') return value;

        JSONObject keyJson = (JSONObject) keyArea.get(key);
        if(keyJson.containsKey(unloading ? "UNLOADING" : "LOADING"))
        {
            if(type == null) return keyJson.get(unloading ? "UNLOADING" : "LOADING").toString();
            else
            {
                JSONObject loadingJson = (JSONObject) keyJson.get(unloading ? "UNLOADING" : "LOADING");
                if(loadingJson.containsKey(type)) return loadingJson.get(type).toString();
            }
        } else return keyJson.toString();

        return "";
    }
}
