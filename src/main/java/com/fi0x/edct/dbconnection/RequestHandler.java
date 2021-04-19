package com.fi0x.edct.dbconnection;

import com.fi0x.edct.util.Out;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class RequestHandler
{
    public static String sendHTTPRequest(String endpoint, String requestType, Map<String, String> parameters) throws IOException
    {
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
        } else Out.newBuilder("Response code of HTTP request was " + status).always().ERROR().print();

        con.disconnect();

        return content.toString();
    }

    private static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();

        for(Map.Entry<String, String> entry : params.entrySet())
        {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0 ? "?" + resultString.substring(0, resultString.length() - 1) : "";
    }
}