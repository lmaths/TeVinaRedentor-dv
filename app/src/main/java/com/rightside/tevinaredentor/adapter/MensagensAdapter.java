package com.rightside.tevinaredentor.adapter;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rightside.tevinaredentor.R;
import com.rightside.tevinaredentor.helper.UsuarioFirebase;
import com.rightside.tevinaredentor.model.Mensagem;

import org.w3c.dom.Text;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;


    public MensagensAdapter(List<Mensagem> lista, Context c) {
        this.mensagens = lista;
        this.context = c;
    }
    //

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { //parametro viewType, define o tipo da view que quero retornar

        View item = null;

        //verifica qual dos layouts utilizar

        if (viewType == TIPO_REMETENTE) { //retornar adapter remetente

            item = LayoutInflater.from((parent.getContext())).inflate(R.layout.adapter_mensagem_remetente, parent, false);

        }else if (viewType == TIPO_DESTINATARIO) { //retornar adapter destinatario

            item = LayoutInflater.from((parent.getContext())).inflate(R.layout.adapter_mensagem_destinatario, parent, false);
        }

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Mensagem mensagem = mensagens.get(position); //recuepera a mensagem de acordo com o position
        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImagem();

        if ( imagem != null ) { //exibe imagem
            Uri url = Uri.parse(imagem); //converter de string para URI
            Glide.with(context).load(url).into(holder.imagem); //imagemensagemfoto

            //esconder o texto
            holder.mensagem.setVisibility(View.GONE);


        }else { //caso não tenha imagem exibe texto
            holder.mensagem.setText( msg );
            holder.imagem.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mensagens.size(); //retornar quantidade de mensagens no contador
    }

    @Override
    public int getItemViewType(int position) {  //para cada elemento da lista teremos uma posição, utlizar o metodo para retornar o tipo de view para cada mensagem

        Mensagem mensagem = mensagens.get(position); //recuepera a mensagem de acordo com o position

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario(); //retornar strin de id usuario

        if (idUsuario.equals(mensagem.getIdUsuario())){      //caso o id do usuario logado for igual ao id usuario da mensagem, retorna tipo remetente
                              return TIPO_REMETENTE;
                              }
                              return TIPO_DESTINATARIO; //retorna tipo destinatario da mensagem.



    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView mensagem;
            ImageView imagem;

        public MyViewHolder(View itemView) {
            super(itemView);

            mensagem = itemView.findViewById(R.id.textMensagemTexto); //pegar id textmensagemtexto
            imagem = itemView.findViewById(R.id.imageMensagemFoto); //recupera foto


        }
    }
}
