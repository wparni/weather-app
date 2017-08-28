package com.example.arni.weatherapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String urlAddress = "http://api.openweathermap.org/data/2.5/forecast?q=%s&lang=%s&units=%s&APPID=909d0a4d34bbefca53de0afe4fdf5fde";
    public static final String TEMPERATURE_KEY = "temperature";
    public static final String WIND_KEY = "wind";
    public static final String LANGUAGE_KEY = "language";
    public static final String LOCATION_KEY = "location";
    static final String SAVING_KEY = "saving_array";
    private ListView listView;
    static List<Weather> weatherList = new ArrayList<>();
    private WeatherAdapter weatherAdapter;
    private String city;
    private SwipeRefreshLayout swipeRefreshLayout;
    static SharedPreferences sharedPreferences;
    private String language;
    boolean connected;
    private String temperatureValue;
    private Weather weather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);

        listView = (ListView) findViewById(R.id.list_view);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        city = sharedPreferences.getString(LOCATION_KEY, "");
        language = sharedPreferences.getString(LANGUAGE_KEY, "");
        temperatureValue = sharedPreferences.getString(TEMPERATURE_KEY, "");

        if (getSupportActionBar() != null && !sharedPreferences.getString(LOCATION_KEY, "").isEmpty()) {
            getSupportActionBar().setTitle(sharedPreferences.getString(LOCATION_KEY, ""));
        } else if (sharedPreferences.getString(LOCATION_KEY, "").isEmpty() && sharedPreferences.getString(LANGUAGE_KEY, "").equals("eng")) {
            getSupportActionBar().setTitle("Set city");
        } else if (sharedPreferences.getString(LOCATION_KEY, "").isEmpty() && sharedPreferences.getString(LANGUAGE_KEY, "").equals("pl")) {
            getSupportActionBar().setTitle("Ustaw miasto");
        }

        weatherAdapter = new WeatherAdapter(this, weatherList);
        listView.setAdapter(weatherAdapter);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeather();
            }
        });


    }
    private String convertKmtoMilesSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) / 1.61)) +sharedPreferences.getString(WIND_KEY, "");
    }

    private String convertMilesToKmSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) * 1.61)) + sharedPreferences.getString(WIND_KEY, "");
    }

    private String convertMetersToMilesSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) * 2.24)) + sharedPreferences.getString(WIND_KEY, "");
    }

    private String converMetersToKmWindSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) * 3.6)) + sharedPreferences.getString(WIND_KEY, "");

    }

    private String convertToFahrenheit(String actualTemp) {
        actualTemp = actualTemp.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(actualTemp) * 1.8 + 32)) + " °F";

    }

    private String convertToCelcius(String actualTemp) {
        actualTemp = actualTemp.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", (Double.parseDouble(actualTemp) - 32) / 1.8) + " ℃");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                openSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkOn = cm.getActiveNetworkInfo();
            if (networkOn != null) {
                connected = true;
                getWeatherDataOnline();
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            } else {
                connected = false;
                updateDataOnSettingsChangedOffline();
                Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void updateDataOnSettingsChangedOffline() {
        if (getArrayList(SAVING_KEY) != null) {
            weatherList = getArrayList(SAVING_KEY);
            String actualTemp = weatherList.get(0).getTemperature();
            String windValue = weatherList.get(0).getWind();
            if(sharedPreferences.getString(WIND_KEY, "").equals("km/h") && !windValue.contains("km/h")){
                weatherList.get(0).setWind(convertMilesToKmSpeed(windValue));
            }else if(sharedPreferences.getString(WIND_KEY, "").equals("mil/h") && !windValue.contains("mil/h")){
                weatherList.get(0).setWind(convertKmtoMilesSpeed(windValue));
            }
            if (sharedPreferences.getString(TEMPERATURE_KEY, "").equals("metric") && !actualTemp.contains("℃")) {
                weatherList.get(0).setTemperature(convertToCelcius(actualTemp));
            } else if (sharedPreferences.getString(TEMPERATURE_KEY, "").equals("imperial") && !actualTemp.contains("°F")) {
                weatherList.get(0).setTemperature(convertToFahrenheit(actualTemp));
            }
            saveArrayListSharedPreferences(weatherList, SAVING_KEY);
        }
        weatherAdapter = new WeatherAdapter(MainActivity.this, weatherList);
        listView.setAdapter(weatherAdapter);

    }


    private void getWeatherDataOnline() {
        if (!sharedPreferences.getString(LOCATION_KEY, "").isEmpty() && !sharedPreferences.getString(LANGUAGE_KEY, "").isEmpty()) {
            new DownloadData().execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    public void saveArrayListSharedPreferences(List<Weather> list, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<Weather> getArrayList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Weather>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    private void updateWidget() {
        Intent intent = new Intent(this, WeatherWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        String newTemperature = weatherList.get(0).getTemperature();
        intent.putExtra(TEMPERATURE_KEY, newTemperature);
        sendBroadcast(intent);
    }

    private void refreshWeather() {
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          new RefreshDownloadData().execute();
                                          if (weatherList.isEmpty()) {
                                              runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      Toast.makeText(MainActivity.this, "There is no city on list", Toast.LENGTH_SHORT).show();
                                                  }
                                              });
                                          } else if (!connected) {
                                              Toast.makeText(MainActivity.this, "Cant refresh data, no connection", Toast.LENGTH_SHORT).show();
                                          }
                                          weatherAdapter.notifyDataSetChanged();
                                          swipeRefreshLayout.setRefreshing(false);
                                      }
                                  },
                2000);
    }


    private class DownloadData extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        String newUrlAddress = String.format(urlAddress, city, language, temperatureValue);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Checking weather");
            progress.setCancelable(false);
            progress.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            String jsonString = new DownloadAllData().sendQuery(newUrlAddress);
            if (jsonString != null) {
                try {
                    JSONObject json = new JSONObject(jsonString);
                    JSONArray weatherData = json.getJSONArray("list");
                    JSONObject info = weatherData.getJSONObject(0);
                    JSONObject main = info.getJSONObject("main");
                    String temp = main.getString("temp");
                    String humidity = main.getString("humidity");


                    JSONArray weatherDescriptionList = info.getJSONArray("weather");
                    JSONObject weatherDescription = weatherDescriptionList.getJSONObject(0);
                    String description = weatherDescription.getString("description");


                    JSONObject city = json.getJSONObject("city");
                    String country = city.getString("country");
                    String cityName = city.getString("name");

                    JSONObject wind = info.getJSONObject("wind");
                    String windSpeed = wind.getString("speed");


                    String parsedTempCelcius = String.format(Math.round(Float.parseFloat(temp)) + "%s", " ℃");
                    String parsedTempFahrenheit = String.format(Math.round(Float.parseFloat(temp)) + "%s", " °F");

                    if (sharedPreferences.getString(TEMPERATURE_KEY, "").equals("metric")) {
                        if (sharedPreferences.getString(WIND_KEY, "").equals("km/h")) {
                            windSpeed = converMetersToKmWindSpeed(windSpeed);
                        } else if (sharedPreferences.getString(WIND_KEY, "").equals("mil/h")) {
                            windSpeed = convertMetersToMilesSpeed(windSpeed);
                        }
                        weather = new Weather(country, cityName, windSpeed, parsedTempCelcius, humidity, description);
                    } else if (sharedPreferences.getString(TEMPERATURE_KEY, "").equals("imperial")) {
                        if (sharedPreferences.getString(WIND_KEY, "").equals("km/h")) {
                            windSpeed = convertMilesToKmSpeed(windSpeed);
                        } else if (sharedPreferences.getString(WIND_KEY, "").equals("mil/h")) {
                            windSpeed = String.format(Locale.US, "%.0f %s", Double.parseDouble(windSpeed), sharedPreferences.getString(WIND_KEY, ""));
                        }
                        weather = new Weather(country, cityName, windSpeed, parsedTempFahrenheit, humidity, description);
                    }


                    if (weatherList.size() < 1) {
                        weatherList.add(0, weather);
                    } else if (weatherList.size() == 1) {
                        weatherList.set(0, weather);
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
            progress.dismiss();
            saveArrayListSharedPreferences(weatherList, SAVING_KEY);

            weatherAdapter = new WeatherAdapter(MainActivity.this, weatherList);
            listView.setAdapter(weatherAdapter);

            updateWidget();
        }
    }


    private class RefreshDownloadData extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        String newUrlAddress = String.format(urlAddress, city, language, temperatureValue);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Refreshing weather");
            progress.setCancelable(false);
            progress.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            if (!weatherList.isEmpty()) {
                String jsonString = new DownloadAllData().sendQuery(newUrlAddress);
                if (jsonString != null) {
                    try {
                        JSONObject json = new JSONObject(jsonString);
                        JSONArray weatherData = json.getJSONArray("list");
                        JSONObject info = weatherData.getJSONObject(0);
                        JSONObject main = info.getJSONObject("main");
                        String temp = main.getString("temp");
                        String humidity = main.getString("humidity");

                        JSONArray weatherDescriptionList = info.getJSONArray("weather");
                        JSONObject weatherDescription = weatherDescriptionList.getJSONObject(0);
                        String description = weatherDescription.getString("description");

                        JSONObject city = json.getJSONObject("city");
                        String country = city.getString("country");
                        String cityName = city.getString("name");


                        JSONObject wind = info.getJSONObject("wind");
                        String windSpeed = wind.getString("speed");

                        String parsedTempCelcius = String.format(Math.round(Float.parseFloat(temp)) + "%s", " ℃");
                        String parsedTempFahrenheit = String.format(Math.round(Float.parseFloat(temp)) + "%s", " °F");

                        if (sharedPreferences.getString(TEMPERATURE_KEY, "").equals("metric")) {
                            if (sharedPreferences.getString(WIND_KEY, "").equals("km/h")) {
                                windSpeed = converMetersToKmWindSpeed(windSpeed);
                            } else if (sharedPreferences.getString(WIND_KEY, "").equals("mil/h")) {
                                windSpeed = convertMetersToMilesSpeed(windSpeed);
                            }
                            weather = new Weather(country, cityName, windSpeed, parsedTempCelcius, humidity, description);
                        } else if (sharedPreferences.getString(TEMPERATURE_KEY, "").equals("imperial")) {
                            if (sharedPreferences.getString(WIND_KEY, "").equals("km/h")) {
                                windSpeed = convertMilesToKmSpeed(windSpeed);
                            } else if (sharedPreferences.getString(WIND_KEY, "").equals("mil/h")) {
                                windSpeed = String.format(Locale.US, "%.0f %s", Double.parseDouble(windSpeed), sharedPreferences.getString(WIND_KEY, ""));
                            }
                            weather = new Weather(country, cityName, windSpeed, parsedTempFahrenheit, humidity, description);
                        }

                        weatherList.set(0, weather);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            saveArrayListSharedPreferences(weatherList, SAVING_KEY);


            if (!weatherList.isEmpty()) {
                weatherAdapter = new WeatherAdapter(MainActivity.this, weatherList);
                listView.setAdapter(weatherAdapter);
                updateWidget();
            }
        }
    }
}

