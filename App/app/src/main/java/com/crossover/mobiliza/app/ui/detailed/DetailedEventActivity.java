package com.crossover.mobiliza.app.ui.detailed;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.ui.event.AddEventActivity;
import com.crossover.mobiliza.app.ui.main.MainActivity;
import com.crossover.mobiliza.app.ui.utils.CalendarUtils;
import com.crossover.mobiliza.app.ui.utils.ImageUtils;

import java.util.Calendar;

public class DetailedEventActivity extends AppCompatActivity {

    private static final String TAG = DetailedEventActivity.class.getSimpleName();

    private Long eventId;
    private String googleIdToken;
    private Long idOwner;
    private Long idVoluntario;

    private ProgressDialog mProgressDialog;
    private DetailedEventViewModel mViewModel;

    private TextView nomeEvento;
    private TextView qntConfirmados;
    private LinearLayout ownerOptions;
    private LinearLayout volunteerOptions;
    private Button bConfirmPresenca;
    private Button bRemovePresenca;
    private Button bAddAgenda;
    private ImageView imgView;

    // Agenda
    private String descEvento;
    private String regiaoEvento;
    private String enderecoEvento;
    private Calendar dataEvento;

    private TextView descEventoTV;
    private TextView regiaoEventoTV;
    private TextView enderecoEventoTV;
    private TextView dataEventoTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_event);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_message));
        mProgressDialog.setCancelable(false);

        eventId = getIntent().getLongExtra("idEvent", -1);
        Intent intent = getIntent();
        if (intent.hasExtra("googleIdToken")) {
            googleIdToken = getIntent().getStringExtra("googleIdToken");
        } else {
            googleIdToken = "";
        }

        mViewModel = ViewModelProviders.of(this).get(DetailedEventViewModel.class);
        mViewModel.setEventoId(eventId);
        mViewModel.setGoogleIdToken(googleIdToken);

        // Elementos da Tela
        nomeEvento = findViewById(R.id.detailEventName);
        ownerOptions = findViewById(R.id.ongOwnerOptions);
        volunteerOptions = findViewById(R.id.volunteerOptions);
        bConfirmPresenca = findViewById(R.id.bConfirmPresence);
        bRemovePresenca = findViewById(R.id.bRemovePresence);
        qntConfirmados = findViewById(R.id.detailEventConfirmados);
        bAddAgenda = findViewById(R.id.buttonCalendar);
        imgView = findViewById(R.id.detailEventImage);

        descEventoTV = findViewById(R.id.detailEventDescription);
        regiaoEventoTV = findViewById(R.id.detailEventRegion);
        enderecoEventoTV = findViewById(R.id.detailEventAddress);
        dataEventoTV = findViewById(R.id.detailEventDate);

        if (intent.hasExtra("idOwner")) {
            idOwner = intent.getLongExtra("idOwner", -1);
            ownerOptions.setVisibility(LinearLayout.VISIBLE);
        } else {
            ownerOptions.setVisibility(LinearLayout.GONE);
        }

        if (intent.hasExtra("idVoluntario")) {
            idVoluntario = intent.getLongExtra("idVoluntario", -1);
            volunteerOptions.setVisibility(LinearLayout.VISIBLE);
        } else {
            volunteerOptions.setVisibility(LinearLayout.GONE);
        }

        bAddAgenda.setOnClickListener(this::onAddAgenda);

        Button editarEvento = findViewById(R.id.buttonEditEvent);
        Button deletarEvento = findViewById(R.id.buttonDeletEvent);
        editarEvento.setOnClickListener(this::onEdit);
        deletarEvento.setOnClickListener(this::onConfirmDelete);

        Log.i(TAG, "onCreate: ");
    }

    private void onAddAgenda(View view) {
        String local = regiaoEvento == null ? "" : regiaoEvento;
        if (enderecoEvento != null) local += "\n" + enderecoEvento;
        long milis = dataEvento == null ? Calendar.getInstance().getTimeInMillis() : dataEvento.getTimeInMillis();

        CalendarUtils.startAddEventIntent(this,
                nomeEvento.getText().toString(),
                descEvento,
                local,
                milis);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mViewModel.getEvento(this).observe(this, eventoResource -> {
            if (eventoResource.getStatus() == Resource.Status.SUCCESS && eventoResource.getData() != null) {
                Evento evt = eventoResource.getData();
                mProgressDialog.dismiss();
                nomeEvento.setVisibility(View.VISIBLE);
                nomeEvento.setText(evt.getNome());
                descEventoTV.setVisibility(View.VISIBLE);
                descEventoTV.setText(evt.getDescricao());
                regiaoEventoTV.setVisibility(View.VISIBLE);
                regiaoEventoTV.setText(evt.getRegiao());
                enderecoEventoTV.setVisibility(View.VISIBLE);
                enderecoEventoTV.setText(evt.getEndereco());
                dataEventoTV.setVisibility(View.VISIBLE);
                dataEventoTV.setText(evt.getDataRealizacao());

                if (evt.getIdsConfirmados() != null) {
                    qntConfirmados.setVisibility(View.VISIBLE);
                    qntConfirmados.setText("" + evt.getIdsConfirmados().size());
                }

                // Presença
                if (idVoluntario != null) {
                    if (evt.getIdsConfirmados().contains(idVoluntario)) {
                        bConfirmPresenca.setVisibility(View.GONE);
                        bRemovePresenca.setVisibility(View.VISIBLE);
                    } else {
                        bConfirmPresenca.setVisibility(View.VISIBLE);
                        bRemovePresenca.setVisibility(View.GONE);
                    }
                    bConfirmPresenca.setOnClickListener(this::onConfirmPresence);
                    bRemovePresenca.setOnClickListener(this::onRemovePresence);
                }

                // Imagem
                if (evt.getImg() == null || evt.getImg().isEmpty()) {
                    imgView.setImageBitmap(ImageUtils.getDefaultEventImg());
                } else {
                    try {
                        imgView.setImageBitmap(ImageUtils.getBitmapFromBase64(evt.getImg()));
                    } catch (Exception ex) {
                        Log.e(TAG, "onStart: onSetImg: " + ex.getMessage());
                        imgView.setImageBitmap(ImageUtils.getDefaultEventImg());
                    }
                }

                descEvento = evt.getDescricao();
                regiaoEvento = evt.getRegiao();
                enderecoEvento = evt.getEndereco();
                dataEvento = evt.getDataRealizacaoAsCalendar();

            } else if (eventoResource.getStatus() == Resource.Status.LOADING) {
                mProgressDialog.show();

            } else if (eventoResource.getStatus() == Resource.Status.ERROR) {
                mProgressDialog.dismiss();
                Toast.makeText(this, this.getString(R.string.toast_data_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onEdit(View view) {
        mProgressDialog.show();
        Intent myIntent = new Intent(this, AddEventActivity.class);
        myIntent.putExtra("idEvent", eventId);
        myIntent.putExtra("googleIdToken", googleIdToken);
        this.startActivity(myIntent);
    }

    private void onConfirmDelete(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Deseja deletar este evento?");
        alert.setMessage("Esta ação é irreversível");
        alert.setIcon(android.R.drawable.ic_delete);

        alert.setPositiveButton("Deletar", (dialog, which) -> onDelete(view));
        alert.setNegativeButton("Cancelar", (dialog, which) -> {
            return;
        });

        alert.create();
        alert.show();
    }


    private void onDelete(View view) {
        mProgressDialog.show();
        try {
            mViewModel.deletarEvento(this,
                    ong ->
                    {
                        mProgressDialog.dismiss();
                        Toast.makeText(this, "Evento deletado!", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(this, MainActivity.class);
                        this.startActivity(myIntent);
                    },
                    errorMsg -> {
                        mProgressDialog.dismiss();
                        Toast.makeText(this, this.getString(R.string.toast_delete_error) + ": " + errorMsg, Toast.LENGTH_LONG).show();
                    });
        } catch (Exception e) {
            mProgressDialog.dismiss();
            Toast.makeText(this, this.getString(R.string.toast_save_error) + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void onConfirmPresence(View view) {
        mProgressDialog.show();
        try {
            mViewModel.confirmarEvento(this, true,
                    evento ->
                    {
                        mProgressDialog.dismiss();
                        Toast.makeText(this, "Presença confirmada!", Toast.LENGTH_SHORT).show();
                        bConfirmPresenca.setVisibility(View.GONE);
                        bRemovePresenca.setVisibility(View.VISIBLE);
                        recreate();
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

    private void onRemovePresence(View view) {
        mProgressDialog.show();
        try {
            mViewModel.confirmarEvento(this, false,
                    evento ->
                    {
                        mProgressDialog.dismiss();
                        Toast.makeText(this, "Presença removida", Toast.LENGTH_SHORT).show();
                        bRemovePresenca.setVisibility(View.GONE);
                        bConfirmPresenca.setVisibility(View.VISIBLE);

                        recreate();
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
