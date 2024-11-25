package com.example.weatherapp2.ui.list;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherapp2.ui.room.AppDatabase;
import com.example.weatherapp2.R;
import com.example.weatherapp2.ui.retrofit.response.Weather;
import com.example.weatherapp2.ui.room.WeatherEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragmento que exibe uma lista de previsões do clima.
 * Estende {@link Fragment} para fornecer uma lista de previsões do clima com base nas localizações fornecidas.
 */
public class WeatherListFragment extends Fragment {

    private ListView weatherListView;
    private WeatherAdapter adapter;
    private List<Weather> weatherList;
    private List<String> locations;
    private AppDatabase appDatabase;

    /**
     * Infla a visualização do fragmento e configura o adaptador da lista.
     * Inicializa a lista de localizações e inicia a busca dos dados do clima.
     *
     * @param inflater           O {@link LayoutInflater} para inflar a visualização.
     * @param container          O contêiner no qual o fragmento será inserido.
     * @param savedInstanceState O estado salvo do fragmento, se disponível.
     * @return A visualização do fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather_list, container, false);

        weatherListView = rootView.findViewById(R.id.weather_list);
        weatherList = new ArrayList<>();
        adapter = new WeatherAdapter(getActivity(), weatherList);
        weatherListView.setAdapter(adapter);

        // Configura o clique na lista para abrir detalhes do clima
        weatherListView.setOnItemClickListener((parent, view, position, id) -> {
            Weather selectedWeather = (Weather) adapter.getItem(position);

            // Salva os dados no banco de dados
            new SaveWeatherTask().execute(selectedWeather);

            Intent intent = new Intent(getActivity(), WeatherDetailFragment.class);
            intent.putExtra("location", selectedWeather.getLocation());
            startActivity(intent);
        });

        // Lista de localizações para buscar o clima
        locations = Arrays.asList(
                "Lisbon", "Porto", "Funchal", "Ponta Delgada", "Braga", "Faro", "Setúbal",
                "Coimbra", "Madrid,es", "Paris,fr", "Barcelona,es", "Rome,it", "London,uk",
                "Berlin,de", "Amsterdam,nl", "Vienna,at", "Prague,cz", "Warsaw,pl",
                "Brussels,be", "Zurich,ch", "Geneva,ch", "Stockholm,se", "Oslo,no",
                "Helsinki,fi", "Copenhagen,dK", "Dublin,ie", "Edinburgh,uk", "Glasgow,uk",
                "Luxembourg,lu", "Monaco", "Malta", "San Marino", "Andorra", "Liechtenstein",
                "Vilnius,lt", "Riga,lv", "Tallinn,ee", "Bucharest,ro", "Sofia,bg", "Budapest,hu",
                "Ljubljana,si", "Zagreb,hr", "Dubrovnik,hr", "Sarajevo,ba", "Belgrade,rs",
                "Skopje,mk", "Tirana,al", "Athens,gr", "Istanbul,tr", "Dubai,ae", "Doha,qa"
        );

        // Cria URLs para cada localização
        List<String> urls = new ArrayList<>();
        for (String location : locations) {
            String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + location + "&appid=6285a2ad6dbc06206791b13cdb864c27&units=metric";
            urls.add(url);
        }

        // Inicia a tarefa para buscar os dados do clima
        new FetchWeatherTask().execute(urls.toArray(new String[0]));

        // Inicializa o banco de dados
        appDatabase = AppDatabase.getDatabase(getContext());

        return rootView;
    }

    /**
     * Tarefa assíncrona para buscar os dados do clima da API.
     * Processa as URLs fornecidas para obter as previsões do clima.
     */
    private class FetchWeatherTask extends AsyncTask<String, Void, List<Weather>> {

        /**
         * Executa a tarefa em segundo plano para buscar os dados do clima.
         *
         * @param urls As URLs para buscar os dados do clima.
         * @return Uma lista de objetos {@link Weather} com os dados do clima.
         */
        @Override
        protected List<Weather> doInBackground(String... urls) {
            List<Weather> result = new ArrayList<>();
            for (String urlStr : urls) {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }
                    reader.close();

                    JSONObject jsonObject = new JSONObject(json.toString());
                    JSONArray list = jsonObject.getJSONArray("list");
                    if (list.length() > 0) {
                        JSONObject dayForecast = list.getJSONObject(0); // Usa o primeiro item para obter o clima atual
                        JSONObject main = dayForecast.getJSONObject("main");
                        double tempMin = main.getDouble("temp_min");
                        double tempMax = main.getDouble("temp_max");
                        String location = jsonObject.getJSONObject("city").getString("name");

                        result.add(new Weather(location, tempMin, tempMax));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        /**
         * Atualiza a interface do usuário com os dados do clima após a tarefa em segundo plano ser concluída.
         *
         * @param weathers A lista de objetos {@link Weather} com os dados do clima.
         */
        @Override
        protected void onPostExecute(List<Weather> weathers) {
            if (weathers != null && !weathers.isEmpty()) {
                weatherList.clear();
                weatherList.addAll(weathers);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Tarefa assíncrona para salvar os dados do clima no banco de dados.
     */
    private class SaveWeatherTask extends AsyncTask<Weather, Void, Void> {

        /**
         * Executa a tarefa em segundo plano para salvar os dados do clima no banco de dados.
         *
         * @param weathers Os objetos {@link Weather} a serem salvos no banco de dados.
         * @return Null.
         */
        @Override
        protected Void doInBackground(Weather... weathers) {
            for (Weather weather : weathers) {
                WeatherEntity weatherEntity = new WeatherEntity(weather.getLocation(), weather.getTempMin(), weather.getTempMax());
                appDatabase.weatherDao().insert(weatherEntity);
            }
            return null;
        }
    }
}
