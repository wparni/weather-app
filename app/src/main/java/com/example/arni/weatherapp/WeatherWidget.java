package com.example.arni.weatherapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


/**
 * Created by Arni on 2017-08-20.
 */

public class WeatherWidget extends AppWidgetProvider {
    private RemoteViews remoteViews;
    private String celcius = "metric";
    private String fahrenheit = "imperial";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        ComponentName weatherWidget = new ComponentName(context, WeatherWidget.class);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget_layout);
        remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

        appWidgetManager.updateAppWidget(weatherWidget, remoteViews);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);


        if (intent != null) {
            String tempCelcius = intent.getStringExtra(MainActivity.TEMPERATURE_KEY); // + "℃"          DO SPRAWDZENIA
            String tempFahrenheit = intent.getStringExtra(MainActivity.TEMPERATURE_KEY); // + "°F"

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget_layout);
            if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals(celcius)) {
                remoteViews.setTextViewText(R.id.text_view_widget_temperature, tempCelcius);
            } else if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals(fahrenheit)) {
                remoteViews.setTextViewText(R.id.text_view_widget_temperature, tempFahrenheit);
            }
        }

        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, WeatherWidget.class), remoteViews);
    }
}

