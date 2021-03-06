package com.crossover.mobiliza.app.ui.detailed;

import android.content.Context;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.repository.EventoRepository;
import com.crossover.mobiliza.app.data.remote.repository.OngRepository;

public class DetailedEventViewModel extends ViewModel {

    private long mEventId = -1;
    private String mGoogleIdToken = null;

    public void setEventoId(long id) {
        mEventId = id;
    }

    public void setGoogleIdToken(String googleIdToken) {
        mGoogleIdToken = googleIdToken;
    }

    public LiveData<Resource<Evento>> getEvento(Context context) {
        return EventoRepository.getInstance(context).findById(mEventId);
    }

    public void confirmarEvento(Context context,
                                Boolean valor,
                                Consumer<Evento> onSuccess,
                                Consumer<String> onFailure) {
        EventoRepository.getInstance(context).confirmarEvento(mEventId, mGoogleIdToken, valor, onSuccess, onFailure);
    }

    public void deletarEvento(Context context,
                              Consumer<Ong> onSuccess,
                              Consumer<String> onFailure) {
        EventoRepository.getInstance(context).deletarEvento(mEventId, mGoogleIdToken, onSuccess, onFailure);
    }

    public LiveData<Resource<Ong>> getOng(Context context, long idOng) {
        return OngRepository.getInstance(context).findById(idOng);
    }
}
