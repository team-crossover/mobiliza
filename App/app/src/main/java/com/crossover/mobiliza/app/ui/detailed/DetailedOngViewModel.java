package com.crossover.mobiliza.app.ui.detailed;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.data.remote.repository.OngRepository;

public class DetailedOngViewModel extends ViewModel {

    private long mOngId = -1;

    public void setOngId(long id) {
        mOngId = id;
    }

    public long getOngId() {
        return mOngId;
    }

    public LiveData<Resource<Ong>> getOng(Context context) {
        return OngRepository.getInstance(context).findById(mOngId);
    }
}
