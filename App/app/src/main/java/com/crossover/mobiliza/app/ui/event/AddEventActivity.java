package com.crossover.mobiliza.app.ui.event;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.local.enums.RegiaoEnum;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.ui.utils.ImageUtils;
import com.crossover.mobiliza.app.ui.utils.PermissionUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = AddEventActivity.class.getSimpleName();
    private static final int PICK_IMAGE = 452;
    private static final int ACCEPT_PERMISSION = 453;

    private ProgressDialog mProgressDialog;
    private AddEventViewModel mViewModel;
    private EditText nameText;
    private EditText descricao;
    private ImageButton imageButton;

    /**
     * TODO: inserir captação de dados para os outros atributos de evento: data, endereço, região(possui enum). Olhar a entidade evento.
     */
    private String regiao;
    private Calendar data;

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_message));
        mProgressDialog.setCancelable(false);

        long eventId = getIntent().getLongExtra("idEvent", -1);
        String googleIdToken = getIntent().getStringExtra("googleIdToken");

        //if (eventId >= 0) {
        //Event exists
        mViewModel = ViewModelProviders.of(this).get(AddEventViewModel.class);
        mViewModel.setEventoId(eventId);
        mViewModel.setGoogleIdToken(googleIdToken);

        nameText = findViewById(R.id.eventoNomeText);

        /**
         * TODO: regiao, dataRealizacao e descricao (O QUE ESTÁ AQUI É TEMPORÁRIO, PQ NÃO PODE SER NULO)
         */
        regiao = RegiaoEnum.CENTRO.getText();
        data = new GregorianCalendar(2019, 6, 28, 13, 25);

        descricao = findViewById(R.id.eventoDescricaoText);

        imageButton = findViewById(R.id.eventoImgButton);
        imageButton.setOnClickListener(this::onClickImg);

        Button saveButton = findViewById(R.id.eventSaveButton);
        saveButton.setOnClickListener(this::onSave);

        Log.i(TAG, "onCreate: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.getEvento(this).observe(this, eventoResource -> {
            if (eventoResource.getStatus() == Resource.Status.SUCCESS && eventoResource.getData() != null) {
                Evento evt = eventoResource.getData();

                mProgressDialog.dismiss();
                mViewModel.setEventImg(evt.getImg());

                nameText.setVisibility(View.VISIBLE);

                // Set the fields to contain  the event's information
                nameText.setText(evt.getNome());
                descricao.setText(evt.getDescricao());

                //Imagem
                if (mViewModel.getEventImg() == null || mViewModel.getEventImg().isEmpty()) {
                    imageButton.setImageBitmap(ImageUtils.getDefaultEventImg());
                } else {
                    try {
                        imageButton.setImageBitmap(ImageUtils.getBitmapFromBase64(mViewModel.getEventImg()));
                    } catch (Exception ex) {
                        Log.e(TAG, "onStart: onSetImg: " + ex.getMessage());
                        imageButton.setImageBitmap(ImageUtils.getDefaultEventImg());
                    }
                }

            } else if (eventoResource.getStatus() == Resource.Status.LOADING) {
                mProgressDialog.show();

            } else if (eventoResource.getStatus() == Resource.Status.ERROR) {
                mProgressDialog.dismiss();
                Toast.makeText(this, this.getString(R.string.toast_data_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onClickImg(View view) {
        if (PermissionUtils.startPermissionCheck(this, Manifest.permission.READ_EXTERNAL_STORAGE, ACCEPT_PERMISSION)) {
            ImageUtils.startSelectImageIntent(this, PICK_IMAGE);
        }
    }

    private void onSave(View view) {
        mProgressDialog.show();
        try {
            mViewModel.saveEvent(this, nameText.getText().toString(), regiao, descricao.getText().toString(), data,
                    newEvent -> {
                        mProgressDialog.dismiss();
                        Toast.makeText(this.getApplicationContext(), this.getString(R.string.toast_save_success), Toast.LENGTH_LONG).show();
                        this.finish();
                    },
                    errorMsg -> {
                        mProgressDialog.dismiss();
                        Toast.makeText(this, this.getString(R.string.toast_save_error) + ": " + errorMsg, Toast.LENGTH_LONG).show();
                    });


        } catch (Exception e) {
            mProgressDialog.dismiss();
            Toast.makeText(this, this.getString(R.string.toast_save_error) + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            mProgressDialog.show();
            if (data != null && resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = ImageUtils.getBitmapFromUri(this, data.getData());
                    imageButton.setImageBitmap(bitmap);
                    String base64 = ImageUtils.getImageBase64FromBitmap(bitmap, true, true);
                    mViewModel.setNewEventImg(base64);
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
                    onClickImg(imageButton);
                }
                return;
            }
        }
    }

}
