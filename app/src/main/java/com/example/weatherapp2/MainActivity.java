package com.example.weatherapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.weatherapp2.ui.list.WeatherListFragment;
import com.example.weatherapp2.ui.map.WeatherMapFragment;
import com.example.weatherapp2.widget.WidgetConfigureActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Atividade principal da aplicação que gerencia a navegação entre fragmentos.
 * Contém um menu para configuração de widgets e uma barra de navegação inferior
 * para alternar entre a lista de clima e o mapa de clima.
 */
public class MainActivity extends AppCompatActivity {

    private WeatherListFragment weatherListFragment;
    private WeatherMapFragment weatherMapFragment;

    /**
     * Inicializa a atividade. Define o layout da atividade e configura a navegação inferior.
     *
     * @param savedInstanceState O estado salvo, se disponível.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configura a navegação inferior
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Inicializa os fragmentos
        weatherListFragment = new WeatherListFragment();
        weatherMapFragment = new WeatherMapFragment();
    }

    /**
     * Infla o menu de opções da atividade.
     *
     * @param menu O menu a ser exibido.
     * @return Retorna verdadeiro se o menu foi inflado corretamente.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Manipula a seleção de itens no menu de opções.
     *
     * @param item O item do menu selecionado.
     * @return Retorna verdadeiro se o item foi manipulado.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create_widget) {
            // Inicia a atividade para configurar widgets
            Intent intent = new Intent(this, WidgetConfigureActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Listener para o item selecionado na barra de navegação inferior.
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.navigation_list) {
                // Carrega o fragmento da lista de clima
                loadFragment(weatherListFragment);
                return true;
            } else if (id == R.id.navigation_map) {
                // Carrega o fragmento do mapa de clima
                loadFragment(weatherMapFragment);
                return true;
            }

            return false;
        }
    };

    /**
     * Substitui o fragmento exibido no contêiner de fragmentos.
     *
     * @param fragment O fragmento a ser exibido.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
