package com.crossover.mobiliza.app.data.remote.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.crossover.mobiliza.app.AppExecutors;
import com.crossover.mobiliza.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppServices {

    private static final String TAG = AppServices.class.getSimpleName();
    private static final Object LOCK = new Object();

    private static AppServices sInstance;

    private Retrofit mRetrofit;

    public static AppServices getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating AppServices instance");
                sInstance = new AppServices(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    private AppServices(Context context) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public <TService> TService createService(Class<TService> serviceClass) {
        return mRetrofit.create(serviceClass);
    }

    public static <T> void runCallAsync(final Call<T> call, final Consumer<T> onSuccess, final Consumer<String> onFailure) {
        AppExecutors.getInstance().network().execute(() -> {
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (response.isSuccessful()) {
                        onSuccess.accept(response.body());
                    } else {
                        try {
                            String resp = response.errorBody().string();
                            JSONObject json = new JSONObject(resp);
                            String msg = json.getString("message");
                            Log.e(TAG, msg);
                            onFailure.accept(msg);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            onFailure.accept(response.message());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                    onFailure.accept(t.getMessage());
                }
            });
        });
    }
}
