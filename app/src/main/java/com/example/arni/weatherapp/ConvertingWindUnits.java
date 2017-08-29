package com.example.arni.weatherapp;

/**
 * Created by Arni on 2017-08-29.
 */

public interface ConvertingWindUnits {

    String convertKmtoMilesSpeed(String windValue);
    String convertMilesToKmSpeed(String windValue);
    String convertMetersToMilesSpeed(String windValue);
    String converMetersToKmWindSpeed(String windValue);
    String convertToFahrenheit(String actualTemp);
    String convertToCelcius(String actualTemp);
}
