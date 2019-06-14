package com.crossover.mobiliza.app.data.remote.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import com.crossover.mobiliza.app.AppExecutors;
import com.crossover.mobiliza.app.data.local.AppDatabase;
import com.crossover.mobiliza.app.data.local.dao.EventoDao;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.remote.NetworkBoundResource;
import com.crossover.mobiliza.app.data.remote.RateLimiter;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.service.AppServices;
import com.crossover.mobiliza.app.data.remote.service.EventoService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class EventoRepository {

    private static final String TAG = EventoRepository.class.getSimpleName();
    private static final Object LOCK = new Object();

    private static EventoRepository sInstance;

    private final AppDatabase appDatabase;
    private final AppServices appServices;
    private final EventoDao eventoDao;
    private final EventoService eventoService;
    private RateLimiter<Long> rateLimiter;

    public static EventoRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating singleton instance");
                sInstance = new EventoRepository(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    private EventoRepository(Context context) {
        appDatabase = AppDatabase.getInstance(context);
        eventoDao = appDatabase.eventoDao();
        appServices = AppServices.getInstance(context);
        eventoService = appServices.createService(EventoService.class);
        rateLimiter = new RateLimiter<>(10, TimeUnit.SECONDS);
    }

    public LiveData<Resource<List<Evento>>> findAll() {
        return new NetworkBoundResource<List<Evento>, List<Evento>>() {
            @Override
            protected void saveCallResult(List<Evento> item) {
                if (item != null)
                    eventoDao.saveAll(item);
            }

            @NonNull
            @Override
            protected LiveData<List<Evento>> loadFromDb() {
                return eventoDao.findAll();
            }

            @NonNull
            @Override
            protected Call<List<Evento>> createCall() {
                return eventoService.findAll(null, null, null, null);
            }

            @Override
            protected boolean shouldFetch() {
                return rateLimiter.shouldFetch(null) || super.shouldFetch();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Evento>>> findAllByOng(Long idOng) {
        return new NetworkBoundResource<List<Evento>, List<Evento>>() {
            @Override
            protected void saveCallResult(List<Evento> item) {
                if (item != null)
                    eventoDao.saveAll(item);
            }

            @NonNull
            @Override
            protected LiveData<List<Evento>> loadFromDb() {
                return eventoDao.findAllByOng(idOng);
            }

            @NonNull
            @Override
            protected Call<List<Evento>> createCall() {
                return eventoService.findAll(idOng, null, null, null);
            }

            @Override
            protected boolean shouldFetch() {
                return rateLimiter.shouldFetch(null) || super.shouldFetch();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Evento>>> findAllByFinalizado(Boolean finalizado) {
        return new NetworkBoundResource<List<Evento>, List<Evento>>() {
            @Override
            protected void saveCallResult(List<Evento> item) {
                if (item != null)
                    eventoDao.saveAll(item);
            }

            @NonNull
            @Override
            protected LiveData<List<Evento>> loadFromDb() {
                return finalizado ? eventoDao.findAllFinalizados() : eventoDao.findAllNotFinalizados();
            }

            @NonNull
            @Override
            protected Call<List<Evento>> createCall() {
                return eventoService.findAll(null, null, null, finalizado);
            }

            @Override
            protected boolean shouldFetch() {
                return rateLimiter.shouldFetch(null) || super.shouldFetch();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Evento>> findById(final long id) {
        return new NetworkBoundResource<Evento, Evento>() {

            @Override
            protected void saveCallResult(Evento item) {
                if (item != null)
                    eventoDao.save(item);
            }

            @NonNull
            @Override
            protected LiveData<Evento> loadFromDb() {
                return eventoDao.findById(id);
            }

            @NonNull
            @Override
            protected Call<Evento> createCall() {
                return eventoService.findById(id);
            }

            @Override
            protected boolean shouldFetch() {
                return rateLimiter.shouldFetch(id) || super.shouldFetch();
            }
        }.getAsLiveData();
    }

    public void save(final Evento evento, final String googleIdToken, final Consumer<Evento> onSuccess, final Consumer<String> onFailure) {
        AppExecutors.getInstance().network().execute(() -> {
            Call<Evento> call = eventoService.save(evento, googleIdToken);
            AppServices.runCallAsync(call,
                    newEvento -> {
                        rateLimiter.shouldFetch(null);
                        rateLimiter.shouldFetch(evento.getId());
                        onSuccess.accept(newEvento);
                        Log.i(TAG, "saved evento: " + newEvento.toString());
                    },
                    errorMsg -> {
                        onFailure.accept(errorMsg);
                    });
        });
    }

}
