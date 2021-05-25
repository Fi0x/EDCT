package com.fi0x.edct.data.webconnection;

import com.fi0x.edct.Main;
import com.fi0x.edct.util.Out;

import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestHandler
{
    @Nullable
    public static String sendHTTPRequest(String endpoint, String requestType, Map<String, String> parameters) throws IOException
    {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(Main.errors.toPath(), StandardCharsets.UTF_8));
        for(int i = fileContent.size() - 1; i >= 0; i--)
        {
            String error = fileContent.get(i);
            if(error.startsWith("429"))
            {
                String errorTime = error.replace("429:", "");
                if(System.currentTimeMillis() <= Long.parseLong(errorTime) + 1000 * 60 * 60)
                {
                    Out.newBuilder("There was a recent block from inara for this address (429)").always().WARNING().print();
                    return "";
                }
            }
        }
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
        } else if(status == 429)
        {
            Out.newBuilder("Received a 429 status code. Please wait a while until you try your next request").always().ERROR().print();
            fileContent = new ArrayList<>(Files.readAllLines(Main.errors.toPath(), StandardCharsets.UTF_8));
            fileContent.add("429:" + System.currentTimeMillis());
            Files.write(Main.errors.toPath(), fileContent, StandardCharsets.UTF_8);
        } else Out.newBuilder("Response code of HTTP request was " + status).always().ERROR().print();

        con.disconnect();

        return content.toString();
    }

    private static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException
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