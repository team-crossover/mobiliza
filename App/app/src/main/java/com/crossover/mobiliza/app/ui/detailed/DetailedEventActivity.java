package com.crossover.mobiliza.app.ui.detailed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.remote.Resource;
import com.crossover.mobiliza.app.ui.event.AddEventActivity;

public class DetailedEventActivity extends AppCompatActivity {

    private static final String TAG = DetailedEventActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private DetailedEventViewModel mViewModel;
    private TextView nomeEvento;
    private LinearLayout ownerOptions;
    private LinearLayout volunteerOptions;

    private Long eventId;
    private String googleIdToken;

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

        if (intent.hasExtra("idOwner")) {
            Long idOwner = intent.getLongExtra("idOwner", -1);
            ownerOptions.setVisibility(LinearLayout.VISIBLE);
        } else {
            ownerOptions.setVisibility(LinearLayout.GONE);
        }

        if (intent.hasExtra("idVoluntario")) {
            Long idVoluntario = intent.getLongExtra("idVoluntario", -1);
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
}
