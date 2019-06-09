package com.crossover.mobiliza.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.crossover.mobiliza.app.data.local.entity.User;

import java.util.List;

@Dao
public interface UserDao extends DaoBase<User> {

    @Query("SELECT * FROM users")
    LiveData<List<User>> findAll();

    @Query("SELECT * FROM users where id = :id")
    LiveData<User> findById(long id);

    @Query("SELECT * FROM users WHERE googleIdToken = :googleIdToken")
    LiveData<User> findByGoogleIdToken(String googleIdToken);

}
