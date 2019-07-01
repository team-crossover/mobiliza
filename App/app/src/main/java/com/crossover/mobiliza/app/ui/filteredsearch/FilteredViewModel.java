package com.crossover.mobiliza.app.ui.filteredsearch;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.local.entity.User;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.repository.EventoRepository;
import com.crossover.mobiliza.app.data.remote.repository.OngRepository;
import com.crossover.mobiliza.app.ui.main.MainActivity;

import java.util.List;

public class FilteredViewModel extends ViewModel {

    public LiveData<Resource<List<Evento>>> findAllEventos(Context context) {
        return EventoRepository.getInstance(context).findAll();
    }

    public LiveData<Resource<List<Ong>>> findAllOngs(Context context) {
        return OngRepository.getInstance(context).findAll();
    }

    public LiveData<Resource<Ong>> getOngById(Context context, Long idOng) {
        return OngRepository.getInstance(context).findById(idOng);
    }

    public User getCurrentUser(){
        if (MainActivity.getUser() != null) {
            return MainActivity.getUser();
        } else {
            return null;
        }

    }
}
