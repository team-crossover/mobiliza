package com.crossover.mobiliza.app.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainEventsFragment extends Fragment {

    private static final String TAG = MainEventsFragment.class.getSimpleName();

    private MainEventsViewModel mainEventsViewModel;

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

        // TODO: Parar de usar esse TextView ridículo e usar uma RecyclerView decente
        // TODO: Adicionar um "swipe to refresh"

        // Atualiza a lista de ongs
        final TextView textView = getView().findViewById(R.id.test_label);
        mainEventsViewModel.findAllEventos(getContext()).observe(this, listResource -> {
            if (listResource.getData() != null) {
                StringBuilder sb = new StringBuilder();
                for (Evento evt : listResource.getData()) {
                    sb.append(evt.getNome()+"\n");
                }
                textView.setText(sb.toString());
            }
        });
    }
}