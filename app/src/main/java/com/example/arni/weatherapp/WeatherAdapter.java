package com.example.arni.weatherapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class WeatherAdapter extends ArrayAdapter<Weather> {


    private Context context;
    private List<Weather> weatherList;

    WeatherAdapter(Context context, List<Weather> listOfWeatherSpecs) {
        super(context, 0, listOfWeatherSpecs);
        this.context = context;
        this.weatherList = listOfWeatherSpecs;
    }

    private static class ViewHolder {
        TextView temperature;
        TextView humidity;
        TextView location;
        TextView weatherDescription;
        TextView windSpeed;
        TextView pressure;
        TextView uv_index;
        TextView sunset;
        TextView sunrise;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View weatherInfoView = convertView;
        ViewHolder viewHolder = new ViewHolder();

        if (weatherInfoView == null) {
            weatherInfoView = inflater.inflate(R.layout.weather_info, parent, false);
            viewHolder.temperature = (TextView) weatherInfoView.findViewById(R.id.temperature);
            viewHolder.humidity = (TextView) weatherInfoView.findViewById(R.id.humidity);
            viewHolder.location = (TextView) weatherInfoView.findViewById(R.id.location);
            viewHolder.weatherDescription = (TextView) weatherInfoView.findViewById(R.id.weather_description);
            viewHolder.windSpeed = (TextView) weatherInfoView.findViewById(R.id.wind_speed);
            viewHolder.pressure = (TextView) weatherInfoView.findViewById(R.id.pressure);
            viewHolder.uv_index = (TextView) weatherInfoView.findViewById(R.id.uv_index);
            viewHolder.sunset = (TextView) weatherInfoView.findViewById(R.id.sunset);
            viewHolder.sunrise = (TextView) weatherInfoView.findViewById(R.id.sunrise);


            weatherInfoView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) weatherInfoView.getTag();

        Weather weather = weatherList.get(position);


        viewHolder.temperature.setText(weather.getTemperature());
        viewHolder.location.setText(weather.getCity());
        viewHolder.humidity.setText(weather.getHumidity());
        viewHolder.weatherDescription.setText(weather.getDescription());
        viewHolder.windSpeed.setText(weather.getWind());

        viewHolder.uv_index.setText(MainActivity.sharedPreferences.getString(MainActivity.UV_KEY, ""));
        viewHolder.pressure.setText(weather.getPressure());
        viewHolder.sunrise.setText(MainActivity.sharedPreferences.getString(MainActivity.SUNRISE_KEY, ""));
        viewHolder.sunset.setText(MainActivity.sharedPreferences.getString(MainActivity.SUNSET_KEY, ""));


        return weatherInfoView;
    }
}
