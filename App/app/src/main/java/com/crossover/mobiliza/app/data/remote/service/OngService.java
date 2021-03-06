package com.crossover.mobiliza.app.data.remote.service;

import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.local.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OngService {

    @GET("ongs")
    Call<List<Ong>> findAll(@Query("categoria") String categoria,
                            @Query("regiao") String regiao);

    @GET("ongs/{id}")
    Call<Ong> findById(@Path("id") Long id);

    @DELETE("ongs")
    Call<User> deleteSelf(@Query("googleIdToken") String googleIdToken);

    @POST("ongs")
    Call<Ong> save(@Body Ong ong, @Query("googleIdToken") String googleIdToken);

}