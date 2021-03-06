package com.example.arni.weatherapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ForeCast24h extends Activity implements ConvertingWindUnits, IValueFormatter {
    private Weather weather;
    static List<Weather> weatherForecast24h = new ArrayList<>();
    private LineChart lineChart;
    private ArrayList<Entry> valueX = new ArrayList<>();
    private ArrayList<String> valueY = new ArrayList<>();
    boolean connected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast);
        lineChart = (LineChart) findViewById(R.id.line_chart);
        lineChart.setNoDataText("Downloading data");

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


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkOn = cm.getActiveNetworkInfo();
            if (networkOn != null) {
                connected = true;
                new DownloadDataForecast24h().execute();
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            } else {
                connected = false;
                Toast.makeText(context, "Can't download data, no internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    };

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



    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals("metric")) {
            return String.format((int) value + "%s", " ℃");
        } else if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals("imperial")) {
            return String.format((int) value + "%s", " °F");
        }
        return "" + (int) value;
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


                        String hour = info.getString("dt_txt");


                        String parsedTempCelcius = String.valueOf(Math.round(Float.parseFloat(temp)));
                        String parsedTempFahrenheit = String.valueOf(Math.round(Float.parseFloat(temp)));

                        if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals("metric")) {
                            if (MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "").equals("km/h")) {
                                windSpeed = converMetersToKmWindSpeed(windSpeed);
                            } else if (MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "").equals("mil/h")) {
                                windSpeed = convertMetersToMilesSpeed(windSpeed);
                            }
                            weather = new Weather(parsedTempCelcius, description, windSpeed, hour);
                        } else if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals("imperial")) {
                            if (MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "").equals("km/h")) {
                                windSpeed = convertMilesToKmSpeed(windSpeed);
                            } else if (MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, "").equals("mil/h")) {
                                windSpeed = String.format(Locale.US, "%.0f %s", Double.parseDouble(windSpeed), MainActivity.sharedPreferences.getString(MainActivity.WIND_KEY, ""));
                            }
                            weather = new Weather(parsedTempFahrenheit, description, windSpeed, hour);
                        }
                        if (weatherForecast24h.size() < 8) {
                            weatherForecast24h.add(weather);
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

            for (int i = 0; i < weatherForecast24h.size(); i++) {
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(weatherForecast24h.get(i).getDate());
                valueY.add(timestamp.toString());
                valueX.add(new Entry(i, Float.valueOf(weatherForecast24h.get(i).getTemperature())));
            }

            LineDataSet lineData = new LineDataSet(valueX, "label");
            lineData.setLineWidth(4f);
            lineData.setCircleRadius(6f);
            lineData.setColor(getColor(R.color.colorPrimary));
            lineData.setCircleColor(getColor(R.color.colorPrimaryDark));
            lineData.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineData.setValueTextSize(13);
            lineData.setValueFormatter(new ForeCast24h());

            LineData data = new LineData(lineData);

            lineChart.setData(data);
            lineChart.setDrawBorders(false);
            lineChart.setDrawGridBackground(false);

            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.setEnabled(false);
            YAxis rightAxis = lineChart.getAxisRight();
            rightAxis.setEnabled(false);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new DefaultAxisValueFormatter(5) {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return valueY.get((int) value % valueY.size()).substring(11, 16) + "h";
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            });

            Legend legend = lineChart.getLegend();
            legend.setEnabled(false);
            lineChart.getDescription().setEnabled(true);
            lineChart.getDescription().setText("24h forecast");
            lineChart.setVisibleYRangeMaximum(40, YAxis.AxisDependency.LEFT);
            lineChart.invalidate();


            weatherForecast24h.clear();
        }
    }
}
