package com.rightside.tevinaredentor.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rightside.tevinaredentor.R;
import com.rightside.tevinaredentor.model.Conversasalva;
import com.rightside.tevinaredentor.model.Mensagem;
import com.rightside.tevinaredentor.model.Usuario;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversaAdapter extends RecyclerView.Adapter<ConversaAdapter.MyViewHolder> {

    private List<Usuario> contatos; //passando lista de contatos

  private Context context;


    public ConversaAdapter(List<Usuario> listaContatos, Context c) {
        this.contatos = listaContatos;
        this.context = c;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false); //passando o layout para a lista
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Usuario usuario = contatos.get(position);


        holder.nome.setText(usuario.getNome()); //recuperando o nome do usuario e setando


        if ( usuario.getCaminhoFoto() != null) { //SE O USUARIO.CAMINHOFOTO FOR DIFERENTE DE NULO TEMOS UMA FOTO
            Uri uri = Uri.parse(usuario.getCaminhoFoto()); //CONVERTENDO DE STRING PARA URI PARA PASSAR NO PARAMETRO
            Picasso.get().load(uri).into(holder.foto);

       } else  {
            holder.foto.setImageResource(R.drawable.avatar); //CASO NÃO TENHA FOTO UTILIZAR A FOTO PADRÃO
        }



    }

    @Override
    public int getItemCount() {
        return contatos.size() ; //RETORNANDO O TAMANHO DA LISTA DE USUARIOS




    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView nome, ultimamensagem;


        public MyViewHolder(View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoContato); //ids do xml
            nome = itemView.findViewById(R.id.textNomeContato); //paassando ids do xml
            ultimamensagem =itemView.findViewById(R.id.textUltimaMensagem);

            ultimamensagem.setVisibility(View.GONE);


        }
    }
}
