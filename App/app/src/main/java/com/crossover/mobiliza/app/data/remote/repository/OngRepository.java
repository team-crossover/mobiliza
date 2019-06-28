package com.crossover.mobiliza.app.data.remote.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import com.crossover.mobiliza.app.AppExecutors;
import com.crossover.mobiliza.app.data.local.AppDatabase;
import com.crossover.mobiliza.app.data.local.dao.OngDao;
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.remote.NetworkBoundResource;
import com.crossover.mobiliza.app.data.remote.RateLimiter;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.service.AppServices;
import com.crossover.mobiliza.app.data.remote.service.OngService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class OngRepository {

    private static final String TAG = OngRepository.class.getSimpleName();
    private static final Object LOCK = new Object();

    private static OngRepository sInstance;

    private final AppDatabase appDatabase;
    private final AppServices appServices;
    private final OngDao ongDao;
    private final OngService ongService;
    private RateLimiter<Long> rateLimiter;

    public static OngRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating singleton instance");
                sInstance = new OngRepository(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    private OngRepository(Context context) {
        appDatabase = AppDatabase.getInstance(context);
        ongDao = appDatabase.ongDao();
        appServices = AppServices.getInstance(context);
        ongService = appServices.createService(OngService.class);
        rateLimiter = new RateLimiter<Long>(10, TimeUnit.SECONDS);
    }

    public LiveData<Resource<List<Ong>>> findAll() {
        return new NetworkBoundResource<List<Ong>, List<Ong>>() {
            @Override
            protected void saveCallResult(List<Ong> item) {
                if (item != null) {
                    ongDao.deleteAll();
                    ongDao.saveAll(item);
                }
            }

            @NonNull
            @Override
            protected LiveData<List<Ong>> loadFromDb() {
                return ongDao.findAll();
            }

            @NonNull
            @Override
            protected Call<List<Ong>> createCall() {
                return ongService.findAll(null, null);
            }

            @Override
            protected boolean shouldFetch() {
                return rateLimiter.shouldFetch(null) || super.shouldFetch();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Ong>> findById(final long id) {
        return new NetworkBoundResource<Ong, Ong>() {

            @Override
            protected void saveCallResult(Ong item) {
                if (item != null)
                    ongDao.save(item);
            }

            @NonNull
            @Override
            protected LiveData<Ong> loadFromDb() {
                return ongDao.findById(id);
            }

            @NonNull
            @Override
            protected Call<Ong> createCall() {
                return ongService.findById(id);
            }

            @Override
            protected boolean shouldFetch() {
                return rateLimiter.shouldFetch(id) || super.shouldFetch();
            }
        }.getAsLiveData();
    }

    public void save(final Ong ong, final String googleIdToken, final Consumer<Ong> onSuccess, final Consumer<String> onFailure) {
        AppExecutors.getInstance().network().execute(() -> {
            AppServices.runCallAsync(ongService.save(ong, googleIdToken),
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
