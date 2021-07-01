package com.fi0x.edct.telemetry;

import com.fi0x.edct.util.Logger;
import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class MixpanelHandler
{
    private static final String PROJECT_TOKEN = "cbdd63a3871a9f08b430df46217cf420";
    private static MessageBuilder builder;
    private static ClientDelivery delivery;

    public static boolean sendMessages()
    {
        try
        {
            new MixpanelAPI().deliver(delivery);
        } catch(IOException e)
        {
            Logger.WARNING("Could not send messages to mixpanel", e);
            return false;
        }

        delivery = null;
        return true;
    }

    public static boolean addMessage(String userID, String eventName, Map<String, String> properties)
    {
        JSONObject props = new JSONObject();
        for(Map.Entry<String, String> property : properties.entrySet())
        {
            try
            {
                props.put(property.getKey(), property.getValue());
            } catch(JSONException e)
            {
                Logger.WARNING("Could not add a mixpanel message to the delivery queue", e);
                return false;
            }
        }

        addMessageToDelivery(getBuilder().event(userID, eventName, props));
        Logger.INFO("Added message for mixpanel to delivery queue");
        return true;
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
}
