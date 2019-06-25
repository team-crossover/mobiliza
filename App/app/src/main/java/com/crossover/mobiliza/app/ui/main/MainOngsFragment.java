package com.crossover.mobiliza.app.ui.main;

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
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.ui.detailed.DetailedOngActivity;
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

        //...?

        return root;
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
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
                recyclerView.setAdapter(adapterOngs);

                // Click event
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(
                                getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Ong ong = ongs.get(position);
                                Intent myIntent = new Intent(getContext(), DetailedOngActivity.class);
                                myIntent.putExtra("idOng", ong.getId());
                                getContext().startActivity(myIntent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) { }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                        }
                        )
                );

            }
        });

    }
}