package com.example.weatherapp2.ui.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WeatherDao {
    @Insert
    void insert(WeatherEntity weatherEntity);

    @Query("SELECT * FROM weather WHERE location = :location")
    WeatherEntity getWeatherByLocation(String location);

    @Query("DELETE FROM weather")
    void deleteAll();
}
