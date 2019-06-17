package com.crossover.mobiliza.app.ui.profile;

import android.content.Context;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.repository.OngRepository;

public class ProfileOngViewModel extends ViewModel {

    private long mOngId = -1;
    private String mGoogleIdToken = null;

    public void setOngId(long id) {
        mOngId = id;
    }

    public void setGoogleIdToken(String googleIdToken) {
        mGoogleIdToken = googleIdToken;
    }

    public LiveData<Resource<Ong>> getOng(Context context) {
        return OngRepository.getInstance(context).findById(mOngId);
    }

    public void saveOng(Context context,
                        String nome,
                        String descricao,
                        String categoria,
                        String email,
                        String endereco,
                        String regiao,
                        String telefone,
                        Consumer<Ong> onSuccess,
                        Consumer<String> onFailure) {
        Ong ong = new Ong();
        ong.setId(mOngId);
        ong.setNome(nome);
        ong.setCategoria(categoria);
        ong.setDescricao(descricao);
        ong.setEmail(email);
        ong.setEndereco(endereco);
        ong.setRegiao(regiao);
        ong.setTelefone(telefone);

        OngRepository.getInstance(context).save(ong, mGoogleIdToken, onSuccess, onFailure);
    }
}
