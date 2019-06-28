package com.crossover.mobiliza.app;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.crossover.mobiliza.app.ui.main.MainActivity;

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

    public static void relaunchApp(Context context) {
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
}
