package com.crossover.mobiliza.app.ui.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.crossover.mobiliza.app.ui.detailed.DetailedEventActivity;
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

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_eventos, container, false);

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