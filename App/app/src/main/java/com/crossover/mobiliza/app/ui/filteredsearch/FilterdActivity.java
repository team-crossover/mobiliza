package com.crossover.mobiliza.app.ui.filteredsearch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.local.entity.User;
import com.crossover.mobiliza.app.ui.detailed.DetailedEventActivity;
import com.crossover.mobiliza.app.ui.detailed.DetailedOngActivity;
import com.crossover.mobiliza.app.ui.main.adapters.AdapterEvents;
import com.crossover.mobiliza.app.ui.main.adapters.AdapterOngs;
import com.crossover.mobiliza.app.ui.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FilterdActivity extends AppCompatActivity {

    private static final String TAG = FilterdActivity.class.getSimpleName();

    private FilteredViewModel myViewModel;
    private ProgressDialog mProgressDialog;
    private RecyclerView recyclerView;

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
        myViewModel = ViewModelProviders.of(this).get(FilteredViewModel.class);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_message));
        mProgressDialog.setCancelable(false);

        // RecyclerView Config
        recyclerView = findViewById(R.id.recyclerFiltered);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        entidadeFiltrada = findViewById(R.id.textFilteredEntity);
        filtroAplicado = findViewById(R.id.textFilter);

        Intent intent = getIntent();
        //Intents: isEvento, category, filter
        if (intent.hasExtra("isEvento")) {
            entidadeFiltrada.setText("Filtrando Eventos");
            isEvento = true;
        } else {
            entidadeFiltrada.setText("Filtrando Ongs");
            isEvento = false;
        }

        filtro = intent.getStringExtra("filter");

        if (intent.hasExtra("category")) {
            filtroAplicado.setText("Categoria " + filtro);
            isCategoria = true;
        } else {
            filtroAplicado.setText("Região " + filtro);
            isCategoria = false;
        }

        if (isEvento) {
            filteredEvents();
        } else {
            filteredOngs();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Nada
    }

    private void filteredOngs() {
        myViewModel.findAllOngs(this).observe(this, listResource -> {
            if (listResource.getData() != null) {
                List<Ong> ongs = new ArrayList<>();

                for (Ong ong : listResource.getData()) {
                    if (isCategoria) {
                        if (ong.getCategoria().equals(filtro)) {
                            ongs.add(ong);
                        }
                    } else {
                        if (ong.getRegiao().equals(filtro)) {
                            ongs.add(ong);
                        }
                    }
                }
                // Resultados encontrados?
                if (ongs.size() <= 0)
                    noResults();

                // Adapter Config
                AdapterOngs adapterOngs = new AdapterOngs(ongs);

                // RecyclerView Config
                recyclerView.setAdapter(adapterOngs);

                // Click event
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(
                                this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Ong ong = ((AdapterOngs) recyclerView.getAdapter()).getOng(position);
                                Intent myIntent = new Intent(getApplicationContext(), DetailedOngActivity.class);
                                myIntent.putExtra("idOng", ong.getId());
                                getApplicationContext().startActivity(myIntent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            }
                        }
                        )
                );

            }
        });
    }

    private void filteredEvents() {
        myViewModel.findAllEventos(this).observe(this, listResource -> {
            if (listResource.getData() != null) {
                mProgressDialog.show();
                List<Evento> eventos = new ArrayList<>();
                Calendar now = Calendar.getInstance();
                for (Evento evt : listResource.getData()) {
                    if (!evt.getDataRealizacaoAsCalendar().before(now)) {

                        if (!evt.getDataRealizacaoAsCalendar().before(now)) {
                            if (isCategoria) {
                                if (evt.getCategoria() != null &&  evt.getCategoria().equals(filtro)) {
                                    eventos.add(evt);
                                }
                            } else {
                                if (evt.getRegiao().equals(filtro))
                                    eventos.add(evt);
                            }
                        }
                    }
                }

                if (eventos.size() <= 0)
                    noResults();

                // Adapter Config
                AdapterEvents adapterEvents = new AdapterEvents(eventos);

                // RecyclerView Config
                recyclerView = findViewById(R.id.recyclerFiltered);
                recyclerView.setAdapter(adapterEvents);

                // Click event
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(
                                this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User user = myViewModel.getCurrentUser();
                                Evento evento = ((AdapterEvents) recyclerView.getAdapter()).getEvento(position);
                                Intent myIntent = new Intent(getApplicationContext(), DetailedEventActivity.class);
                                if (user != null) {
                                    if (user.isLastUsedAsOng()) {
                                        if (evento.getIdOng() == user.getIdOng()) {
                                            myIntent.putExtra("idOwner", user.getId());
                                        }
                                    } else {
                                        myIntent.putExtra("idVoluntario", user.getIdVoluntario());
                                    }
                                    myIntent.putExtra("googleIdToken", user.getGoogleIdToken());
                                }
                                myIntent.putExtra("idEvent", evento.getId());

                                getApplicationContext().startActivity(myIntent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            }
                        }
                        )
                );
                mProgressDialog.dismiss();
            }
        });
    }

    private void noResults() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Nenhum resultado encontrado");
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setCancelable(false);

        alert.setPositiveButton("OK", (dialog, which) -> {
            this.finish();
        });

        alert.create();
        try {
            alert.show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
