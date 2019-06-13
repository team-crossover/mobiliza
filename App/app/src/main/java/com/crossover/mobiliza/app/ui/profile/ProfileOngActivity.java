package com.crossover.mobiliza.app.ui.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.local.enums.CategoriaEnum;
import com.crossover.mobiliza.app.data.remote.Resource;

public class ProfileOngActivity extends AppCompatActivity {

    private static final String TAG = ProfileOngActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private ProfileOngViewModel mViewModel;
    private EditText nameText;
    private EditText descText;
    private RadioGroup rgCategoria;
    private String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_ong);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_message));
        mProgressDialog.setCancelable(false);

        long ongId = getIntent().getLongExtra("idOng", -1);
        String googleIdToken = getIntent().getStringExtra("googleIdToken");
        if (ongId < 0) {
            this.finish();
            return;
        }

        mViewModel = ViewModelProviders.of(this).get(ProfileOngViewModel.class);
        mViewModel.setOngId(ongId);
        mViewModel.setGoogleIdToken(googleIdToken);

        nameText = findViewById(R.id.ongNameText);
        descText = findViewById(R.id.ongDescricaoText);
        rgCategoria = findViewById(R.id.rgCategoria);
        categoria = new String();
        verifyCategoria();

        mViewModel.getOng(this).observe(this, ongResource -> {
            if (ongResource.getStatus() == Resource.Status.SUCCESS && ongResource.getData() != null) {
                Ong ong = ongResource.getData();
                mProgressDialog.dismiss();
                nameText.setVisibility(View.VISIBLE);
                descText.setVisibility(View.VISIBLE);
                nameText.setText(ong.getNome());
                descText.setText(ong.getDescricao());
                categoria = ong.getCategoria();
                setCategoria();

            } else if (ongResource.getStatus() == Resource.Status.LOADING) {
//                nameText.setVisibility(View.GONE);
//                descText.setVisibility(View.GONE);
                mProgressDialog.show();

            } else if (ongResource.getStatus() == Resource.Status.ERROR) {
                mProgressDialog.dismiss();
//                nameText.setVisibility(View.GONE);
//                descText.setVisibility(View.GONE);
                Toast.makeText(this, this.getString(R.string.toast_data_error), Toast.LENGTH_LONG).show();
            }
        });

        Button saveButton = findViewById(R.id.ongSaveButton);
        saveButton.setOnClickListener(this::onSave);

        Log.i(TAG, "onCreate: ");
    }

    private void onSave(View view) {
        mProgressDialog.show();
        try {
            mViewModel.saveOng(this,
                    nameText.getText().toString(),
                    descText.getText().toString(),
                    categoria,
                    newOng -> {
                        mProgressDialog.dismiss();
                        Toast.makeText(this.getApplicationContext(), this.getString(R.string.toast_save_success), Toast.LENGTH_LONG).show();
                        this.finish();
                    },
                    errorMsg -> {
                        mProgressDialog.dismiss();
                        Toast.makeText(this, this.getString(R.string.toast_save_error) + ": " + errorMsg, Toast.LENGTH_LONG).show();
                    });
        } catch (Exception ex) {
            mProgressDialog.dismiss();
            Toast.makeText(this, this.getString(R.string.toast_save_error) + ": " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void verifyCategoria() {
        rgCategoria.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.rbAnimais:
                    categoria = CategoriaEnum.ANIMAIS.getText();
                    break;
                case R.id.rbEducacao:
                    categoria = CategoriaEnum.EDUCACAO.getText();
                    break;
                case R.id.rbEsporte:
                    categoria = CategoriaEnum.ESPORTE.getText();
                    break;
                case R.id.rbHumanitario:
                    categoria = CategoriaEnum.HUMANITARIO.getText();
                    break;
                case R.id.rbMAmbiente:
                    categoria = CategoriaEnum.MEIO_AMBIENTE.getText();
                    break;
                case R.id.rbTurismo:
                    categoria = CategoriaEnum.TURISMO.getText();
                    break;
                default:
                    categoria = null;
            }
        });
    }

    private void setCategoria() {

        if (categoria.equals(CategoriaEnum.ANIMAIS.getText())) {
            rgCategoria.check(R.id.rbAnimais);

        } else if (categoria.equals(CategoriaEnum.EDUCACAO.getText())) {
            rgCategoria.check(R.id.rbEducacao);

        } else if (categoria.equals(CategoriaEnum.ESPORTE.getText())) {
            rgCategoria.check(R.id.rbEducacao);

        } else if (categoria.equals(CategoriaEnum.HUMANITARIO.getText())) {
            rgCategoria.check(R.id.rbHumanitario);

        } else if (categoria.equals(CategoriaEnum.MEIO_AMBIENTE.getText())) {
            rgCategoria.check(R.id.rbMAmbiente);

        } else if (categoria.equals(CategoriaEnum.TURISMO.getText())) {
            rgCategoria.check(R.id.rbTurismo);
        } else { }

    }


}
