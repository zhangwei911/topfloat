package com.apkstory.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wei on 13-7-23.
 */
public class HttpDownloader {

    public static String download(String urlPath, String charset) {
        StringBuilder sb = new StringBuilder();
        String line = null;
        BufferedReader br = null;
        String result = null;

        try {
            //create a URL object
            URL url = new URL(urlPath);
            Log.e("vitest", "----1----");
            //create http connection
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            //use i/o stream to read data
            br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), charset));
            String newline = System.getProperty("line.separator");
            while ((line = br.readLine()) != null) {
                sb.append(line).append(newline);
            }
            result = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error------", "Url Failed ! " + e.getMessage());
        }
        return result;
    }
}
