package com.crossover.mobiliza.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.crossover.mobiliza.app.data.local.entity.Voluntario;

import java.util.List;

@Dao
public interface VoluntarioDao extends DaoBase<Voluntario> {

    @Query("SELECT * FROM voluntarios")
    LiveData<List<Voluntario>> findAll();

    @Query("SELECT * FROM voluntarios where id = :id")
    LiveData<Voluntario> findById(long id);

    @Query("DELETE FROM voluntarios")
    void deleteAll();
}
