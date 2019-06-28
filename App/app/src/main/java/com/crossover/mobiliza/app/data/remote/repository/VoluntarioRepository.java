package com.crossover.mobiliza.app.data.remote.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import com.crossover.mobiliza.app.AppExecutors;
import com.crossover.mobiliza.app.data.local.AppDatabase;
import com.crossover.mobiliza.app.data.local.dao.VoluntarioDao;
import com.crossover.mobiliza.app.data.local.entity.Voluntario;
import com.crossover.mobiliza.app.data.remote.NetworkBoundResource;
import com.crossover.mobiliza.app.data.remote.RateLimiter;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.service.AppServices;
import com.crossover.mobiliza.app.data.remote.service.VoluntarioService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class VoluntarioRepository {

    private static final String TAG = VoluntarioRepository.class.getSimpleName();
    private static final Object LOCK = new Object();

    private static VoluntarioRepository sInstance;

    private final AppDatabase appDatabase;
    private final AppServices appServices;
    private final VoluntarioDao voluntarioDao;
    private final VoluntarioService voluntarioService;
    private RateLimiter<Long> rateLimiter;

    public static VoluntarioRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating singleton instance");
                sInstance = new VoluntarioRepository(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    private VoluntarioRepository(Context context) {
        appDatabase = AppDatabase.getInstance(context);
        voluntarioDao = appDatabase.voluntarioDao();
        appServices = AppServices.getInstance(context);
        voluntarioService = appServices.createService(VoluntarioService.class);
        rateLimiter = new RateLimiter<Long>(10, TimeUnit.SECONDS);
    }

    public LiveData<Resource<List<Voluntario>>> findAll() {
        return new NetworkBoundResource<List<Voluntario>, List<Voluntario>>() {
            @Override
            protected void saveCallResult(List<Voluntario> item) {
                if (item != null) {
                    voluntarioDao.deleteAll();
                    voluntarioDao.saveAll(item);
                }
            }

            @NonNull
            @Override
            protected LiveData<List<Voluntario>> loadFromDb() {
                return voluntarioDao.findAll();
            }

            @NonNull
            @Override
            protected Call<List<Voluntario>> createCall() {
                return voluntarioService.findAll();
            }

            @Override
            protected boolean shouldFetch() {
                return rateLimiter.shouldFetch(null) || super.shouldFetch();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Voluntario>> findById(final long id) {
        return new NetworkBoundResource<Voluntario, Voluntario>() {

            @Override
            protected void saveCallResult(Voluntario item) {
                if (item != null)
                    voluntarioDao.save(item);
            }

            @NonNull
            @Override
            protected LiveData<Voluntario> loadFromDb() {
                return voluntarioDao.findById(id);
            }

            @NonNull
            @Override
            protected Call<Voluntario> createCall() {
                return voluntarioService.findById(id);
            }

            @Override
            protected boolean shouldFetch() {
                return rateLimiter.shouldFetch(id) || super.shouldFetch();
            }
        }.getAsLiveData();
    }

    public void save(final Voluntario ong, final String googleIdToken, final Consumer<Voluntario> onSuccess, final Consumer<String> onFailure) {
        AppExecutors.getInstance().network().execute(() -> {
            AppServices.runCallAsync(voluntarioService.save(ong, googleIdToken),
                    newOng -> {
                        rateLimiter.shouldFetch(null);
                        rateLimiter.shouldFetch(ong.getId());
                        onSuccess.accept(newOng);
                        Log.i(TAG, "saved ong: " + newOng.toString());
                    },
                    errorMsg -> {
                        onFailure.accept(errorMsg);
                    });
        });
    }

}
