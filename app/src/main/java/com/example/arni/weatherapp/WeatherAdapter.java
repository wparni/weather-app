package com.example.arni.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Arni on 2017-08-19.
 */

class WeatherAdapter extends ArrayAdapter {


    private Context context;
    private List<Weather> weatherList;
    private TextView temperature;
    private TextView location;
    private TextView humidity;
    private TextView weatherDescription;
    private TextView windSpeed;
    private String celcius = "metric";
    private String fahrenheit = "imperial";

    public WeatherAdapter(Context context, List<Weather> listOfWeatherSpecs) {
        super(context, 0, listOfWeatherSpecs);
        this.context = context;
        this.weatherList = listOfWeatherSpecs;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View weatherInfoView = inflater.inflate(R.layout.weather_info, parent, false);

        temperature = (TextView) weatherInfoView.findViewById(R.id.temperature);
        humidity = (TextView) weatherInfoView.findViewById(R.id.humidity);
        location = (TextView) weatherInfoView.findViewById(R.id.location);
        weatherDescription = (TextView) weatherInfoView.findViewById(R.id.weather_description);
        windSpeed = (TextView) weatherInfoView.findViewById(R.id.wind_speed);


        Weather weather = weatherList.get(position);

        if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals(celcius)) {
            temperature.setText(weather.getTemperature());
        }else if(MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals(fahrenheit)){
            temperature.setText(weather.getTemperature());
        }
        location.setText(weather.getCity());
        humidity.setText(weather.getHumidity());
        weatherDescription.setText(weather.getDescription());
        windSpeed.setText(weather.getWind());



        return weatherInfoView;
    }
}
