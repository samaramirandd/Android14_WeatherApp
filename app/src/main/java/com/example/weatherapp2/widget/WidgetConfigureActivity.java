package com.example.weatherapp2.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.weatherapp2.R;

import java.util.Arrays;
import java.util.List;

/**
 * Activity para configurar o widget de clima.
 * Permite ao usuário selecionar uma localização para o widget exibir o clima.
 */
public class WidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.weatherapp2.widget.WeatherWidgetProvider";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    ListView listView;
    List<String> locations = Arrays.asList(
            "Lisbon", "Porto", "Funchal", "Ponta Delgada", "Braga", "Faro", "Setúbal",
            "Coimbra", "Madrid,es", "Paris,fr", "Barcelona,es", "Rome,it", "London,uk",
            "Berlin,de", "Amsterdam,nl", "Vienna,at", "Prague,cz", "Warsaw,pl",
            "Brussels,be", "Zurich,ch", "Geneva,ch", "Stockholm,se", "Oslo,no",
            "Helsinki,fi", "Copenhagen,dK", "Dublin,ie", "Edinburgh,uk", "Glasgow,uk",
            "Luxembourg,lu", "Monaco", "Malta", "San Marino", "Andorra", "Liechtenstein",
            "Vilnius,lt", "Riga,lv", "Tallinn,ee", "Bucharest,ro", "Sofia,bg", "Budapest,hu",
            "Ljubljana,si", "Zagreb,hr", "Dubrovnik,hr", "Sarajevo,ba", "Belgrade,rs",
            "Skopje,mk", "Tirana,al", "Athens,gr", "Istanbul,tr", "Dubai,ae", "Doha,qa");

    /**
     * Método chamado quando a Activity é criada.
     * Configura o layout e o comportamento da Activity, incluindo a configuração do ListView
     * e o listener para quando um item é clicado.
     *
     * @param icicle Um Bundle que contém o estado salvo da Activity, se houver.
     */
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Define o resultado padrão para cancelar
        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_configure);

        // Obtém o appWidgetId do Intent que lançou essa Activity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // Se isso não for parte de um pedido de widget válido, encerre a Activity.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        listView = findViewById(R.id.location_list);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locations));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Método chamado quando um item do ListView é clicado.
             * Salva a localização selecionada em SharedPreferences e atualiza o widget.
             *
             * @param parent   O AdapterView onde o clique ocorreu.
             * @param view     A visão clicada.
             * @param position A posição do item clicado.
             * @param id       O ID do item clicado.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Context context = WidgetConfigureActivity.this;

                // Quando um local é clicado, salva a escolha em SharedPreferences
                String location = locations.get(position);
                saveLocationPref(context, appWidgetId, location);

                // Atualiza o widget após a configuração
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                WeatherWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId);

                // Define o resultado de OK
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
    }

    /**
     * Salva a localização selecionada em SharedPreferences.
     *
     * @param context     O contexto em que as SharedPreferences são acessadas.
     * @param appWidgetId O ID do widget para o qual a localização é salva.
     * @param location    A localização selecionada a ser salva.
     */
    static void saveLocationPref(Context context, int appWidgetId, String location) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, location);
        prefs.apply();
    }

    /**
     * Obtém a localização salva de SharedPreferences.
     *
     * @param context     O contexto em que as SharedPreferences são acessadas.
     * @param appWidgetId O ID do widget para o qual a localização é carregada.
     * @return A localização salva, ou um valor padrão ("Lisbon") se não houver nenhuma localização salva.
     */
    static String loadLocationPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY + appWidgetId, "Lisbon");
    }

    /**
     * Remove a localização salva de SharedPreferences.
     *
     * @param context     O contexto em que as SharedPreferences são acessadas.
     * @param appWidgetId O ID do widget para o qual a localização é removida.
     */
    static void deleteLocationPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }
}
