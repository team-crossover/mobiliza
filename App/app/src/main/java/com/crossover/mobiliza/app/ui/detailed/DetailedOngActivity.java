package com.crossover.mobiliza.app.ui.detailed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.remote.Resource;

public class DetailedOngActivity extends AppCompatActivity {

    private static final String TAG = DetailedOngActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private DetailedOngViewModel mViewModel;
    private TextView nomeOng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_ong);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_message));
        mProgressDialog.setCancelable(false);

        long ongId = getIntent().getLongExtra("idOng", -1);

        mViewModel = ViewModelProviders.of(this).get(DetailedOngViewModel.class);
        mViewModel.setOngId(ongId);

        // Elementos da Tela
        nomeOng = findViewById(R.id.detailOngName);

        mViewModel.getOng(this).observe(this, ongResource -> {
            if (ongResource.getStatus() == Resource.Status.SUCCESS && ongResource.getData() != null) {
                Ong ong = ongResource.getData();
                mProgressDialog.dismiss();

                nomeOng.setText(ong.getNome());

            } else if (ongResource.getStatus() == Resource.Status.LOADING) {
                mProgressDialog.show();

            } else if (ongResource.getStatus() == Resource.Status.ERROR) {
                mProgressDialog.dismiss();
                Toast.makeText(this, this.getString(R.string.toast_data_error), Toast.LENGTH_LONG).show();
            }
        });

        Log.i(TAG, "onCreate: ");
    }
}
