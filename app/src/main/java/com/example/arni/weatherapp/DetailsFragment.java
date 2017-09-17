package com.example.arni.weatherapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
    TextView pressure;
    TextView uv_index;
    TextView sunset;
    TextView sunrise;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.details_fragment, container, false);

        pressure = (TextView) view.findViewById(R.id.pressure);
        uv_index = (TextView) view.findViewById(R.id.uv_index);
        sunset = (TextView) view.findViewById(R.id.sunset);
        sunrise = (TextView) view.findViewById(R.id.sunrise);


        uv_index.setText(String.format(MainActivity.sharedPreferences.getString(MainActivity.UV_KEY, "") + "%s", " UV"));
        pressure.setText(String.format(MainActivity.sharedPreferences.getString(MainActivity.PRESSURE_KEY, "") + "%s", " hPa"));
        sunrise.setText(MainActivity.sharedPreferences.getString(MainActivity.SUNRISE_KEY, ""));
        sunset.setText(MainActivity.sharedPreferences.getString(MainActivity.SUNSET_KEY, ""));

        return view;
    }
}
