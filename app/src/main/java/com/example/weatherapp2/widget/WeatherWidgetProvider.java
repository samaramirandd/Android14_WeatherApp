package com.example.weatherapp2.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import com.example.weatherapp2.MainActivity;
import com.example.weatherapp2.R;
import com.example.weatherapp2.ui.retrofit.response.Weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fornece o provedor de widgets para o widget de clima.
 * Atualiza o widget com as informações do clima para a localização configurada.
 */
public class WeatherWidgetProvider extends AppWidgetProvider {

    /**
     * Atualiza todos os widgets configurados.
     * Este método é chamado pelo sistema quando o widget precisa ser atualizado.
     *
     * @param context            O contexto da aplicação.
     * @param appWidgetManager   O gerenciador de widgets usado para atualizar o widget.
     * @param appWidgetIds       Os IDs dos widgets a serem atualizados.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Atualiza todos os widgets
        for (int appWidgetId : appWidgetIds) {
            // Carregar localização configurada ou usar padrão
            String location = WidgetConfigureActivity.loadLocationPref(context, appWidgetId);
            new FetchWeatherTask(context, appWidgetManager, appWidgetId).execute(location);
        }
    }

    /**
     * Atualiza o widget com os dados do clima.
     * Este método é chamado quando o widget precisa ser atualizado, por exemplo, após a configuração.
     *
     * @param context            O contexto da aplicação.
     * @param appWidgetManager   O gerenciador de widgets usado para atualizar o widget.
     * @param appWidgetId        O ID do widget a ser atualizado.
     */
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Carregar localização configurada ou usar padrão
        String location = WidgetConfigureActivity.loadLocationPref(context, appWidgetId);
        new FetchWeatherTask(context, appWidgetManager, appWidgetId).execute(location);
    }

    /**
     * Tarefa assíncrona para buscar as informações do clima da API e atualizar o widget.
     */
    private static class FetchWeatherTask extends AsyncTask<String, Void, Weather> {
        private Context context;
        private AppWidgetManager appWidgetManager;
        private int appWidgetId;

        /**
         * Construtor da tarefa assíncrona.
         *
         * @param context            O contexto da aplicação.
         * @param appWidgetManager   O gerenciador de widgets usado para atualizar o widget.
         * @param appWidgetId        O ID do widget a ser atualizado.
         */
        public FetchWeatherTask(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetId = appWidgetId;
        }

        /**
         * Busca os dados do clima em segundo plano.
         *
         * @param params O(s) parâmetro(s) de entrada, onde o primeiro parâmetro é a localização.
         * @return O objeto {@link Weather} com as informações do clima, ou null se ocorrer um erro.
         */
        @Override
        protected Weather doInBackground(String... params) {
            String location = params[0];
            String apiKey = "6285a2ad6dbc06206791b13cdb864c27";
            String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + location + "&appid=" + apiKey + "&units=metric";

            try {
                URL url = new URL(apiUrl);
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
                    JSONObject dayForecast = list.getJSONObject(0); // Pega o primeiro item para o clima atual
                    JSONObject main = dayForecast.getJSONObject("main");
                    double tempMin = main.getDouble("temp_min");
                    double tempMax = main.getDouble("temp_max");
                    String cityName = jsonObject.getJSONObject("city").getString("name");

                    return new Weather(cityName, tempMin, tempMax);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Atualiza o widget com os dados do clima após a tarefa assíncrona ser concluída.
         *
         * @param weather O objeto {@link Weather} com as informações do clima.
         */
        @Override
        protected void onPostExecute(Weather weather) {
            if (weather != null) {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);

                // Atualizar o widget com os dados do clima
                views.setTextViewText(R.id.widget_location, weather.getLocation());
                views.setTextViewText(R.id.widget_temp_min, String.format("Min: %.1f°C", weather.getTempMin()));
                views.setTextViewText(R.id.widget_temp_max, String.format("Max: %.1f°C", weather.getTempMax()));

                // Configurar um clique no widget para abrir o MainActivity
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.widget_location, pendingIntent);

                // Atualizar o widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }
}
