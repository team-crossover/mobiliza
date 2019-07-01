package com.crossover.mobiliza.app.data.local;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.crossover.mobiliza.app.data.local.converters.Converters;
import com.crossover.mobiliza.app.data.local.dao.EventoDao;
import com.crossover.mobiliza.app.data.local.dao.OngDao;
import com.crossover.mobiliza.app.data.local.dao.UserDao;
import com.crossover.mobiliza.app.data.local.dao.VoluntarioDao;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.local.entity.User;
import com.crossover.mobiliza.app.data.local.entity.Voluntario;

@Database(entities = {
        Evento.class,
        Ong.class,
        Voluntario.class,
        User.class}, version = 9, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String NAME = "mobiliza_db";

    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating database instance");
                sInstance = Room
                        .databaseBuilder(context.getApplicationContext(), AppDatabase.class, NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return sInstance;
    }

    public abstract OngDao ongDao();

    public abstract VoluntarioDao voluntarioDao();

    public abstract EventoDao eventoDao();

    public abstract UserDao userDao();

}
