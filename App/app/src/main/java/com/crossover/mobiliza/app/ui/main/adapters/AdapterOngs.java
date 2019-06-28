package com.crossover.mobiliza.app.ui.main.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Ong;

import java.util.List;

public class AdapterOngs extends RecyclerView.Adapter<AdapterOngs.OngViewHolder> {

    private List<Ong> listaOngs;

    public AdapterOngs(List<Ong> listaOngs) {
        this.listaOngs = listaOngs;
    }

    public Ong getOng(int position) {
        return listaOngs == null ? null : listaOngs.get(position);
    }

    @NonNull
    @Override
    public OngViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_ongs_list, parent, false);

        return new OngViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull OngViewHolder holder, int position) {
        Ong ong = listaOngs.get(position);
        holder.nome.setText(ong.getNome());
        holder.descrição.setText(ong.getDescricao());
        holder.categoria.setText(ong.getCategoria());

    }

    @Override
    public int getItemCount() {
        return listaOngs.size();
    }

    public class OngViewHolder extends RecyclerView.ViewHolder {

        private TextView nome;
        private TextView descrição;
        private TextView categoria;

        public OngViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeOng);
            descrição = itemView.findViewById(R.id.textDescricaoOng);
            categoria = itemView.findViewById(R.id.textCategoriaOng);
        }
    }
}
