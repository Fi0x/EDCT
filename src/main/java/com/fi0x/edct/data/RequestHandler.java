package com.fi0x.edct.data;

import com.fi0x.edct.Main;
import com.fi0x.edct.util.Logger;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RequestHandler
{
    @Nullable
    public static String sendHTTPRequest(String endpoint, String requestType, Map<String, String> parameters) throws InterruptedException
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
    public static String sendHTTPRequest(String endpoint, String requestType, Map<String, String> parameters, boolean ignore429) throws IOException, InterruptedException
    {
        if(!canRequest(ignore429)) return null;

        endpoint += getParamsString(parameters);
        URL url = cleanUpUrl(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(requestType);

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int status = 0;
        try
        {
            status = con.getResponseCode();
        } catch(IOException e)
        {
            Logger.WARNING(995, "Could not establish a connection to the server");
        }
        StringBuilder content = new StringBuilder();
        if(status == 200)
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            try
            {
                while((inputLine = in.readLine()) != null)
                {
                    content.append(inputLine);
                }
            }
            catch(SocketTimeoutException e)
            {
                Logger.WARNING("A http request timed out", e);
            }
            in.close();
        } else if(status == 429) Logger.ERROR(429, "Received a 429 status code from a website");
        else if(status != 0) Logger.WARNING("Received a bad HTTP response: " + status);

        con.disconnect();

        return content.toString();
    }

    private static boolean canRequest(boolean ignore429) throws IOException, InterruptedException
    {
        if(!ignore429)
        {
            if(!Main.errors.exists()) return true;
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.errors.toPath(), StandardCharsets.UTF_8));

            for(int i = fileContent.size() - 1; i >= 0; i--)
            {
                String error = fileContent.get(i);
                if(!error.contains("[ERR]") && !error.contains("[WRN]")) continue;

                if(error.contains("[429]"))
                {
                    String[] logEntry = error.split("]");
                    String errorTimeString = logEntry[0].replace("[", "");

                    Date errorDate = getDateFromString(errorTimeString);
                    if(errorDate == null) return true;

                    if(System.currentTimeMillis() <= errorDate.getTime() + 1000 * 60 * 60)
                    {
                        Logger.WARNING(429, "HTTP request could not be sent because of a 429 response");
                        return false;
                    }
                } else if(error.contains("[995]"))
                {
                    String[] logEntry = error.split("]");
                    String errorTimeString = logEntry[0].replace("[", "");

                    Date errorDate = getDateFromString(errorTimeString);
                    if(errorDate == null) return true;

                    if(System.currentTimeMillis() <= errorDate.getTime() + 1000 * 10)
                    {
                        Thread.sleep(1000 * 10);
                    }
                }
            }
        }

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

    private static Date getDateFromString(String input)
    {
        try
        {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(input);
        } catch(ParseException e)
        {
            Logger.WARNING("Could not parse a date: " + input, e);
            return null;
        }
    }

    private static URL cleanUpUrl(String endpoint) throws MalformedURLException
    {

        return new URL(endpoint
                .replace(" ", "%20")
                .replace("'", "%27")
                .replace("`", "%60")
        );
    }
}