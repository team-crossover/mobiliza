package com.crossover.mobiliza.app.data.remote.service;

import com.crossover.mobiliza.app.data.local.entity.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {

    @GET("/users")
    Call<User> findByGoogleTokenId(@Query("googleIdToken") String googleIdToken, @Query("asOng") boolean asOng);

    @GET("/users")
    Call<User> findByGoogleTokenId(@Query("googleIdToken") String googleIdToken);

}