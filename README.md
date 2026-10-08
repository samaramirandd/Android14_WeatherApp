# WeatherApp — Android 14

Android weather application developed as part of an Android development course, focused on consuming external APIs, displaying weather information, map integration and Android home-screen widgets.

## Overview

WeatherApp is an Android application that allows users to explore weather information for different locations through a simple and interactive interface.

The project was developed using Android Studio and the Android SDK, with a focus on Android application architecture, REST API integration and asynchronous data retrieval.

## Features

* **Weather list** — Browse weather information for multiple locations.
* **Weather details** — View multi-day weather forecasts for a selected location.
* **Interactive map** — Select a location on a map and retrieve its current weather information.
* **Home-screen widget** — Display current temperature information directly through an Android widget.
* **REST API integration** — Retrieve weather data from OpenWeatherMap using Retrofit.
* **JSON data processing** — Parse and handle weather data received from the external API.
* **Android UI components** — Navigation between activities/fragments and dedicated screens for different weather-related features.

## Architecture

The application is organized into different components according to their responsibilities:

```text
WeatherApp
│
├── Presentation
│   ├── MainActivity
│   ├── WeatherListFragment
│   ├── WeatherDetailFragment
│   └── WeatherMapFragment
│
├── Data
│   ├── Weather
│   ├── WeatherEntity
│   ├── AppDatabase
│   └── WeatherDao
│
├── API Integration
│   ├── RetrofitClient
│   ├── WeatherApiService
│   └── WeatherResponse
│
└── Widget
    ├── WeatherWidgetProvider
    ├── WeatherUpdateService
    └── WidgetConfigureActivity
```

The project follows an MVP-oriented structure, separating presentation logic from data and API-related responsibilities.

## Technologies

* Java
* Android SDK / Android 14
* Android Studio
* Retrofit
* OpenWeatherMap API
* Google Maps
* Room
* Android Fragments
* Android App Widgets
* REST APIs
* JSON

## API Integration

Weather data is retrieved from the **OpenWeatherMap API** through Retrofit.

The application receives JSON responses from the API and maps the relevant information to the application's weather models before displaying it to the user.

## Maps

The application includes an interactive map through `WeatherMapFragment`.

Users can interact with the map to select a location and retrieve weather information associated with that position.

## Android Widget

WeatherApp also includes an Android home-screen widget.

The widget uses:

* `WeatherWidgetProvider`
* `WidgetConfigureActivity`
* `WeatherUpdateService`

to display weather information and support widget configuration.

## Testing

The application was tested using:

* Physical Android devices with different Android versions.
* Android Emulator with different device configurations and screen sizes.

## Current Limitations

Some planned features were only partially implemented in the version submitted for the course:

* Room database persistence was partially implemented but was not enabled in the final version.
* Automatic widget refresh frequency configuration was implemented partially but was not fully completed.

These limitations represent potential areas for future development.

## What I Learned

This project gave me practical experience in Android application development, particularly in:

* Consuming and integrating REST APIs.
* Working with JSON data.
* Building Android interfaces using activities and fragments.
* Integrating Google Maps.
* Developing Android home-screen widgets.
* Structuring an application into presentation, data and API integration components.
* Testing applications across physical devices and emulators.

The project was an important step in developing my understanding of mobile software development and integrating external services into an application.

## Project Context

Academic project developed during an Android development course.

**Author:** Samara Miranda
**Platform:** Android
**Development Environment:** Android Studio
