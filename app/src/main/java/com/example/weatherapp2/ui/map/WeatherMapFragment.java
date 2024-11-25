package com.example.weatherapp2.ui.map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.weatherapp2.R;
import com.example.weatherapp2.ui.retrofit.RetrofitClient;
import com.example.weatherapp2.ui.retrofit.WeatherApiService;
import com.example.weatherapp2.ui.retrofit.response.WeatherResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragmento que exibe um mapa com a capacidade de visualizar e selecionar localizações.
 * O mapa é inicializado com uma localização padrão e pode buscar e exibir informações meteorológicas
 * com base na localização clicada.
 */
public class WeatherMapFragment extends Fragment {

    private GoogleMap mMap;
    private Marker currentMarker;
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String PREFS_NAME = "weatherApp";
    private static final String API_KEY = "6285a2ad6dbc06206791b13cdb864c27";

    /**
     * Infla a visualização para este fragmento.
     *
     * @param inflater O {@link LayoutInflater} para inflar a visualização.
     * @param container O contêiner pai que contém a visualização do fragmento.
     * @param savedInstanceState O estado salvo, se disponível.
     * @return A visualização inflada para este fragmento.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla o layout para este fragmento
        return inflater.inflate(R.layout.fragment_weather_map, container, false);
    }

    /**
     * Configura o fragmento do mapa e define o comportamento quando o mapa está pronto.
     *
     * @param view A visualização raiz do fragmento.
     * @param savedInstanceState O estado salvo, se disponível.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtém o fragmento do mapa e configura o callback para quando o mapa estiver pronto
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(mapReadyCallback);
        }
    }

    /**
     * Callback chamado quando o mapa está pronto para ser usado.
     */
    private final OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            // Define o tipo de mapa como híbrido
            LatLng isep = new LatLng(41.17846776626829, -8.608932781350758);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            // Recupera a última localização salva nas preferências
            SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
            double lastLatitude = Double.longBitsToDouble(preferences.getLong(KEY_LATITUDE, Double.doubleToLongBits(41.17846776626829))); // Default para ISEP
            double lastLongitude = Double.longBitsToDouble(preferences.getLong(KEY_LONGITUDE, Double.doubleToLongBits(-8.608932781350758))); // Default para ISEP
            LatLng lastLocation = new LatLng(lastLatitude, lastLongitude);

            // Adiciona um marcador na última localização e define a posição da câmera
            currentMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Last clicked location"));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(lastLocation)  // Define o centro do mapa
                    .zoom(17)              // Define o nível de zoom
                    .bearing(90)           // Define a orientação da câmera para o leste
                    .tilt(30)              // Define a inclinação da câmera para 30 graus
                    .build();              // Cria uma CameraPosition a partir do construtor
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            // Configura o ouvinte de clique no mapa
            mMap.setOnMapClickListener(latLng -> {
                // Remove o marcador atual, se houver
                if (currentMarker != null) {
                    currentMarker.remove();
                }

                // Adiciona um novo marcador na localização clicada
                currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Clicked location"));

                // Salva a nova localização nas preferências
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(KEY_LATITUDE, Double.doubleToLongBits(latLng.latitude));
                editor.putLong(KEY_LONGITUDE, Double.doubleToLongBits(latLng.longitude));
                editor.apply();

                // Busca informações meteorológicas para a nova localização
                fetchWeather(latLng.latitude, latLng.longitude);
            });
        }
    };

    /**
     * Busca informações meteorológicas para uma localização específica e exibe os resultados em um Toast.
     *
     * @param latitude A latitude da localização.
     * @param longitude A longitude da localização.
     */
    private void fetchWeather(double latitude, double longitude) {
        WeatherApiService apiService = RetrofitClient.getClient(BASE_URL).create(WeatherApiService.class);
        Call<WeatherResponse> call = apiService.getWeather(latitude, longitude, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    String weatherInfo = String.format("Current temperature: %.1f°C", weather.main.temp);
                    Toast.makeText(getActivity(), weatherInfo, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to get weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
