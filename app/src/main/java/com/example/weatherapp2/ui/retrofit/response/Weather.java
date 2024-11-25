package com.example.weatherapp2.ui.retrofit.response;
public class Weather {
    private String location;
    private double tempMin;
    private double tempMax;

    public Weather(String location, double tempMin, double tempMax) {
        this.location = location;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public String getLocation() {
        return location;
    }

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }
}
