package com.crossover.mobiliza.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    private Long id;

    private String googleId;

    private String googleIdToken;

    private boolean lastUsedAsOng;

    private Long idOng;

    private Long idVoluntario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getGoogleIdToken() {
        return googleIdToken;
    }

    public void setGoogleIdToken(String googleIdToken) {
        this.googleIdToken = googleIdToken;
    }

    public boolean isLastUsedAsOng() {
        return lastUsedAsOng;
    }

    public void setLastUsedAsOng(boolean lastUsedAsOng) {
        this.lastUsedAsOng = lastUsedAsOng;
    }

    public Long getIdOng() {
        return idOng;
    }

    public void setIdOng(Long idOng) {
        this.idOng = idOng;
    }

    public Long getIdVoluntario() {
        return idVoluntario;
    }

    public void setIdVoluntario(Long idVoluntario) {
        this.idVoluntario = idVoluntario;
    }
}
