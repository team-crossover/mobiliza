package com.crossover.mobiliza.app.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;

import java.util.List;

public class AdapterEvents extends RecyclerView.Adapter<AdapterEvents.EventViewHolder> {

    private List<Evento> listaEventos;

    public AdapterEvents(List<Evento> listaEventos) {
        this.listaEventos = listaEventos;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_events_list, parent, false);

        return new EventViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Evento evt = listaEventos.get(position);
        holder.nome.setText(evt.getNome());
        holder.data.setText(evt.getDataRealizacao());
        holder.descrição.setText(evt.getDescricao());

    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        private TextView nome;
        private TextView descrição;
        private TextView data;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNome);
            data = itemView.findViewById(R.id.textData);
            descrição = itemView.findViewById(R.id.textDescricao);
        }
    }
}
