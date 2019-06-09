package com.crossover.mobiliza.app.data.local.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Converters {
    @TypeConverter
    public static Calendar fromTimestamp(Long value) {
        if (value == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(value);
        return calendar;
    }

    @TypeConverter
    public static Long dateToTimestamp(Calendar date) {
        return date == null ? null : date.getTimeInMillis();
    }

    private static Gson gson = new Gson();

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