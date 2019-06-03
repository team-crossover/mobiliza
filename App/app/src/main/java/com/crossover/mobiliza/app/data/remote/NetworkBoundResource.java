package com.crossover.mobiliza.app.data.remote;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.crossover.mobiliza.app.AppExecutors;
import com.crossover.mobiliza.app.MobilizaApplication;
import com.crossover.mobiliza.app.R;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    protected NetworkBoundResource() {
        result.setValue(Resource.loading(null));

        // Always load the data from DB intially so that we have
        LiveData<ResultType> dbSource = loadFromDb();

        // Fetch the data from network and add it to the resource
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch()) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, newData -> {
                    if (null != newData)
                        result.setValue(Resource.success(newData));
                });
            }
        });
    }

    /**
     * This method fetches the data from remoted service and save it to local db
     *
     * @param dbSource - Database source
     */
    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        result.addSource(dbSource, newData -> result.setValue(Resource.loading(newData)));
        AppExecutors.getInstance().network().execute(() -> createCall().enqueue(new Callback<RequestType>() {
            @Override
            public void onResponse(@NonNull Call<RequestType> call, @NonNull Response<RequestType> response) {
                result.removeSource(dbSource);
                saveResultAndReInit(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<RequestType> call, @NonNull Throwable t) {
                result.removeSource(dbSource);
                result.addSource(dbSource, newData -> result.setValue(Resource.error(newData, getErrorMessage(t))));
            }
        }));
    }

    private String getErrorMessage(Throwable error) {
        if (error instanceof SocketTimeoutException) {
            return MobilizaApplication.getAppContext().getString(R.string.requestTimeOutError);
        } else if (error instanceof IOException) {
            return MobilizaApplication.getAppContext().getString(R.string.networkError);
        } else {
            return MobilizaApplication.getAppContext().getString(R.string.unknownError);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    private void saveResultAndReInit(RequestType response) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                saveCallResult(response);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                result.addSource(loadFromDb(), newData -> {
                    if (null != newData)
                        result.setValue(Resource.success(newData));
                });
            }
        }.execute();
    }

    @WorkerThread
    protected abstract void saveCallResult(RequestType item);

    @MainThread
    protected boolean shouldFetch() {
        return true;
    }

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @NonNull
    @MainThread
    protected abstract Call<RequestType> createCall();

    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return result;
    }
}