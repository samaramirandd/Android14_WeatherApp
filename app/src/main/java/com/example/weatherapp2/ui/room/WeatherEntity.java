package com.example.weatherapp2.ui.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather")
public class WeatherEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String location;
    private double tempMin;
    private double tempMax;

    public WeatherEntity(String location, double tempMin, double tempMax) {
        this.location = location;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }
}
