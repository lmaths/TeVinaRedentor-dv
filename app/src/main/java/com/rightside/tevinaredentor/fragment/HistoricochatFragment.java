package com.rightside.tevinaredentor.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.rightside.tevinaredentor.R;
import com.rightside.tevinaredentor.activity.ChatActivity;
import com.rightside.tevinaredentor.adapter.HistoricoAdapter;
import com.rightside.tevinaredentor.helper.ConfiguracaoFirebase;
import com.rightside.tevinaredentor.helper.RecyclerItemClickListener;
import com.rightside.tevinaredentor.helper.UsuarioFirebase;
import com.rightside.tevinaredentor.model.Conversasalva;
import com.rightside.tevinaredentor.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoricochatFragment extends Fragment {

    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerViewConversas;
    private List<Conversasalva> listaConversas = new ArrayList<>();
    private HistoricoAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;





    public HistoricochatFragment() {
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historicochat, container, false);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        recyclerViewConversas = view.findViewById(R.id.recyclerListaConversas);

        //configurar adapter

        adapter = new HistoricoAdapter(listaConversas, getActivity());



        //configurar recyclervioew
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager(layoutManager);
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adapter);

        //configura evento de clique

        recyclerViewConversas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewConversas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Conversasalva conversaSelecionada = listaConversas.get(position);
                Intent i = new Intent(getActivity(), ChatActivity.class); // passando a activity chat
                i.putExtra("chatContato", conversaSelecionada.getUsuarioExibicao());
                startActivity(i);


            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));



        //configura conversas ref
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        database = ConfiguracaoFirebase.getFirebase();
        conversasRef = database.child("conversas").child(identificadorUsuario);




        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new ConversaFragment(),"conversa").commit();


            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void recuperarConversas() {

       childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {

               //recuperar conversas

               Conversasalva conversasalva = dataSnapshot.getValue(Conversasalva.class);
               listaConversas.add(conversasalva);

               adapter.notifyDataSetChanged();


           }

           @Override
           public void onChildChanged(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onChildRemoved(DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });






    }


}
