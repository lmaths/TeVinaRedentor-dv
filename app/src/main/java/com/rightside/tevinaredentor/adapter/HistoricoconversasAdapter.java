package com.rightside.tevinaredentor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.rightside.tevinaredentor.model.Conversasalva;

import java.util.List;

public class HistoricoconversasAdapter extends RecyclerView.Adapter<HistoricoconversasAdapter.MyViewHolder> {

    private List<Conversasalva> conversas;
    private Context context;

    public HistoricoconversasAdapter(List<Conversasalva> lista, Context c) {

        this.conversas = lista;
        this.context = c;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    //
    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder (View itemView) {
            super(itemView);
        }
    }
}
