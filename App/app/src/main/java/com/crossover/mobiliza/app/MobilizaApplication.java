package com.crossover.mobiliza.app;

import android.app.Application;

public class MobilizaApplication extends Application {

    private static MobilizaApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        // This can be used to initialize things before the first Activity is shown.
    }


    public static MobilizaApplication getAppContext() {
        return sInstance;
    }

    private static synchronized void setInstance(MobilizaApplication app) {
        sInstance = app;
    }

}
