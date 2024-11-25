package com.example.weatherapp2.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp2.R;
import com.example.weatherapp2.ui.retrofit.response.Weather;

import java.util.List;

/**
 * Adaptador personalizado para exibir uma lista de informações de clima.
 * Extende {@link BaseAdapter} para fornecer uma visão personalizada de cada item na lista.
 */
public class WeatherAdapter extends BaseAdapter {
    private Context context;
    private List<Weather> weatherList;

    /**
     * Construtor do adaptador.
     *
     * @param context       O contexto da aplicação, usado para inflar layouts e acessar recursos.
     * @param weatherList   A lista de objetos {@link Weather} a ser exibida na lista.
     */
    public WeatherAdapter(Context context, List<Weather> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    /**
     * Retorna o número de itens na lista de clima.
     *
     * @return O número de itens na lista {@link #weatherList}.
     */
    @Override
    public int getCount() {
        return weatherList.size();
    }

    /**
     * Retorna o item na posição especificada.
     *
     * @param position A posição do item na lista.
     * @return O objeto {@link Weather} na posição especificada.
     */
    @Override
    public Object getItem(int position) {
        return weatherList.get(position);
    }

    /**
     * Retorna o ID da posição do item na lista.
     *
     * @param position A posição do item na lista.
     * @return O ID da posição do item na lista (neste caso, a posição é retornada como ID).
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Cria e retorna a visualização de um item na lista. Se a visualização reciclada não estiver disponível,
     * um novo layout é inflado.
     *
     * @param position     A posição do item na lista.
     * @param convertView  A visualização reciclada, se disponível. Caso contrário, uma nova visualização será inflada.
     * @param parent       O grupo pai ao qual a visualização será adicionada.
     * @return A visualização configurada para o item na posição especificada.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        Weather currentWeather = (Weather) getItem(position);

        ImageView weatherIcon = convertView.findViewById(R.id.weather_icon);
        TextView locationTextView = convertView.findViewById(R.id.location);
        TextView tempMinTextView = convertView.findViewById(R.id.temp_min);
        TextView tempMaxTextView = convertView.findViewById(R.id.temp_max);

        // Atualiza o ícone do clima com base na temperatura máxima
        if (currentWeather.getTempMax() <= 15) {
            weatherIcon.setImageResource(R.drawable.frio);
        } else if (currentWeather.getTempMax() >= 20) {
            weatherIcon.setImageResource(R.drawable.calor);
        } else {
            weatherIcon.setImageResource(R.drawable.nuvem);
        }

        // Atualiza o texto com a localização e as temperaturas
        locationTextView.setText(currentWeather.getLocation());
        tempMinTextView.setText(String.format("Min: %.1f°C", currentWeather.getTempMin()));
        tempMaxTextView.setText(String.format("Max: %.1f°C", currentWeather.getTempMax()));

        return convertView;
    }
}
