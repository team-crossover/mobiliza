package com.crossover.mobiliza.app.ui.event;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
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

public class AddEventActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddEventActivity.class.getSimpleName();
    private static final int PICK_IMAGE = 452;
    private static final int ACCEPT_PERMISSION = 453;

    private ProgressDialog mProgressDialog;
    private AddEventViewModel mViewModel;
    private EditText nameText;
    private EditText descricao;
    private ImageButton imageButton;
    private EditText enderecoText;
    private EditText eventoData;
    private EditText eventoHora;

    private Spinner regiaoSpinner;

    private String[] regioesArray = new String[]{
            RegiaoEnum.CENTRO.getText(),
            RegiaoEnum.LESTE.getText(),
            RegiaoEnum.NOROESTE.getText(),
            RegiaoEnum.NORTE.getText(),
            RegiaoEnum.OESTE.getText(),
            RegiaoEnum.SUDOESTE.getText(),
            RegiaoEnum.SUL.getText()
    };

    private int mYear, mMonth, mDay, mHour, mMinute;
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
        descricao = findViewById(R.id.eventoDescricaoText);
        enderecoText = findViewById(R.id.eventoEnderecoText);
        eventoData = (EditText) findViewById(R.id.eventoData);
        eventoHora = (EditText) findViewById(R.id.eventoHora);

        eventoData.setOnClickListener(this);
        eventoHora.setOnClickListener(this);

        regiaoSpinner = findViewById(R.id.spEventoRegiao);
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

        imageButton = findViewById(R.id.eventoImgButton);
        imageButton.setOnClickListener(this::onClickImg);

        Button saveButton = findViewById(R.id.eventSaveButton);
        saveButton.setOnClickListener(this::onSave);

        //Data automática inicial para o dia seguinte
        if ((eventoData.getText() == null || eventoData.getText().toString().isEmpty()) && (eventoHora.getText() == null || eventoHora.getText().toString().isEmpty())) {
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);
            data = tomorrow;

            mYear = data.get(Calendar.YEAR);
            mMonth = data.get(Calendar.MONTH);
            mDay = data.get(Calendar.DAY_OF_MONTH);
            eventoData.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);

            mHour = data.get(Calendar.HOUR_OF_DAY);
            mMinute = data.get(Calendar.MINUTE);
            eventoHora.setText(mHour + ":" + mMinute);

        }

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
                enderecoText.setVisibility(View.VISIBLE);

                // Set the fields to contain  the event's information
                nameText.setText(evt.getNome());
                descricao.setText(evt.getDescricao());
                enderecoText.setText(evt.getEndereco());

                data = evt.getDataRealizacaoAsCalendar();
                mYear = data.get(Calendar.YEAR);
                mMonth = data.get(Calendar.MONTH);
                mDay = data.get(Calendar.DAY_OF_MONTH);
                eventoData.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);

                mHour = data.get(Calendar.HOUR_OF_DAY);
                mMinute = data.get(Calendar.MINUTE);
                eventoHora.setText(mHour + ":" + mMinute);

                eventoData.setOnClickListener(this);
                eventoHora.setOnClickListener(this);

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

                int indexRegiao = verifySpinnerElement(regioesArray, evt.getRegiao());
                if (indexRegiao != -1) {
                    regiaoSpinner.setSelection(indexRegiao);
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

            if ((eventoData.getText() == null || eventoData.getText().toString().isEmpty()) && (eventoHora.getText() == null || eventoHora.getText().toString().isEmpty())) {
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DAY_OF_YEAR, 1);
                data = tomorrow;

            } else {
                //Tratamento de data feito para a formatação aprorpiada para o Calendar
                String[] dataString, horaString;
                dataString = eventoData.getText().toString().split("/");
                horaString = eventoHora.getText().toString().split(":");

                int dia = Integer.parseInt(dataString[0]);
                int mes = Integer.parseInt(dataString[1]) - 1;
                int ano = Integer.parseInt(dataString[2]);
                int horas = Integer.parseInt(horaString[0]);
                int minutos = Integer.parseInt(horaString[1]);

                data = new GregorianCalendar(ano, mes, dia, horas, minutos);
            }

            mViewModel.saveEvent(this, nameText.getText().toString(), regiao, descricao.getText().toString(), enderecoText.getText().toString(), data,
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

    @Override
    public void onClick(View view) {

        if (view == eventoData) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            eventoData.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (view == eventoHora) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            eventoHora.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}
