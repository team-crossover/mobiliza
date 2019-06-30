package com.crossover.mobiliza.app.ui.filteredsearch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crossover.mobiliza.app.R;

public class FilterdActivity extends AppCompatActivity {

    private TextView entidadeFiltrada;
    private TextView filtroAplicado;
    private TextView tipoFiltro;
    private boolean isEvento;
    private boolean isCategoria;
    private String filtro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filterd);

        entidadeFiltrada = findViewById(R.id.textFilteredEntity);
        filtroAplicado = findViewById(R.id.textFilter);
        tipoFiltro = findViewById(R.id.textTypeFilter);

        Intent intent = getIntent();
        //Intents: isEvento/isOng, category/region, filter
        if (intent.hasExtra("isEvento")) {
            entidadeFiltrada.setText("Filtrando Eventos");
            isEvento = true;
        } else {
            entidadeFiltrada.setText("Filtrando Ongs");
            isEvento = false;
        }

        filtro = intent.getStringExtra("filter");

        if (intent.hasExtra("category")){
            filtroAplicado.setText("Categoria " + filtro);
            isCategoria = true;
        } else {
            filtroAplicado.setText(filtro);
            isCategoria = false;
        }

    }
}
