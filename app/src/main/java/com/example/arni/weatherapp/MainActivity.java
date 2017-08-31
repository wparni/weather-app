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
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ConvertingWindUnits {

    static String urlAddress = "http://api.openweathermap.org/data/2.5/forecast?q=%s&lang=%s&units=%s&APPID=909d0a4d34bbefca53de0afe4fdf5fde";
    private String urlAdressForUvIndex = "http://api.openweathermap.org/data//2.5/uvi?lat=%s&lon=%s&APPID=909d0a4d34bbefca53de0afe4fdf5fde";
    private String urlAddressForSunRiseSet = "http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=909d0a4d34bbefca53de0afe4fdf5fde";
    static final String TEMPERATURE_KEY = "temperature";
    static final String WIND_KEY = "wind";
    static final String LANGUAGE_KEY = "language";
    static final String LOCATION_KEY = "location";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String LATITUDE_KEY = "latitude";
    private static final String SAVING_KEY = "saving_array";
    static final String SUNRISE_KEY = "sunrise";
    static final String SUNSET_KEY = "sunset";
    static final String UV_KEY = "uv_value";
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
    private String longitude;
    private String latitude;
    private String wind;
    private String uvValue;
    private String sunset;
    private String sunrise;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);

        listView = (ListView) findViewById(R.id.list_view);
        linearLayout = (LinearLayout) findViewById(R.id.main_activity);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        city = sharedPreferences.getString(LOCATION_KEY, "");
        language = sharedPreferences.getString(LANGUAGE_KEY, "");
        temperatureValue = sharedPreferences.getString(TEMPERATURE_KEY, "");
        latitude = sharedPreferences.getString(LATITUDE_KEY, "");
        longitude = sharedPreferences.getString(LONGITUDE_KEY, "");
        wind = sharedPreferences.getString(WIND_KEY, "");
        uvValue = sharedPreferences.getString(UV_KEY, "");

        sunset = sharedPreferences.getString(SUNSET_KEY, "");
        sunrise = sharedPreferences.getString(SUNRISE_KEY, "");

        if (getSupportActionBar() != null && !city.isEmpty()) {
            getSupportActionBar().setTitle(city);
        } else if (city.isEmpty() && language.equals("eng")) {
            getSupportActionBar().setTitle("Set city");
        } else if (city.isEmpty() && language.equals("pl")) {
            getSupportActionBar().setTitle("Ustaw miasto");
        }

        changeBackground(linearLayout);

        weatherAdapter = new WeatherAdapter(this, weatherList);
        listView.setAdapter(weatherAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    Intent intent = new Intent(MainActivity.this, ForeCast24h.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeather();
            }
        });


    }

    void changeBackground(LinearLayout linearLayout) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
        Date currentTime = Calendar.getInstance().getTime();
        String actualHour = dateFormat.format(currentTime);
        int parsedActualHour = Integer.valueOf(actualHour.replaceAll("[^\\d]", ""));
        int parsedSunriseHour = Integer.valueOf(sunrise.replaceAll("[^\\d]", ""));
        int parsedSunsetHour = Integer.valueOf(sunset.replaceAll("[^\\d]", ""));

        if (parsedActualHour > parsedSunriseHour && parsedActualHour <= parsedSunsetHour && !sunrise.isEmpty() && !sunset.isEmpty()) {
            linearLayout.setBackgroundColor(getColor(R.color.colorLightBlue));
        }else{
            linearLayout.setBackgroundColor(getColor(R.color.colorDarkBlue));
        }

    }

    public String convertKmtoMilesSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) / 1.61)) + wind;
    }

    public String convertMilesToKmSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) * 1.61)) + wind;
    }

    public String convertMetersToMilesSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) * 2.24)) + wind;
    }

    public String converMetersToKmWindSpeed(String windValue) {
        windValue = windValue.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(windValue) * 3.6)) + wind;

    }

    public String convertToFahrenheit(String actualTemp) {
        actualTemp = actualTemp.replaceAll("[^\\d.]", "");
        return String.valueOf(String.format(Locale.US, "%.0f", Double.parseDouble(actualTemp) * 1.8 + 32)) + " °F";

    }

    public String convertToCelcius(String actualTemp) {
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
            if (wind.equals("km/h") && !windValue.contains("km/h")) {
                weatherList.get(0).setWind(convertMilesToKmSpeed(windValue));
            } else if (wind.equals("mil/h") && !windValue.contains("mil/h")) {
                weatherList.get(0).setWind(convertKmtoMilesSpeed(windValue));
            }
            if (temperatureValue.equals("metric") && !actualTemp.contains("℃")) {
                weatherList.get(0).setTemperature(convertToCelcius(actualTemp));
            } else if (temperatureValue.equals("imperial") && !actualTemp.contains("°F")) {
                weatherList.get(0).setTemperature(convertToFahrenheit(actualTemp));
            }

            saveArrayListSharedPreferences(weatherList, SAVING_KEY);
        }
        weatherAdapter = new WeatherAdapter(MainActivity.this, weatherList);
        listView.setAdapter(weatherAdapter);

    }


    private void getWeatherDataOnline() {
        if (!city.isEmpty() && !language.isEmpty()) {
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
                                          if (!weatherList.isEmpty()) {
                                              new DownloadData().execute();
                                          } else if (weatherList.isEmpty()) {
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
                    String pressure = main.getString("pressure");


                    JSONArray weatherDescriptionList = info.getJSONArray("weather");
                    JSONObject weatherDescription = weatherDescriptionList.getJSONObject(0);
                    String description = weatherDescription.getString("description");


                    JSONObject city = json.getJSONObject("city");
                    String country = city.getString("country");
                    String cityName = city.getString("name");

                    JSONObject coord = city.getJSONObject("coord");
                    longitude = coord.getString("lon");
                    latitude = coord.getString("lat");

                    sharedPreferences.edit().putString(LONGITUDE_KEY, longitude).putString(LATITUDE_KEY, latitude).apply();

                    JSONObject windObject = info.getJSONObject("wind");
                    String windSpeed = windObject.getString("speed");


                    String parsedTempCelcius = String.format(Math.round(Float.parseFloat(temp)) + "%s", " ℃");
                    String parsedTempFahrenheit = String.format(Math.round(Float.parseFloat(temp)) + "%s", " °F");

                    if (temperatureValue.equals("metric")) {
                        if (wind.equals("km/h")) {
                            windSpeed = converMetersToKmWindSpeed(windSpeed);
                        } else if (wind.equals("mil/h")) {
                            windSpeed = convertMetersToMilesSpeed(windSpeed);
                        }
                        weather = new Weather(country, cityName, windSpeed, parsedTempCelcius, humidity, description, longitude, latitude, pressure);
                    } else if (temperatureValue.equals("imperial")) {
                        if (wind.equals("km/h")) {
                            windSpeed = convertMilesToKmSpeed(windSpeed);
                        } else if (wind.equals("mil/h")) {
                            windSpeed = String.format(Locale.US, "%.0f %s", Double.parseDouble(windSpeed), wind);
                        }
                        weather = new Weather(country, cityName, windSpeed, parsedTempFahrenheit, humidity, description, longitude, latitude, pressure);
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
            latitude = sharedPreferences.getString(LATITUDE_KEY, "");
            longitude = sharedPreferences.getString(LONGITUDE_KEY, "");

            if (!latitude.isEmpty() && !longitude.isEmpty()) {
                new DownloadUVvalue().execute();
            }
            new DownloadSunSetRise().execute();

            saveArrayListSharedPreferences(weatherList, SAVING_KEY);

            weatherAdapter = new WeatherAdapter(MainActivity.this, weatherList);
            listView.setAdapter(weatherAdapter);

            updateWidget();
        }
    }

    private class DownloadUVvalue extends AsyncTask<Void, Void, Void> {
        String urlAddressUv = String.format(urlAdressForUvIndex, latitude, longitude);

        @Override
        protected Void doInBackground(Void... params) {

            String jsonString = new DownloadAllData().sendQuery(urlAddressUv);
            if (jsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    String uvValue = jsonObject.getString("value");
                    sharedPreferences.edit().putString(UV_KEY, uvValue).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            weatherAdapter = new WeatherAdapter(MainActivity.this, weatherList);
            listView.setAdapter(weatherAdapter);
        }
    }

    private class DownloadSunSetRise extends AsyncTask<Void, Void, Void> {
        String urlAddressSun = String.format(urlAddressForSunRiseSet, city);

        String convertingUnixTime(String value) {
            Date date = new Date(Long.valueOf(value) * 1000);
            return new SimpleDateFormat("kk:mm ", Locale.ENGLISH).format(date);
        }

        @Override
        protected Void doInBackground(Void... params) {

            String jsonString = new DownloadAllData().sendQuery(urlAddressSun);
            if (jsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject jsonSun = jsonObject.getJSONObject("sys");

                    String sunset = jsonSun.getString("sunset");
                    String sunrise = jsonSun.getString("sunrise");

                    sunset = convertingUnixTime(sunset);
                    sunrise = convertingUnixTime(sunrise);


                    sharedPreferences.edit().putString(SUNSET_KEY, sunset).apply();
                    sharedPreferences.edit().putString(SUNRISE_KEY, sunrise).apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            weatherAdapter = new WeatherAdapter(MainActivity.this, weatherList);
            listView.setAdapter(weatherAdapter);
        }
    }
}

