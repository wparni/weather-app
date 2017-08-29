package com.example.arni.weatherapp;

/**
 * Created by Arni on 2017-08-19.
 */

public class Weather {


    private String country;
    private String city;
    private String wind;
    private String temperature;
    private String humidity;
    private String description;
    private String longitude;
    private String latitude;
    private String pressure;

    public Weather(String temperature, String description, String wind){
        this.temperature = temperature;
        this.description = description;
        this.wind = wind;
    }

    public Weather(String country, String city, String wind, String temperature, String humidity, String description, String longitude, String latitude, String pressure) {
        this.country = country;
        this.city = city;
        this.wind = wind;
        this.temperature = temperature;
        this.humidity = humidity;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.pressure = pressure;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
}
