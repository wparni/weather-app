package com.example.arni.weatherapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadAllData {

    String sendQuery(String urlAddress) {
        String response = null;

        try {
            URL url = new URL(urlAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            response = streamToString(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    private String streamToString(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();

        String line;
        try {
            do {
                line = bufferedReader.readLine();
                builder.append(line);
            } while (line != null);

            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
