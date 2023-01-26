package com.fi0x.edct.logic.webrequests;

import com.fi0x.edct.logging.LogName;
import com.fi0x.edct.logging.exceptions.HtmlConnectionException;
import io.fi0x.javalogger.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class RequestHandler
{
    public static String sendHTTPRequest(String endpoint, String requestType, Map<String, String> parameters) throws InterruptedException, HtmlConnectionException
    {
        try
        {
            return sendHTTPRequest(endpoint, requestType, parameters, false);
        } catch(IOException e)
        {
            Logger.log("Some error occurred when sending a HTTP request", LogName.WARNING, e, 995);
            return null;
        }
    }
    public static String sendHTTPRequest(String endpoint, String requestType, Map<String, String> parameters, boolean ignore429) throws IOException, InterruptedException, HtmlConnectionException
    {
        if(!canRequest(ignore429))
            return null;

        endpoint += getParamsString(parameters);
        URL url = cleanUpUrl(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(requestType);
        con.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int status;
        try
        {
            status = con.getResponseCode();
        } catch(IOException e)
        {
            Logger.log("Could not establish a connection to the server for request: " + endpoint, LogName.WARNING, e, 995);
            throw new HtmlConnectionException();
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
                Logger.log("A http request timed out", LogName.WARNING, e);
            }
            in.close();
        }
        else if(status == 429)
            Logger.log("Received a 429 status code from a website\n\tUrl was: " + url, LogName.getError(429), null, 429);
        else if(status != 0)
            Logger.log("Received a bad HTTP response: " + status + "\n\tFor url: " + url, LogName.WARNING);

        con.disconnect();

        return content.toString();
    }

    private static boolean canRequest(boolean ignore429) throws IOException, InterruptedException
    {
//        if(!ignore429)
//        {
//            if(!Main.errors.exists()) return true;
//            List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.errors.toPath(), StandardCharsets.UTF_8));
//
//            for(int i = fileContent.size() - 1; i >= 0; i--)
//            {
//                String error = fileContent.get(i);
//                if(!error.contains("[ERR]") && !error.contains("[WRN]")) continue;
//
//                if(error.contains("[429]"))
//                {
//                    String[] logEntry = error.split("]");
//                    String errorTimeString = logEntry[0].replace("[", "");
//
//                    Date errorDate = getDateFromString(errorTimeString);
//                    if(errorDate == null) return true;
//
//                    if(System.currentTimeMillis() <= errorDate.getTime() + 1000 * 60 * 60)
//                    {
//                        Logger.log("HTTP request could not be sent because of a 429 response", LogName.WARNING, null, 429);
//                        return false;
//                    }
//                } else if(error.contains("[995]"))
//                {
//                    String[] logEntry = error.split("]");
//                    String errorTimeString = logEntry[0].replace("[", "");
//
//                    Date errorDate = getDateFromString(errorTimeString);
//                    if(errorDate == null) return true;
//
//                    if(System.currentTimeMillis() <= errorDate.getTime() + 1000 * 10)
//                    {
//                        Thread.sleep(1000 * 10);
//                    }
//                }
//            }
//        }

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
            Logger.log("Could not parse a date: " + input, LogName.WARNING, e);
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