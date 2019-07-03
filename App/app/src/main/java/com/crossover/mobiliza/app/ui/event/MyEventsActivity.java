package com.crossover.mobiliza.app.ui.event;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.local.entity.User;
import com.crossover.mobiliza.app.ui.detailed.DetailedEventActivity;
import com.crossover.mobiliza.app.ui.main.adapters.AdapterEvents;
import com.crossover.mobiliza.app.ui.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity {

    private MyEventsViewModel myEventsViewModel;
    private ProgressDialog mProgressDialog;
    private RecyclerView recyclerView;

    private Long ongId;
    private String googleIdToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        myEventsViewModel = ViewModelProviders.of(this).get(MyEventsViewModel.class);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_message));
        mProgressDialog.setCancelable(false);

        ongId = getIntent().getLongExtra("idOng", -1);
        if(getIntent().hasExtra("googleIdToken")){
            googleIdToken = getIntent().getStringExtra("googleIdToken");
        }
        if (ongId < 0) {
            this.finish();
            return;
        }
        myEventsViewModel.setOngId(ongId);

        // RecyclerView Config
        recyclerView = findViewById(R.id.recyclerMyEvents);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
    }

    @Override
    public void onStart() {
        super.onStart();

        myEventsViewModel.findMyEventos(this).observe(this, listResource -> {
            if (listResource.getData() != null) {
                mProgressDialog.show();
                List<Evento> eventos = new ArrayList<>();
                Calendar now = Calendar.getInstance();
                for (Evento evt : listResource.getData()) {
                    if (!evt.getDataRealizacaoAsCalendar().before(now))
                        eventos.add(evt);
                }

                // If ONG doesn't have any events
                if (eventos.size() <= 0) {
                    noEventsYet();
                }

                // Adapter Config
                AdapterEvents adapterEvents = new AdapterEvents(eventos);

                // RecyclerView Config
                recyclerView = findViewById(R.id.recyclerMyEvents);
                recyclerView.setAdapter(adapterEvents);

                // Click event
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(
                                this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User user = myEventsViewModel.getCurrentUser();
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

    private void noEventsYet() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Nenhum evento disponível");
        alert.setMessage("Eventos criados serão listados aqui");
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setCancelable(false);

        alert.setPositiveButton("OK", (dialog, which) -> {
            this.finish();
        });

        alert.create();
        alert.show();
    }



}
