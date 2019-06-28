package com.crossover.mobiliza.app.data.remote.service;

import com.crossover.mobiliza.app.data.local.entity.User;
import com.crossover.mobiliza.app.data.local.entity.Voluntario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface VoluntarioService {

    @GET("voluntarios")
    Call<List<Voluntario>> findAll();

    @GET("voluntarios")
    Call<Voluntario> findById(@Query("id") Long id);

    @DELETE("voluntarios")
    Call<User> deleteSelf(@Query("googleIdToken") String googleIdToken);

    @POST("voluntarios")
    Call<Voluntario> save(@Body Voluntario voluntario, @Query("googleIdToken") String idToken);

}