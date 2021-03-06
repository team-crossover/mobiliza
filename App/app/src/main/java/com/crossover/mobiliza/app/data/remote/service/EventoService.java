package com.crossover.mobiliza.app.data.remote.service;

import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.local.entity.Ong;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventoService {

    @GET("eventos")
    Call<List<Evento>> findAll(@Query("idOng") Long idOng,
                               @Query("categoria") String categoria,
                               @Query("regiao") String regiao,
                               @Query("finalizado") Boolean finalizado);

    @GET("eventos/{id}")
    Call<Evento> findById(@Path("id") Long id);

    @DELETE("eventos/{id}")
    Call<Ong> deleteById(@Path("id") Long id, @Query("googleIdToken") String googleIdToken);

    @POST("eventos")
    Call<Evento> save(@Body Evento evento, @Query("googleIdToken") String googleIdToken);

    @POST("eventos/{id}/confirmar")
    Call<Evento> confirmar(@Path("id") Long id, @Query("googleIdToken") String googleIdToken, @Query("valor") Boolean valor);
}