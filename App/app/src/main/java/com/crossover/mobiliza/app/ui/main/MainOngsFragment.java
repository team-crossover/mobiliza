package com.crossover.mobiliza.app.ui.main;

import android.app.AlertDialog;
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
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.local.enums.CategoriaEnum;
import com.crossover.mobiliza.app.data.local.enums.RegiaoEnum;
import com.crossover.mobiliza.app.ui.detailed.DetailedOngActivity;
import com.crossover.mobiliza.app.ui.filteredsearch.FilterdActivity;
import com.crossover.mobiliza.app.ui.main.adapters.AdapterOngs;
import com.crossover.mobiliza.app.ui.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainOngsFragment extends Fragment {

    private static final String TAG = MainOngsFragment.class.getSimpleName();

    private MainOngsViewModel mainOngsViewModel;
    private RecyclerView recyclerView;
    private Button filtrarCategoria;
    private Button filtrarRegiao;
    private String selected;

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
        View root = inflater.inflate(R.layout.fragment_main_ongs, container, false);

        filtrarCategoria = root.findViewById(R.id.buttonOngCategory);
        filtrarCategoria.setOnClickListener(this::onCategoryFilter);
        filtrarRegiao = root.findViewById(R.id.buttonOngRegion);
        filtrarRegiao.setOnClickListener(this::onRegionFilter);

        // RecyclerView Config
        recyclerView = root.findViewById(R.id.recyclerOngs);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));

        return root;
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
        if (isCategoria) {
            myIntent.putExtra("category", true);
        }
        myIntent.putExtra("filter", selected);
        getContext().startActivity(myIntent);
    }

    @Override
    public void onStart() {
        super.onStart();

        mainOngsViewModel.findAllOngs(getContext()).observe(this, listResource -> {
            if (listResource.getData() != null) {
                List<Ong> ongs = new ArrayList<>();
                for (Ong ong : listResource.getData()) {
                    ongs.add(ong);
                }
                recyclerView = getView().findViewById(R.id.recyclerOngs);

                // Adapter Config
                AdapterOngs adapterOngs = new AdapterOngs(ongs);

                // RecyclerView Config
                recyclerView.setAdapter(adapterOngs);

                // Click event
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(
                                getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Ong ong = ((AdapterOngs) recyclerView.getAdapter()).getOng(position);
                                Intent myIntent = new Intent(getContext(), DetailedOngActivity.class);
                                myIntent.putExtra("idOng", ong.getId());
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

            }
        });
    }
}