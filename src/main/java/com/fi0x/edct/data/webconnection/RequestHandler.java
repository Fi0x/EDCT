package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.Main;
import com.fi0x.edct.util.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RequestHandler
{
    @Nullable
    public static String sendHTTPRequest(String endpoint, String requestType, Map<String, String> parameters)
    {
        try
        {
            return sendHTTPRequest(endpoint, requestType, parameters, false);
        } catch(IOException e)
        {
            Logger.WARNING("Some error occurred when sending a HTTP request", e);
            return null;
        }
    }
    @Nullable
    public static String sendHTTPRequest(String endpoint, String requestType, Map<String, String> parameters, boolean ignore429) throws IOException
    {
        if(!canRequest(ignore429)) return null;

        endpoint += getParamsString(parameters);
        URL url = new URL(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(requestType);

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int status = con.getResponseCode();
        StringBuilder content = new StringBuilder();
        if(status == 200)
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while((inputLine = in.readLine()) != null)
            {
                content.append(inputLine);
            }
            in.close();
        } else if(status == 429) Logger.ERROR(429, "Received a 429 status code from a website");
        else Logger.WARNING("Received a bad HTTP response: " + status);

        con.disconnect();

        return content.toString();
    }

    private static boolean canRequest(boolean ignore429) throws IOException
    {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.errors.toPath(), StandardCharsets.UTF_8));
        if(!ignore429)
        {
            for(int i = fileContent.size() - 1; i >= 0; i--)
            {
                String error = fileContent.get(i);
                if(error.contains("[429]"))
                {
                    String[] logEntry = error.split("]");
                    String errorTimeString = logEntry[0].replace("[", "");
                    Date errorDate = Date.from(Instant.parse(errorTimeString));
                    if(System.currentTimeMillis() <= errorDate.getTime() + 1000 * 60 * 60)
                    {
                        Logger.WARNING("HTTP request could not be sent because of a 429 response");
                        return false;
                    }
                }
            }
        }

        //TODO: Check for socketException

        return true;
    }

    private static String getParamsString(Map<String, String> params)
    {
        StringBuilder result = new StringBuilder();

        for(Map.Entry<String, String> entry : params.entrySet())
        {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0 ? "?" + resultString.substring(0, resultString.length() - 1) : "";
    }
}