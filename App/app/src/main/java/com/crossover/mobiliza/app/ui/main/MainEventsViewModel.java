package com.crossover.mobiliza.app.ui.main;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.repository.EventoRepository;

import java.util.List;

public class MainEventsViewModel extends ViewModel {

    // TODO: Adicionar paginação
    public LiveData<Resource<List<Evento>>> findAllEventos(Context context) {
        return EventoRepository.getInstance(context).findAll();
    }


}