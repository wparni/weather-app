package com.example.arni.weatherapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


public class WeatherWidget extends AppWidgetProvider {
    private RemoteViews remoteViews;

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
            if (!MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").isEmpty()) {
                String tempCelcius = intent.getStringExtra(MainActivity.TEMPERATURE_KEY);
                String tempFahrenheit = intent.getStringExtra(MainActivity.TEMPERATURE_KEY);


                remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget_layout);
                String celcius = "metric";
                String fahrenheit = "imperial";
                if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals(celcius)) {
                    remoteViews.setTextViewText(R.id.text_view_widget_temperature, tempCelcius);
                } else if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals(fahrenheit)) {
                    remoteViews.setTextViewText(R.id.text_view_widget_temperature, tempFahrenheit);
                }
            }
        }

        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, WeatherWidget.class), remoteViews);
    }
}

