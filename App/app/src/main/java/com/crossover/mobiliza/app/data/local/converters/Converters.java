package com.crossover.mobiliza.app.data.local.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Converters {

    private static SimpleDateFormat dateTimeFormat
            = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);

    private static Gson gson = new Gson();

    @TypeConverter
    public static Calendar stringToCalendar(String value) {
        if (value == null)
            return null;
        try {
            Date date = dateTimeFormat.parse(value);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String calendarToString(Calendar date) {
        return date == null ? null : dateTimeFormat.format(date.getTime());
    }

    @TypeConverter
    public static List<Long> stringToLongList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Long>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String longListToString(List<Long> someObjects) {
        return gson.toJson(someObjects);
    }
}