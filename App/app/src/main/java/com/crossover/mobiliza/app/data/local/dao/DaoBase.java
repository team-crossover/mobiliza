package com.crossover.mobiliza.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import java.util.List;


@Dao
public interface DaoBase<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(T ong);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<T> ong);

    @Delete
    void delete(T ong);

}
