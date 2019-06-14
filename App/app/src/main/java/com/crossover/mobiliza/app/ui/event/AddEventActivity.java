package com.crossover.mobiliza.app.ui.event;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.converters.Converters;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.local.enums.RegiaoEnum;
import com.crossover.mobiliza.app.data.remote.Resource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = AddEventActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private AddEventViewModel mViewModel;
    private EditText nameText;
    private EditText descricao;
    private String regiao;
    private Calendar data;

    /**
     * TODO: inserir captação de dados para os outros atributos de evento: data, endereço, região(possui enum). Olhar a entidade evento.
     *
     * Vai ficar faltando: validação de dados e confirmação de participantes.
     */

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
        data = new GregorianCalendar(2019,6,28,13,25);

        descricao = findViewById(R.id.eventoDescricaoText);

        mViewModel.getEvento(this).observe(this, eventoResource -> {
            if (eventoResource.getStatus() == Resource.Status.SUCCESS && eventoResource.getData() != null) {
                Evento evt = eventoResource.getData();
                mProgressDialog.dismiss();
                nameText.setVisibility(View.VISIBLE);
                // Set the fields to contain  the event's information
                nameText.setText(evt.getNome());

            } else if (eventoResource.getStatus() == Resource.Status.LOADING) {
                mProgressDialog.show();

            } else if (eventoResource.getStatus() == Resource.Status.ERROR) {
                mProgressDialog.dismiss();
                Toast.makeText(this, this.getString(R.string.toast_data_error), Toast.LENGTH_LONG).show();
            }
        });


        Button saveButton = findViewById(R.id.eventSaveButton);
        saveButton.setOnClickListener(this::onSave);

        Log.i(TAG, "onCreate: ");
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
}
