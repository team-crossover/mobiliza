package com.crossover.mobiliza.app.ui.detailed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.ui.event.AddEventActivity;

public class DetailedEventActivity extends AppCompatActivity {

    private static final String TAG = DetailedEventActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private DetailedEventViewModel mViewModel;
    private TextView nomeEvento;
    private TextView qntConfirmados;
    private LinearLayout ownerOptions;
    private LinearLayout volunteerOptions;
    private Button bConfirmPresenca;
    private Button bRemovePresenca;

    private Long eventId;
    private Evento thisEvento;
    private String googleIdToken;
    private Long idOwner;
    private Long idVoluntario;

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


        mViewModel.getEvento(this).observe(this, eventoResource -> {
            if (eventoResource.getStatus() == Resource.Status.SUCCESS && eventoResource.getData() != null) {
                Evento evt = eventoResource.getData();
                mProgressDialog.dismiss();
                nomeEvento.setVisibility(View.VISIBLE);
                nomeEvento.setText(evt.getNome());

                if (evt.getIdsConfirmados() != null) {
                    qntConfirmados.setVisibility(View.VISIBLE);
                    //qntConfirmados.setText("Quantidade de confirmados: " + evt.getIdsConfirmados().size());
                    qntConfirmados.setText("idDonoEvento: " + evt.getIdOng() + " - idDessa Ong: " + idOwner);
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
                    thisEvento = evt;
                    bConfirmPresenca.setOnClickListener(this::onConfirmPresence);
                    bRemovePresenca.setOnClickListener(this::onRemovePresence);
                }

                /**
                 * TODO: Demais informações do evento
                 */

            } else if (eventoResource.getStatus() == Resource.Status.LOADING) {
                mProgressDialog.show();

            } else if (eventoResource.getStatus() == Resource.Status.ERROR) {
                mProgressDialog.dismiss();
                Toast.makeText(this, this.getString(R.string.toast_data_error), Toast.LENGTH_LONG).show();
            }
        });


        Button editarEvento = findViewById(R.id.buttonEditEvent);
        Button deletarEvento = findViewById(R.id.buttonDeletEvent);
        editarEvento.setOnClickListener(this::onEdit);
        deletarEvento.setOnClickListener(this::onDelete);


        Log.i(TAG, "onCreate: ");
    }

    private void onEdit(View view) {
        mProgressDialog.show();
        Intent myIntent = new Intent(this, AddEventActivity.class);
        myIntent.putExtra("idEvent", eventId);
        myIntent.putExtra("googleIdToken", googleIdToken);
        this.startActivity(myIntent);
    }

    private void onDelete(View view) {
        //mProgressDialog.show();
        Toast.makeText(this, "Deletar Evento", Toast.LENGTH_SHORT).show();
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
//                        recreate();
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
                        bConfirmPresenca.setVisibility(View.VISIBLE);
                        bRemovePresenca.setVisibility(View.GONE);
//                        recreate();
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
