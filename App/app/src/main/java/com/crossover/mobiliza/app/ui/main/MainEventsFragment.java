package com.crossover.mobiliza.app.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.ui.main.adapter.AdapterEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainEventsFragment extends Fragment {

    private static final String TAG = MainEventsFragment.class.getSimpleName();

    private MainEventsViewModel mainEventsViewModel;

    private RecyclerView recyclerView;

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

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_eventos, container, false);
        //...
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // TODO: Adicionar um "swipe to refresh"
        // getAllEventos();

        recyclerView = getView().findViewById(R.id.recyclerEvents);

        // Adapter Config
        AdapterEvents adapterEvents = new AdapterEvents(getAllEventos());

        // RecyclerView Config
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapterEvents);


    }

    private List<Evento> getAllEventos() {
        List<Evento> eventos = new ArrayList<>();

        mainEventsViewModel.findAllEventos(getContext()).observe(this, listResource -> {
            if (listResource.getData() != null) {
                for (Evento evt : listResource.getData()) {
                    eventos.add(evt);
                }
            }
        });

        return eventos;
    }
}