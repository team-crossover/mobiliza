package com.crossover.mobiliza.app.ui.utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.CalendarContract;

public class CalendarUtils {

    public static void startAddEventIntent(Activity activity, String nome, String desc, String local, long timeMilis) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, nome);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, desc);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, local);
        intent.putExtra(CalendarContract.Events.ALL_DAY, false);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, timeMilis);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, timeMilis);
        activity.startActivity(intent);
    }

}
