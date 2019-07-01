package com.crossover.mobiliza.app.ui.detailed;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.ui.utils.ImageUtils;

public class DetailedOngActivity extends AppCompatActivity {

    private static final String TAG = DetailedOngActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private DetailedOngViewModel mViewModel;
    private TextView nomeOng;
    private TextView descricaoOng;
    private TextView categoriaOng;
    private TextView telefoneOng;
    private TextView emailOng;
    private TextView enderecoOng;
    private TextView regiaoOng;
    private ImageView fotoOng;

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
        descricaoOng = findViewById(R.id.detailOngDescription);
        categoriaOng = findViewById(R.id.detailOngCategory);
        telefoneOng = findViewById(R.id.detailOngTelephone);
        emailOng = findViewById(R.id.detailOngEmail);
        enderecoOng = findViewById(R.id.detailOngAddress);
        regiaoOng = findViewById(R.id.detailOngRegion);
        fotoOng = findViewById(R.id.detailedOngPicture);

        mViewModel.getOng(this).observe(this, ongResource -> {
            if (ongResource.getStatus() == Resource.Status.SUCCESS && ongResource.getData() != null) {
                Ong ong = ongResource.getData();
                mProgressDialog.dismiss();

                nomeOng.setText(ong.getNome());
                descricaoOng.setText(ong.getDescricao());
                categoriaOng.setText(ong.getCategoria());
                telefoneOng.setText(ong.getTelefone());
                emailOng.setText(ong.getEmail());
                enderecoOng.setText(ong.getEndereco());
                regiaoOng.setText(ong.getRegiao());

                // Imagem
                if (ong.getImgPerfil() == null || ong.getImgPerfil().isEmpty()) {
                    fotoOng.setImageBitmap(ImageUtils.getDefaultOngImg());
                } else {
                    try {
                        fotoOng.setImageBitmap(ImageUtils.getBitmapFromBase64(ong.getImgPerfil()));
                    } catch (Exception ex) {
                        Log.e(TAG, "onStart: onSetImg: " + ex.getMessage());
                        fotoOng.setImageBitmap(ImageUtils.getDefaultOngImg());
                    }
                }

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
