package com.crossover.mobiliza.app.ui.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private String categoria;
    private EditText emailText;
    private EditText enderecoText;
    private String regiao;
    private EditText telefoneText;

    private Spinner categoriaSpinner;
    private Spinner regiaoSpinner;

    private String[] categoriasArray = new String[] {
            CategoriaEnum.ANIMAIS.getText(),
            CategoriaEnum.EDUCACAO.getText(),
            CategoriaEnum.ESPORTE.getText(),
            CategoriaEnum.HUMANITARIO.getText(),
            CategoriaEnum.MEIO_AMBIENTE.getText(),
            CategoriaEnum.TURISMO.getText()
    };

    private String[] regioesArray = new String[] {
            RegiaoEnum.CENTRO.getText(),
            RegiaoEnum.LESTE.getText(),
            RegiaoEnum.NOROESTE.getText(),
            RegiaoEnum.NORTE.getText(),
            RegiaoEnum.OESTE.getText(),
            RegiaoEnum.SUDOESTE.getText(),
            RegiaoEnum.SUL.getText()
    };

    /**
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
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categoriasArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriaSpinner.setAdapter(adapter);

        categoriaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = new String();
                categoria = categoriaSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        emailText = findViewById(R.id.ongEmailText);
        enderecoText = findViewById(R.id.ongEnderecoText);

        regiaoSpinner = findViewById(R.id.spRegiao);
        ArrayAdapter<String> adapterRegiao =new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, regioesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regiaoSpinner.setAdapter(adapterRegiao);

        regiaoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                regiao = new String();
                regiao = regiaoSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

                int indexCategoria = verifySpinnerElement(categoriasArray, ong.getCategoria());
                if (indexCategoria != -1) {
                    categoriaSpinner.setSelection(indexCategoria);
                }

                emailText.setText(ong.getEmail());
                enderecoText.setText(ong.getEndereco());

                int indexRegiao = verifySpinnerElement(regioesArray, ong.getRegiao());
                if (indexRegiao != -1) {
                    regiaoSpinner.setSelection(indexRegiao);
                }

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

    private int verifySpinnerElement(String[] array, String element) {
        int indexArray = 0;

        if (array == null) {
            return -1;
        }

        int len = array.length;
        while (indexArray < len) {
            if (array[indexArray].equals(element)) {
                return indexArray;
            } else {
                indexArray++;
            }
        }

        return -1;
    }


}
