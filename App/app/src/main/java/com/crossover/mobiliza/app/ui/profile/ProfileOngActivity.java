package com.crossover.mobiliza.app.ui.profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Ong;
import com.crossover.mobiliza.app.data.local.enums.CategoriaEnum;
import com.crossover.mobiliza.app.data.local.enums.RegiaoEnum;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.ui.utils.ImageUtils;
import com.crossover.mobiliza.app.ui.utils.PermissionUtils;

import java.io.IOException;

public class ProfileOngActivity extends AppCompatActivity {

    private static final String TAG = ProfileOngActivity.class.getSimpleName();
    private static final int PICK_IMAGE = 452;
    private static final int ACCEPT_PERMISSION = 453;

    private ProgressDialog mProgressDialog;
    private ProfileOngViewModel mViewModel;
    private EditText nameText;
    private EditText descText;
    private String categoria;
    private EditText emailText;
    private EditText enderecoText;
    private String regiao;
    private EditText telefoneText;
    private Button deleteBtn;
    private ImageButton imgPerfilButton;

    private Spinner categoriaSpinner;
    private Spinner regiaoSpinner;

    private String[] categoriasArray = new String[]{
            CategoriaEnum.ANIMAIS.getText(),
            CategoriaEnum.EDUCACAO.getText(),
            CategoriaEnum.ESPORTE.getText(),
            CategoriaEnum.HUMANITARIO.getText(),
            CategoriaEnum.MEIO_AMBIENTE.getText(),
            CategoriaEnum.TURISMO.getText()
    };

    private String[] regioesArray = new String[]{
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
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
        ArrayAdapter<String> adapterRegiao = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, regioesArray);
        adapterRegiao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regiaoSpinner.setAdapter(adapterRegiao);

        regiaoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                regiao = regiaoSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        telefoneText = findViewById(R.id.ongTelefoneText);

        imgPerfilButton = findViewById(R.id.ongImgButton);
        imgPerfilButton.setOnClickListener(this::onClickImg);

        Button saveButton = findViewById(R.id.ongSaveButton);
        saveButton.setOnClickListener(this::onSave);

        Log.i(TAG, "onCreate: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.getOng(this).observe(this, ongResource -> {
            if (ongResource.getStatus() == Resource.Status.SUCCESS && ongResource.getData() != null) {
                Ong ong = ongResource.getData();
                mProgressDialog.dismiss();
                mViewModel.setOngImg(ong.getImgPerfil());

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

                //Imagem
                if (mViewModel.getOngImg() == null || mViewModel.getOngImg().isEmpty()) {
                    imgPerfilButton.setImageBitmap(ImageUtils.getDefaultOngImg());
                } else {
                    try {
                        imgPerfilButton.setImageBitmap(ImageUtils.getBitmapFromBase64(mViewModel.getOngImg()));
                    } catch (Exception ex) {
                        Log.e(TAG, "onStart: onSetImg: " + ex.getMessage());
                        imgPerfilButton.setImageBitmap(ImageUtils.getDefaultOngImg());
                    }
                }

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
        super.onStart();
    }

    private void onClickImg(View view) {
        if (PermissionUtils.startPermissionCheck(this, Manifest.permission.READ_EXTERNAL_STORAGE, ACCEPT_PERMISSION)) {
            ImageUtils.startSelectImageIntent(this, PICK_IMAGE);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            mProgressDialog.show();
            if (data != null && resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = ImageUtils.getBitmapFromUri(this, data.getData());
                    imgPerfilButton.setImageBitmap(bitmap);
                    String base64 = ImageUtils.getImageBase64FromBitmap(bitmap, true, true);
                    mViewModel.setNewOngImg(base64);
                    mProgressDialog.dismiss();
                } catch (IOException e) {
                    Log.e(TAG, "onActivityResult: " + e.getMessage(), e);
                    Toast.makeText(this, this.getString(R.string.toast_upload_error) + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            } else {
                Log.e(TAG, "onActivityResult: no data");
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCEPT_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Try to open image again
                    onClickImg(imgPerfilButton);
                }
                return;
            }
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
