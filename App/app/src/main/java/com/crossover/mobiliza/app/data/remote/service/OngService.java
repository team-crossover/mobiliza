package com.crossover.mobiliza.app.data.remote.service;

import com.crossover.mobiliza.app.data.local.entity.Ong;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OngService {

    @GET("ongs")
    Call<List<Ong>> findAll();

    @GET("ongs")
    Call<List<Ong>> findAllByCategoria(@Query("categoria") String categoria);

    @GET("ongs")
    Call<List<Ong>> findAllByRegiao(@Query("regiao") String regiao);

    @GET("ongs/{id}")
    Call<Ong> findById(@Path("id") Long id);

    @POST("ongs")
    Call<Ong> save(@Body Ong ong, @Query("googleIdToken") String googleIdToken);

}