package com.crossover.mobiliza.app;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static final String TAG = AppExecutors.class.getSimpleName();
    private static final Object LOCK = new Object();

    private static AppExecutors sInstance;

    private final Executor mainThreadExecutor;
    private final Executor networkExecutor;
    private final Executor diskExecutor;

    public AppExecutors(Executor mainThreadExecutor, Executor networkExecutor, Executor diskExecutor) {
        this.mainThreadExecutor = mainThreadExecutor;
        this.networkExecutor = networkExecutor;
        this.diskExecutor = diskExecutor;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(new MainThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor mainThread() {
        return mainThreadExecutor;
    }

    public Executor network() {
        return networkExecutor;
    }

    public Executor disk() {
        return diskExecutor;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
