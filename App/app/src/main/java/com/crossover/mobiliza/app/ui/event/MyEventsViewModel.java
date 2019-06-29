package com.crossover.mobiliza.app.ui.event;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.repository.EventoRepository;

import java.util.List;

public class MyEventsViewModel extends ViewModel {

    private Long ongId;

    public Long getOngId() {
        return ongId;
    }

    public void setOngId(Long ongId) {
        this.ongId = ongId;
    }

    public LiveData<Resource<List<Evento>>> findMyEventos(Context context) {
        return EventoRepository.getInstance(context).findAllByOng(ongId);
    }
}
