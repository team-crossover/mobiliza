package com.crossover.mobiliza.app.ui.main.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.ui.utils.ImageUtils;

import java.util.List;

public class AdapterEvents extends RecyclerView.Adapter<AdapterEvents.EventViewHolder> {

    private static final String TAG = AdapterOngs.class.getSimpleName();

    private List<Evento> listaEventos;

    public AdapterEvents(List<Evento> listaEventos) {
        this.listaEventos = listaEventos;
    }

    public Evento getEvento(int position) {
        return listaEventos == null ? null : listaEventos.get(position);
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

        // Imagem
        if (evt.getImg() == null || evt.getImg().isEmpty()) {
            holder.pic.setImageBitmap(ImageUtils.getDefaultEventImg());
        } else {
            try {
                holder.pic.setImageBitmap(ImageUtils.getBitmapFromBase64(evt.getImg()));
            } catch (Exception ex) {
                Log.e(TAG, "onStart: onSetImg: " + ex.getMessage());
                holder.pic.setImageBitmap(ImageUtils.getDefaultEventImg());
            }
        }

    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        private TextView nome;
        private TextView descrição;
        private TextView data;
        private ImageView pic;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNome);
            data = itemView.findViewById(R.id.textData);
            descrição = itemView.findViewById(R.id.textDescricao);
            pic = itemView.findViewById(R.id.adapterEventImage);
        }
    }
}
