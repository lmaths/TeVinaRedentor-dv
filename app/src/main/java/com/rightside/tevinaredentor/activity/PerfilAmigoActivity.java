package com.rightside.tevinaredentor.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rightside.tevinaredentor.R;
import com.rightside.tevinaredentor.adapter.AdapterGrid;
import com.rightside.tevinaredentor.helper.ConfiguracaoFirebase;
import com.rightside.tevinaredentor.helper.UsuarioFirebase;
import com.rightside.tevinaredentor.model.Postagem;
import com.rightside.tevinaredentor.model.Usuario;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    //teste

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textAmigo, textSeguindo, textView;
    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfilAmigo;

    private String idUsuarioLogado;
    private List<Postagem> postagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //Inicializar componentes
        inicializarComponentes();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Pefil");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Recuperar usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //Configurar referencia postagens usuario
            postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("postagens")
                    .child( usuarioSelecionado.getId() );

            //Configura nome do usuário na toolbar
            getSupportActionBar().setTitle( usuarioSelecionado.getNome() );

            //Recuperar foto do usuário
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if( caminhoFoto != null ){
                Uri url = Uri.parse( caminhoFoto );
                Glide.with(PerfilAmigoActivity.this)
                        .load( url )
                        .into( imagePerfil );
            }

        }

        //Inicializar image loader
        inicializarImageLoader();

        //Carrega as fotos das postagens de um usuário
       // carregarFotosPostagem();

        //Abre a foto clicada
      /*  gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Postagem postagem = postagens.get( position );
                Intent i = new Intent(getApplicationContext(), VisualizarPostagemActivity.class );

                i.putExtra("postagem", postagem );
                i.putExtra("usuario", usuarioSelecionado );

                startActivity( i );

            }
        }); */

    }

    /**
     * Instancia a UniversalImageLoader
     */
    public void inicializarImageLoader() {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init( config );

    }

    public void carregarFotosPostagem(){

        if (usuarioLogado == null) {



        } else {

            //Recupera as fotos postadas pelo usuario
            postagens = new ArrayList<>();
            postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //Configurar o tamanho do grid
                    int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                    int tamanhoImagem = tamanhoGrid / 3;
                   // gridViewPerfil.setColumnWidth( tamanhoImagem );

                    List<String> urlFotos = new ArrayList<>();
                    for( DataSnapshot ds: dataSnapshot.getChildren() ){
                        Postagem postagem = ds.getValue( Postagem.class );
                        postagens.add( postagem );
                        urlFotos.add( postagem.getCaminhoFoto() );
                        Log.i("postagem", "url:" + postagem.getCaminhoFoto() );
                    }

                    //Configurar adapter
                   // adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_postagem, urlFotos );
                   // gridViewPerfil.setAdapter( adapterGrid );

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



    }

    private void recuperarDadosUsuarioLogado(){

        usuarioLogadoRef = usuariosRef.child( idUsuarioLogado );
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Recupera dados de usuário logado
                        usuarioLogado = dataSnapshot.getValue( Usuario.class );

                        /* Verifica se usuário já está seguindo
                           amigo selecionado
                         */
                        verificaSegueUsuarioAmigo();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void verificaSegueUsuarioAmigo(){

        DatabaseReference seguidorRef = seguidoresRef
                .child( usuarioSelecionado.getId() )
                .child( idUsuarioLogado );

        seguidorRef.addListenerForSingleValueEvent(


                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if( dataSnapshot.exists() ){
                            //Já está seguindo
                            Log.i("dadosUsuario", ": Seguindo" );

                        }else {
                            //Ainda não está seguindo
                            Log.i("dadosUsuario", ": seguir" );

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void habilitarBotaoSeguir( boolean segueUsuario ){

        if ( segueUsuario ){
            //buttonAcaoPerfil.setText("Seguindo");
        }else {

            //buttonAcaoPerfil.setText("Seguir");

            //Adiciona evento para seguir usuário
            /*buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Salvar seguidor
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            }); */

        }

    }

    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo){

        /*
        * seguidores
        * id_usuario_selecionado (id amigo)
        *   id_usuario_logado (id usuario logado)
        *       dados logado
        * */
        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
        dadosUsuarioLogado.put("nome", uLogado.getNome() );
        dadosUsuarioLogado.put("caminhoFoto", uLogado.getCaminhoFoto() );
        DatabaseReference seguidorRef = seguidoresRef
                .child( uAmigo.getId() )
                .child( uLogado.getId() );
        seguidorRef.setValue( dadosUsuarioLogado );

        //Alterar botao acao para seguindo
      //  buttonAcaoPerfil.setText("Seguindo");
      //  buttonAcaoPerfil.setOnClickListener(null);

        //Incrementar seguindo do usuário logado
        int seguindo = uLogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo );
        DatabaseReference usuarioSeguindo = usuariosRef
                .child( uLogado.getId() );
        usuarioSeguindo.updateChildren( dadosSeguindo );

        //Incrementar seguidores do amigo
        int seguidores = uAmigo.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores );
        DatabaseReference usuarioSeguidores = usuariosRef
                .child( uAmigo.getId() );
        usuarioSeguidores.updateChildren( dadosSeguidores );


    }

    @Override
    protected void onStart() {
        super.onStart();

        //Recuperar dados do amigo selecionado
        recuperarDadosPerfilAmigo();

        //Recuperar dados usuario logado
        recuperarDadosUsuarioLogado();

    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener( valueEventListenerPerfilAmigo );
    }

    private void recuperarDadosPerfilAmigo(){

        usuarioAmigoRef = usuariosRef.child( usuarioSelecionado.getId() );
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Usuario usuario = dataSnapshot.getValue( Usuario.class );

                        String postagens = String.valueOf( usuario.getPostagens() );

                        String nomeAmigo = String.valueOf(usuario.getNome());

                        String nome = nomeAmigo.toLowerCase();
                        nome = nome.substring(0,1).toUpperCase().concat(nome.substring(1));

                        //Configura valores recuperados
                      textPublicacoes.setText( postagens );

                        textAmigo.setText(nome);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void inicializarComponentes(){
        imagePerfil = findViewById(R.id.imagePerfil);
       // gridViewPerfil = findViewById(R.id.gridViewPerfil);
       // gridViewPerfil.setVisibility(View.GONE);
       buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setVisibility(View.GONE);
       textPublicacoes = findViewById(R.id.textPublicacoes);
        textPublicacoes.setVisibility(View.GONE);
        textView = findViewById(R.id.textView);
        textAmigo = findViewById(R.id.txtNomeUsuario);
        textView.setVisibility(View.GONE);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
