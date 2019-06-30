package com.crossover.mobiliza.app.ui.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.local.entity.User;
import com.crossover.mobiliza.app.data.local.enums.CategoriaEnum;
import com.crossover.mobiliza.app.data.local.enums.RegiaoEnum;
import com.crossover.mobiliza.app.ui.detailed.DetailedEventActivity;
import com.crossover.mobiliza.app.ui.filteredsearch.FilterdActivity;
import com.crossover.mobiliza.app.ui.main.adapters.AdapterEvents;
import com.crossover.mobiliza.app.ui.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainEventsFragment extends Fragment {

    private static final String TAG = MainEventsFragment.class.getSimpleName();

    private MainEventsViewModel mainEventsViewModel;
    private ProgressDialog mProgressDialog;
    private RecyclerView recyclerView;
    private Button filtrarCategoria;
    private Button filtrarRegiao;
    private String selected;

    public static MainEventsFragment newInstance() {
        MainEventsFragment fragment = new MainEventsFragment();
        Bundle bundle = new Bundle();
//        bundle.putInt("key", value); // Optional args
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainEventsViewModel = ViewModelProviders.of(this).get(MainEventsViewModel.class);
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.progress_message));
        mProgressDialog.setCancelable(false);

    }

    private void onCategoryFilter(View view) {

        String[] options = CategoriaEnum.getAsArray();

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        alert.setTitle("Selecione uma categoria");
        alert.setSingleChoiceItems(options, -1, (dialog, which) -> selected = options[which]);

        alert.setPositiveButton("Confirmar", (dialog, which) -> startFiltrar(true));
        alert.setNegativeButton("Cancelar", (dialog, which) -> {
            return;
        });

        alert.create();
        alert.show();

    }

    private void onRegionFilter(View view) {
        String[] options = RegiaoEnum.getAsArray();

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        alert.setTitle("Selecione uma região");
        alert.setSingleChoiceItems(options, -1, (dialog, which) -> selected = options[which]);

        alert.setPositiveButton("Confirmar", (dialog, which) -> startFiltrar(false));
        alert.setNegativeButton("Cancelar", (dialog, which) -> {
            return;
        });

        alert.create();
        alert.show();
    }

    private void startFiltrar(boolean isCategoria) {
        Intent myIntent = new Intent(getContext(), FilterdActivity.class);
        myIntent.putExtra("isEvento", true);
        if (isCategoria) {
            myIntent.putExtra("category", true);
        }
        myIntent.putExtra("filter", selected);
        getContext().startActivity(myIntent);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_eventos, container, false);

        filtrarCategoria = root.findViewById(R.id.buttonEventCategory);
        filtrarCategoria.setOnClickListener(this::onCategoryFilter);
        filtrarRegiao = root.findViewById(R.id.buttonEventRegion);
        filtrarRegiao.setOnClickListener(this::onRegionFilter);

        // TODO: Adicionar um "swipe to refresh"

        // RecyclerView Config
        recyclerView = root.findViewById(R.id.recyclerEvents);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        mainEventsViewModel.findAllEventos(getContext()).observe(this, listResource -> {
            if (listResource.getData() != null) {
                mProgressDialog.show();
                List<Evento> eventos = new ArrayList<>();
                Calendar now = Calendar.getInstance();
                for (Evento evt : listResource.getData()) {
                    if (!evt.getDataRealizacaoAsCalendar().before(now))
                        eventos.add(evt);
                }

                // Adapter Config
                AdapterEvents adapterEvents = new AdapterEvents(eventos);

                // RecyclerView Config
                recyclerView = getView().findViewById(R.id.recyclerEvents);
                recyclerView.setAdapter(adapterEvents);

                // Click event
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(
                                getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User user = mainEventsViewModel.getCurrentUser();
                                Evento evento = ((AdapterEvents) recyclerView.getAdapter()).getEvento(position);
                                Intent myIntent = new Intent(getContext(), DetailedEventActivity.class);

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

                                getContext().startActivity(myIntent);
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
}