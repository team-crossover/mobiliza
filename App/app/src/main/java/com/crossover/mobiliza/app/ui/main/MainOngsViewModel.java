package com.crossover.mobiliza.app.ui.main;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.repository.OngRepository;

import java.util.List;

public class MainOngsViewModel extends ViewModel {

    public LiveData<Resource<List<Ong>>> findAllOngs(Context context) {
        return OngRepository.getInstance(context).findAll();
    }

}