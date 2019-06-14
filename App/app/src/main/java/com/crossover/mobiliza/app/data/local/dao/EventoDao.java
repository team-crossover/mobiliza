package com.crossover.mobiliza.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.crossover.mobiliza.app.data.local.entity.Evento;

import java.util.List;

@Dao
public interface EventoDao extends DaoBase<Evento> {

    @Query("SELECT * FROM eventos")
    LiveData<List<Evento>> findAll();

//    @Query("SELECT * FROM eventos WHERE idOng = :idOng")
//    LiveData<List<Evento>> findAllByOng(long idOng);

    @Query("SELECT * FROM eventos WHERE strftime('%s', dataRealizacao) < strftime('%s', 'now')")
    LiveData<List<Evento>> findAllFinalizados();

    @Query("SELECT * FROM eventos WHERE strftime('%s', dataRealizacao) >= strftime('%s', 'now')")
    LiveData<List<Evento>> findAllNotFinalizados();

    @Query("SELECT * FROM eventos where id = :id")
    LiveData<Evento> findById(long id);

}
