package com.rightside.tevinaredentor.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.rightside.tevinaredentor.R;
import com.rightside.tevinaredentor.helper.ConfiguracaoFirebase;
import com.rightside.tevinaredentor.helper.UsuarioFirebase;
import com.rightside.tevinaredentor.model.Comentario;
import com.rightside.tevinaredentor.model.Indicador;
import com.rightside.tevinaredentor.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.MyViewHolder> {

    private List<Comentario> listaComentarios;

    private Context context;

    public AdapterComentario(List<Comentario> listaComentarios, Context context) {
        this.listaComentarios = listaComentarios;
        this.context = context;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comentario, parent, false);
        return new AdapterComentario.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

       final Comentario comentario = listaComentarios.get( position );
       final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        holder.nomeUsuario.setText( comentario.getNomeUsuario() );
        holder.comentario.setText( comentario.getComentario() );
        Glide.with(context).load(comentario.getCaminhoFoto()).into(holder.imagemPerfil);



        }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    //


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imagemPerfil;
        TextView nomeUsuario, comentario, qtdindicador;
        LikeButton likeButton2;
        ImageView fotoPostagem;




        public MyViewHolder(View itemView) {
            super(itemView);
            fotoPostagem = itemView.findViewById(R.id.imagePerfilPostagem);
            imagemPerfil = itemView.findViewById(R.id.imageFotoComentario);
            nomeUsuario = itemView.findViewById(R.id.textNomeComentario);
            comentario = itemView.findViewById(R.id.textComentario);



        }
    }

}
