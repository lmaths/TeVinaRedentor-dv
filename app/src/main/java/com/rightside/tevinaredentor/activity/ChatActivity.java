package com.rightside.tevinaredentor.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.rightside.tevinaredentor.R;
import com.rightside.tevinaredentor.adapter.MensagensAdapter;
import com.rightside.tevinaredentor.helper.ConfiguracaoFirebase;
import com.rightside.tevinaredentor.helper.UsuarioFirebase;
import com.rightside.tevinaredentor.model.Conversasalva;
import com.rightside.tevinaredentor.model.Mensagem;
import com.rightside.tevinaredentor.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    //activity chat
    private TextView textViewNome;
    private CircleImageView circleImageViewFoto;
    private Usuario usuarioDestinatario;
    private ImageView imageCamera;
    private ImageView imageGaleria;
    private EditText editMensagem;
    private StorageReference storage;
    private static final int SELECAO_CAMERA  = 100;
    private static final int SELECAO_GALERIA = 200;
    private DatabaseReference mensagemRef;
    private DatabaseReference database;
    private ChildEventListener childEventListenerMensagens;


    private RecyclerView recyclerMensagens;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    //IDENTIFICAR USUARIOS REMETENTE E DESTINATARIO

    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //CONFIGURAÇÕES INICIAIS
        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageFoto);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);
        imageCamera = findViewById(R.id.imageCamera);
        imageGaleria = findViewById(R.id.imageGaleria);





        //recuperar dados usuario remetente

        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();

        //recuperar dados usuario destinatario

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato"); //cast para usuario destiantario
            textViewNome.setText(usuarioDestinatario.getNome()); //recuperando o nome

            String foto = usuarioDestinatario.getCaminhoFoto(); //recuperando a foto
            if (foto != null) {
                Uri url = Uri.parse(usuarioDestinatario.getCaminhoFoto()); // retornando a url

                Glide.with(ChatActivity.this)
                        .load(url)
                        .into(circleImageViewFoto);
            } else {
                circleImageViewFoto.setImageResource(R.drawable.avatar); //caso não tenha foto usar o avatar padrão
            }

            //recuperar dados usuario destinatario
            idUsuarioDestinatario = usuarioDestinatario.getId();
        }

        //configar adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext());

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);

        database = ConfiguracaoFirebase.getFirebase();
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        mensagemRef = database.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        //evento de clique na camera

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

        imageGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode == RESULT_OK ){

            Bitmap imagem = null;

            try {

                //Valida tipo de seleção da imagem
                switch ( requestCode ){
                    case SELECAO_CAMERA :
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA :
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                //Valida imagem selecionada
                if( imagem != null ){

                    //Converte imagem em byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();


                    //criar nome para imagem

                        String nomeImagem = UUID.randomUUID().toString();    //gerar identificadores unicos

                    // configura referencia firebase

                    StorageReference imagemRef = storage.child("imagens")
                            .child("fotoschat") //criar pasta fotos pasta
                            .child(idUsuarioRemetente)
                            .child(nomeImagem);

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro", " - Erro ao enviar imagem");
                            Toast.makeText(ChatActivity.this, "Erro ao enviar a imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().getResult().toString(); //url da imagem gerada convertida para string uri

                            Mensagem mensagem = new Mensagem();
                            mensagem.setIdUsuario(idUsuarioRemetente);
                            mensagem.setMensagem("imagem.jpeg");
                            mensagem.setImagem( downloadUrl );


                            //salvar para remetente
                            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                            //salvar para destinatario
                            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                            //salvar uma conversa para historico de conversa
                            salvarConversa(mensagem);




                        }
                    });


                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }


    public void enviarMensagem(View view){
        String textoMensagem = editMensagem.getText().toString();

        if (!textoMensagem.isEmpty()) {
            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario(idUsuarioRemetente);
            mensagem.setMensagem(textoMensagem);

            //salvar mensagem para o remetente
                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
            //salvar mensagem para o destinatario
            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
            //salvar ultiam conversa
            salvarConversa(mensagem);


        }else { // se a mensagem estiver vazia
            Toast.makeText(ChatActivity.this,"Digite uma mensagem para enviar!",Toast.LENGTH_LONG).show();
        }
    }

    private void salvarConversa(Mensagem msg) {
        Conversasalva conversaRemetente = new Conversasalva();
        conversaRemetente.setIdRemetente(idUsuarioRemetente);
        conversaRemetente.setIdDestinatario(idUsuarioDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem()); //salva ultima mensagem
        conversaRemetente.setUsuarioExibicao(usuarioDestinatario);


        conversaRemetente.salvar(); //chama o metodo para salvar

    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg) { //metodo para salvar mensagens
        DatabaseReference mensagemRef = database.child("mensagens");
            mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);

        //limpar mensagem do campo
        editMensagem.setText("");
        }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens(); //executa quando é chamado
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagemRef.removeEventListener(childEventListenerMensagens); //para de executar o listener
    }

    private void recuperarMensagens() { //recupera mensagens do nó mensagens firebase
            childEventListenerMensagens = mensagemRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) { //quando um item é adicionado

                    Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                    mensagens.add(mensagem); //adiciona mensagem na lista de mensagens
                    adapter.notifyDataSetChanged(); //atualiza o adapter

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) { //alterado

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) { //removido

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { //movido

                }

                @Override
                public void onCancelled(DatabaseError databaseError) { //erro

                }
            });    //tratamento para atualizar o event listner
        }


}
