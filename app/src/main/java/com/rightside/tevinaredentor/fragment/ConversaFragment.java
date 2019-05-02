package com.rightside.tevinaredentor.fragment;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rightside.tevinaredentor.R;
import com.rightside.tevinaredentor.activity.ChatActivity;
import com.rightside.tevinaredentor.adapter.ConversaAdapter;
import com.rightside.tevinaredentor.helper.ConfiguracaoFirebase;
import com.rightside.tevinaredentor.helper.RecyclerItemClickListener;
import com.rightside.tevinaredentor.helper.UsuarioFirebase;
import com.rightside.tevinaredentor.model.Usuario;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class  ConversaFragment extends Fragment {

    private RecyclerView recyclerViewListaContatos; // RECYCLER VIEW
    private ConversaAdapter adapter;
    private ArrayList<Usuario> listaContatos = new ArrayList<>(); //instancia iniciada da classe usuario
    private DatabaseReference usuariosRef; //REFERENCIA DO FIREBASE
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser usuarioAtual;



    public ConversaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversa, container, false);
        recyclerViewListaContatos =  view.findViewById(R.id.recyclerViewListaContatos);
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios"); //REFERENCIA DA TABELA DO FIREBASE USUARIOS
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();



        //CONFIGURAR O ADAPTADOR
        adapter = new ConversaAdapter(listaContatos, getActivity() ); //instancia do convwersa adapter

        //CONFIGURAR O RECYCLER VIEW
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerViewListaContatos.setLayoutManager( layoutManager );
        recyclerViewListaContatos.setHasFixedSize(true);
        recyclerViewListaContatos.setAdapter( adapter );

        //configurar evento de click recycler view
        recyclerViewListaContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerViewListaContatos, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listaContatos.get(position);
                        Intent i = new Intent(getActivity(), ChatActivity.class); // passando a activity chat
                        i.putExtra("chatContato", usuarioSelecionado);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                })
        );
       return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos(); //carregar o fragment, recuperamos os contatos
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerContatos); //quando não executa o fragment removemos para não ficar executando
    }

    public void recuperarContatos() { //recuperar os usuarios firebase

        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dados: dataSnapshot.getChildren()){


                    Usuario usuario = dados.getValue(Usuario.class); //retorno do tipo usuario
                    String nomeatual = usuarioAtual.getDisplayName();
                    if (!nomeatual.equals(usuario.getNome())) //NÃO ADICIONAR USUARIO LOGADO COMO CONTATO
                    listaContatos.add(usuario);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
