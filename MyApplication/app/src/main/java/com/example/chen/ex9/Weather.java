package com.example.chen.ex9;

/**
 * Created by Chen on 2016/11/25.
 */

public class Weather {
    private String date, weather_description, temperature;

    public Weather(String date, String weather, String temperature) {
        this.date = date;
        this.weather_description = weather;
        this.temperature = temperature;
    }

    public String getDate() {
        return date;
    }

    public String getWeather() {
        return weather_description;
    }

    public String getTemperature(){
        return temperature;
    }

}
