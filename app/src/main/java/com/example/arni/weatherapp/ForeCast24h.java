package com.example.arni.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Arni on 2017-08-29.
 */

public class ForeCast24h extends Activity implements ConvertingWindUnits {
    private Weather weather;
    static List<Weather> weatherForecast24h = new ArrayList<>();
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast);


        textView = (TextView) findViewById(R.id.data_tv);
        new DownloadDataForecast24h().execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openMainActivity();
        finish();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public String convertKmtoMilesSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) / 1.61)) + MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "");
    }

    public String convertMilesToKmSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) * 1.61)) + MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "");
    }

    public String convertMetersToMilesSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) * 2.24)) + MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "");
    }

    public String converMetersToKmWindSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) * 3.6)) + MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "");

    }

    public String convertToFahrenheit(String actualTemp) {
        actualTemp = actualTemp.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(actualTemp) * 1.8 + 32)) + " °F";

    }

    public String convertToCelcius(String actualTemp) {
        actualTemp = actualTemp.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", (Double.parseDouble(actualTemp) - 32) / 1.8) + " ℃");
    }

    private class DownloadDataForecast24h extends AsyncTask<Void, Void, Void> {
        String newUrlAddressForecast24h = String.format(MainActivity.urlAddress, MainActivity.sharedPreferences.getString(MainActivity.LOCATION_KEY, "")
                , MainActivity.sharedPreferences.getString(MainActivity.LANGUAGE_KEY, "")
                , MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, ""));

        @Override
        protected Void doInBackground(Void... params) {

            String jsonString = new DownloadAllData().sendQuery(newUrlAddressForecast24h);
            if (jsonString != null) {
                try {
                    JSONObject json = new JSONObject(jsonString);
                    JSONArray weatherData = json.getJSONArray("list");

                    for (int i = 0; i < 8; i++) {


                        JSONObject info = weatherData.getJSONObject(i);
                        JSONObject main = info.getJSONObject("main");
                        String temp = main.getString("temp");


                        JSONArray weatherDescriptionList = info.getJSONArray("weather");
                        JSONObject weatherDescription = weatherDescriptionList.getJSONObject(0);
                        String description = weatherDescription.getString("description");

                        JSONObject windObject = info.getJSONObject("wind");
                        String windSpeed = windObject.getString("speed");


                        String parsedTempCelcius = String.format(Math.round(Float.parseFloat(temp)) + "%s", " ℃");
                        String parsedTempFahrenheit = String.format(Math.round(Float.parseFloat(temp)) + "%s", " °F");

                        if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals("metric")) {
                            if (MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "").equals("km/h")) {
                                windSpeed = converMetersToKmWindSpeed(windSpeed);
                            } else if (MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "").equals("mil/h")) {
                                windSpeed = convertMetersToMilesSpeed(windSpeed);
                            }
                            weather = new Weather(parsedTempCelcius, description, windSpeed);
                        } else if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals("imperial")) {
                            if (MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "").equals("km/h")) {
                                windSpeed = convertMilesToKmSpeed(windSpeed);
                            } else if (MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "").equals("mil/h")) {
                                windSpeed = String.format(Locale.US, "%.0f %s", Double.parseDouble(windSpeed), MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, ""));
                            }
                            weather = new Weather(parsedTempFahrenheit, description, windSpeed);
                        }
                        if (weatherForecast24h.isEmpty() || weatherForecast24h.size()<8) {
                            weatherForecast24h.add(weather);
                        }else if (weatherForecast24h.size()==8){
                            weatherForecast24h.clear();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            textView.setText(weatherForecast24h.get(1).getDescription());
        }
    }
}
