package com.crossover.mobiliza.app.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.crossover.mobiliza.app.R;

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
        //...
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_eventos, container, false);
        //...
        return root;
    }
}