package com.crossover.mobiliza.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.crossover.mobiliza.app.data.local.converters.Converters;

import java.util.Calendar;

@Entity(tableName = "voluntarios")
public class Voluntario {

    @PrimaryKey
    private Long id;

    private String nome;

    private String email;

    private String telefone;

    private String dataNascimento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public Calendar getDataNascimentoAsCalendar() {
        return Converters.stringToCalendar(dataNascimento);
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setDataNascimento(Calendar dataNascimento) {
        this.dataNascimento = Converters.calendarToString(dataNascimento);
    }
}
