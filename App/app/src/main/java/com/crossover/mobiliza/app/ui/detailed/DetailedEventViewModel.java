package com.crossover.mobiliza.app.ui.detailed;

import android.content.Context;
import android.widget.Toast;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.repository.EventoRepository;

import java.util.Calendar;

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

    public void deleteEvent() {

    }

    public void saveEvent(Context context,
                          Evento updatedevent,
                          Consumer<Evento> onSuccess,
                          Consumer<String> onFailure) {

        if (updatedevent.getId() == mEventId) {
            EventoRepository.getInstance(context).save(updatedevent, mGoogleIdToken, onSuccess, onFailure);
        } else {
            Toast.makeText(context, "Não foi possível atualizar o evento", Toast.LENGTH_SHORT).show();
        }

    }
}
