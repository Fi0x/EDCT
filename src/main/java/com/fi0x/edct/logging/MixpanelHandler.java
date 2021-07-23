package com.fi0x.edct.logging;

import com.fi0x.edct.Main;
import com.fi0x.edct.gui.controller.Settings;
import com.fi0x.edct.logic.filesystem.SettingsHandler;
import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MixpanelHandler implements Runnable
{
    private static final String PROJECT_TOKEN = "cbdd63a3871a9f08b430df46217cf420";
    private static boolean debug = false;
    private static String userID;

    private static MessageBuilder builder;
    private static ClientDelivery delivery;

    @Override
    public void run()
    {
        while(!Thread.interrupted())
        {
            if(delivery != null) sendMessages();

            try
            {
                Thread.sleep(3000);
            } catch(InterruptedException e)
            {
                break;
            }
        }
    }

    public static void sendMessages()
    {
        try
        {
            new MixpanelAPI().deliver(delivery);
        } catch(UnknownHostException ignored)
        {
        } catch(SocketException ignored)
        {
            Logger.WARNING(995, "Could not establish a connection to mixpanel");
        } catch(IOException e)
        {
            Logger.WARNING(995, "Could not send messages to mixpanel", e);
            return;
        }

        Logger.INFO("Mixpanel delivery successfully sent");
        delivery = null;
    }

    public static void addMessage(EVENT event, @Nullable Map<String, String> properties)
    {
        if(properties == null) properties = new HashMap<>();
        addDefaultProperties(properties);

        JSONObject props = new JSONObject();
        for(Map.Entry<String, String> property : properties.entrySet())
        {
            try
            {
                props.put(property.getKey(), property.getValue());
            } catch(JSONException ignored)
            {
            }
        }

        addMessageToDelivery(getBuilder().event(getUserID(), String.valueOf(event), props));
        Logger.INFO("Added message for mixpanel to delivery queue");
    }

    public static Map<String, String> getProgramState()
    {
        Map<String, String> props = new HashMap<>();

        SettingsHandler.addFiltersToMap(props);
        SettingsHandler.addSettingsToMap(props);

        return props;
    }

    public static Map<String, String> getButtonProperty(String buttonName)
    {
        Map<String, String> props = new HashMap<>();

        props.put("buttonName", buttonName);

        return props;
    }

    public static void setDebugMode(boolean isDebug)
    {
        debug = isDebug;
    }

    private static void addDefaultProperties(Map<String, String> props)
    {
        props.put("version", Main.version);
        props.put("debug", String.valueOf(debug));
        props.put("settingsMode", String.valueOf(Settings.detailedResults));
    }

    private static MessageBuilder getBuilder()
    {
        if(builder == null) builder = new MessageBuilder(PROJECT_TOKEN);
        return builder;
    }
    private static void addMessageToDelivery(JSONObject message)
    {
        if(delivery == null) delivery = new ClientDelivery();
        delivery.addMessage(message);
    }
    private static String getUserID()
    {
        if(userID == null)
        {
            userID = SettingsHandler.loadString("userID", "");

            if(userID.length() < 50)
            {
                String randomString = new Random().ints(48, 123)
                        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                        .limit(50)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();

                userID = randomString;
                SettingsHandler.storeValue("userID", randomString);
            }

            Logger.INFO("UserID: " + userID);
        }

        return userID;
    }

    public enum EVENT
    {
        INITIALIZATION,
        SHUTDOWN,
        WARNING,
        ERROR,
        SETTINGS_CLOSED,
        FILTERS_CHANGE,
        BUTTON_CLICKED,
        TRADES_LOADED
    }
}
