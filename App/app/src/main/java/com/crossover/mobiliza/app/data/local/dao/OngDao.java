package com.crossover.mobiliza.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.crossover.mobiliza.app.data.local.entity.Ong;

import java.util.List;

@Dao
public interface OngDao extends DaoBase<Ong> {

    @Query("SELECT * FROM ongs")
    LiveData<List<Ong>> findAll();

    @Query("SELECT * FROM ongs where id = :id")
    LiveData<Ong> findById(long id);

}
