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
import com.crossover.mobiliza.app.data.local.entity.Ong;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainOngsFragment extends Fragment {

    private static final String TAG = MainOngsFragment.class.getSimpleName();

    private MainOngsViewModel mainOngsViewModel;

    public static MainOngsFragment newInstance() {
        MainOngsFragment fragment = new MainOngsFragment();
        Bundle bundle = new Bundle();
//        bundle.putInt("key", value); // Optional arguments
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainOngsViewModel = ViewModelProviders.of(this).get(MainOngsViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_eventos, container, false);

        //...?

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // TODO: Parar de usar esse TextView ridículo e usar uma RecyclerView decente
        // TODO: Adicionar um "swipe to refresh"

        // Atualiza a lista de ongs
        final TextView textView = getView().findViewById(R.id.test_label);
        mainOngsViewModel.findAllOngs(getContext()).observe(this, listResource -> {
            if (listResource.getData() != null) {
                StringBuilder sb = new StringBuilder();
                for (Ong ong : listResource.getData()) {
                    sb.append(ong.getNome() + ": " + ong.getDescricao() + "\n\n\n");
                }
                textView.setText(sb.toString());
            }
        });
    }
}