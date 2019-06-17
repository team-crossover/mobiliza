package com.crossover.mobiliza.app.ui.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.local.enums.CategoriaEnum;
import com.crossover.mobiliza.app.data.local.enums.RegiaoEnum;
import com.crossover.mobiliza.app.data.remote.Resource;

public class ProfileOngActivity extends AppCompatActivity {

    private static final String TAG = ProfileOngActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private ProfileOngViewModel mViewModel;
    private EditText nameText;
    private EditText descText;
    private RadioGroup rgCategoria;
    private String categoria;
    private EditText emailText;
    private EditText enderecoText;
    private RadioGroup rgRegiao;
    private String regiao;
    private EditText telefoneText;

    private Spinner categoriaSpinner;
    private Spinner regiaoSpinner;

    /**
     * TODO: inserir captação de dados para os outros atributos de ong: telefone, e-mail, endereço e região(possui enum). Olhar a entidade Ong.
     * - Para economizar espaço, trocar o radio button dos enums por um spinner (similar ao dropdown).
     * Vai ficar faltando: validação de dados.
     */

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

        categoriaSpinner = findViewById(R.id.spCategoria);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriaSpinner.setAdapter(adapter);

//        rgCategoria = findViewById(R.id.rgCategoria);
//        categoria = new String();
//        verifyCategoria();
        emailText = findViewById(R.id.ongEmailText);
        enderecoText = findViewById(R.id.ongEnderecoText);

        regiaoSpinner = findViewById(R.id.spRegiao);
        ArrayAdapter<CharSequence> adapterRegiao = ArrayAdapter.createFromResource(this,
                R.array.regions_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regiaoSpinner.setAdapter(adapterRegiao);

//        rgRegiao = findViewById(R.id.rgRegiao);
//        regiao = new String();
//        verifyRegiao();
        telefoneText = findViewById(R.id.ongTelefoneText);

        mViewModel.getOng(this).observe(this, ongResource -> {
            if (ongResource.getStatus() == Resource.Status.SUCCESS && ongResource.getData() != null) {
                Ong ong = ongResource.getData();
                mProgressDialog.dismiss();

                nameText.setVisibility(View.VISIBLE);
                descText.setVisibility(View.VISIBLE);
                emailText.setVisibility(View.VISIBLE);
                enderecoText.setVisibility(View.VISIBLE);
                telefoneText.setVisibility(View.VISIBLE);

                nameText.setText(ong.getNome());
                descText.setText(ong.getDescricao());

                categoria = categoriaSpinner.getSelectedItem().toString();
//                categoria = ong.getCategoria();
//                setCategoria();
                emailText.setText(ong.getEmail());
                enderecoText.setText(ong.getEndereco());

                regiao = regiaoSpinner.getSelectedItem().toString();
//                regiao = ong.getRegiao();
//                setRegiao();
                telefoneText.setText(ong.getTelefone());

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
                    emailText.getText().toString(),
                    enderecoText.getText().toString(),
                    regiao,
                    telefoneText.getText().toString(),

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

//    private void verifyCategoria() {
//        rgCategoria.setOnCheckedChangeListener((group, checkedId) -> {
//
//            switch (checkedId) {
//                case R.id.rbAnimais:
//                    categoria = CategoriaEnum.ANIMAIS.getText();
//                    break;
//                case R.id.rbEducacao:
//                    categoria = CategoriaEnum.EDUCACAO.getText();
//                    break;
//                case R.id.rbEsporte:
//                    categoria = CategoriaEnum.ESPORTE.getText();
//                    break;
//                case R.id.rbHumanitario:
//                    categoria = CategoriaEnum.HUMANITARIO.getText();
//                    break;
//                case R.id.rbMAmbiente:
//                    categoria = CategoriaEnum.MEIO_AMBIENTE.getText();
//                    break;
//                case R.id.rbTurismo:
//                    categoria = CategoriaEnum.TURISMO.getText();
//                    break;
//                default:
//                    categoria = null;
//            }
//        });
//    }

//    private void setCategoria() {
//
//        if (categoria.equals(CategoriaEnum.ANIMAIS.getText())) {
//            rgCategoria.check(R.id.rbAnimais);
//
//        } else if (categoria.equals(CategoriaEnum.EDUCACAO.getText())) {
//            rgCategoria.check(R.id.rbEducacao);
//
//        } else if (categoria.equals(CategoriaEnum.ESPORTE.getText())) {
//            rgCategoria.check(R.id.rbEducacao);
//
//        } else if (categoria.equals(CategoriaEnum.HUMANITARIO.getText())) {
//            rgCategoria.check(R.id.rbHumanitario);
//
//        } else if (categoria.equals(CategoriaEnum.MEIO_AMBIENTE.getText())) {
//            rgCategoria.check(R.id.rbMAmbiente);
//
//        } else if (categoria.equals(CategoriaEnum.TURISMO.getText())) {
//            rgCategoria.check(R.id.rbTurismo);
//        } else { }
//
//    }

//    private void setRegiao() {
//
//        if (regiao.equals(RegiaoEnum.CENTRO.getText())) {
//            rgRegiao.check(R.id.rbCentro);
//        } else if (regiao.equals(RegiaoEnum.LESTE.getText())) {
//            rgRegiao.check(R.id.rbLeste);
//        } else if (regiao.equals(RegiaoEnum.NOROESTE.getText())) {
//            rgRegiao.check(R.id.rbNoroeste);
//        } else if (regiao.equals(RegiaoEnum.NORTE.getText())) {
//            rgRegiao.check(R.id.rbNorte);
//        } else if (regiao.equals(RegiaoEnum.OESTE.getText())) {
//            rgRegiao.check(R.id.rbOeste);
//        } else if (regiao.equals(RegiaoEnum.SUDOESTE.getText())) {
//            rgRegiao.check(R.id.rbSudoeste);
//        } else if (regiao.equals(RegiaoEnum.SUL.getText())) {
//            rgRegiao.check(R.id.rbSul);
//        } else {}
//
//    }
//
//    private void verifyRegiao() {
//        rgRegiao.setOnCheckedChangeListener((group, checkedId) -> {
//            switch (checkedId) {
//                case R.id.rbCentro:
//                    regiao = RegiaoEnum.CENTRO.getText();
//                    break;
//                case R.id.rbLeste:
//                    regiao = RegiaoEnum.LESTE.getText();
//                    break;
//                case R.id.rbNoroeste:
//                    regiao = RegiaoEnum.NOROESTE.getText();
//                    break;
//                case R.id.rbNorte:
//                    regiao = RegiaoEnum.NORTE.getText();
//                    break;
//                case R.id.rbOeste:
//                    regiao = RegiaoEnum.OESTE.getText();
//                    break;
//                case R.id.rbSudoeste:
//                    regiao = RegiaoEnum.SUDOESTE.getText();
//                    break;
//                case R.id.rbSul:
//                    regiao = RegiaoEnum.SUL.getText();
//                    break;
//                default:
//                    regiao = null;
//            }
//        });
//    }

}
