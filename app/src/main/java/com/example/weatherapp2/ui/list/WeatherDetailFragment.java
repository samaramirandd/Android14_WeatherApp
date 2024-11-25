package com.example.weatherapp2.ui.list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp2.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Atividade que exibe detalhes do clima para uma localização específica.
 * Estende {@link AppCompatActivity} para fornecer uma tela detalhada com previsões do clima.
 */
public class WeatherDetailFragment extends AppCompatActivity {

    private TextView locationTextView;
    private TextView detailsTextView;
    private String location;

    /**
     * Inicializa a atividade. Configura a interface do usuário e inicia a busca pelos detalhes do clima.
     *
     * @param savedInstanceState O estado salvo da atividade, se disponível.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_weather_detail);

        locationTextView = findViewById(R.id.location);
        detailsTextView = findViewById(R.id.details);

        // Recebe a localização do Intent
        location = getIntent().getStringExtra("location");
        locationTextView.setText(location);

        // Busca os detalhes do clima
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + location + ",pt&appid=6285a2ad6dbc06206791b13cdb864c27&units=metric";
        new FetchWeatherDetailTask().execute(url);
    }

    /**
     * Tarefa assíncrona para buscar detalhes do clima da API e processar os resultados.
     */
    private class FetchWeatherDetailTask extends AsyncTask<String, Void, List<String>> {

        /**
         * Executa a tarefa em segundo plano para buscar os detalhes do clima.
         *
         * @param urls A URL da API para buscar os dados do clima.
         * @return Uma lista de strings contendo detalhes do clima.
         */
        @Override
        protected List<String> doInBackground(String... urls) {
            List<String> result = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
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
                for (int i = 0; i < list.length(); i += 8) { // Pega o clima de cada dia
                    JSONObject dayForecast = list.getJSONObject(i);
                    JSONObject main = dayForecast.getJSONObject("main");
                    double tempMin = main.getDouble("temp_min");
                    double tempMax = main.getDouble("temp_max");
                    String date = dayForecast.getString("dt_txt").split(" ")[0];
                    result.add(date + ": Min: " + tempMin + "°C, Max: " + tempMax + "°C");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        /**
         * Atualiza a interface do usuário com os detalhes do clima após a tarefa em segundo plano ser concluída.
         *
         * @param details A lista de strings contendo detalhes do clima.
         */
        @Override
        protected void onPostExecute(List<String> details) {
            if (details != null && !details.isEmpty()) {
                StringBuilder detailsText = new StringBuilder();
                for (String detail : details) {
                    detailsText.append(detail).append("\n");
                }
                detailsTextView.setText(detailsText.toString());
            }
        }
    }
}
