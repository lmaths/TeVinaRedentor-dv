package com.rightside.tevinaredentor.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.rightside.tevinaredentor.R;
import com.rightside.tevinaredentor.fragment.ConversaFragment;
import com.rightside.tevinaredentor.fragment.FeedFragment;
import com.rightside.tevinaredentor.fragment.HistoricochatFragment;
import com.rightside.tevinaredentor.fragment.PerfilFragment;
import com.rightside.tevinaredentor.fragment.PesquisaFragment;
import com.rightside.tevinaredentor.fragment.PostagemFragment;
import com.rightside.tevinaredentor.helper.ConfiguracaoFirebase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    //main
    private FirebaseAuth autenticacao;

    private AdView mAdView;
    private ActionBar actionBar;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");


        //Configura toolbar

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("   Te vi na Redentor");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        AdView adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);







        setSupportActionBar( toolbar );

        //configuracoes de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Configurar bottom navigation view
        configuraBottomNavigationView();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");



    }

    /**
     * Método responsável por criar a BottomNavigation
     */
    private void configuraBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

        //faz configurações iniciais do Bottom Navigation
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);


        //Habilitar navegação
        habilitarNavegacao( bottomNavigationViewEx );

        //configura item selecionado inicialmente
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

    }



    /**
     * Método responsável por tratar eventos de click na BottomNavigation
     * @param viewEx
     */
    private void habilitarNavegacao(BottomNavigationViewEx viewEx){

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()){
                    case R.id.ic_home :
                        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                        actionBar = getSupportActionBar();
                        actionBar.setTitle("  Feed:");
                        mAdView.setVisibility(View.GONE);
                       break;
                   case R.id.ic_pesquisa :
                        fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                        actionBar = getSupportActionBar();
                        actionBar.setTitle(" Pesquisar");
                        mAdView.setVisibility(View.VISIBLE);

                        break;
                    case R.id.ic_postagem :
                        fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                        actionBar = getSupportActionBar();
                        actionBar.setTitle(" Poste suas fotos:");
                        mAdView.setVisibility(View.VISIBLE);

                       break;
                    case R.id.ic_perfil :
                        fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                        actionBar = getSupportActionBar();
                        actionBar.setTitle(" Meu Perfil");
                        mAdView.setVisibility(View.VISIBLE);
                        break;

                        case R.id.ic_chat :
                            actionBar = getSupportActionBar();
                            actionBar.setTitle(" Minhas Conversas");
                            fragmentTransaction.replace(R.id.viewPager, new HistoricochatFragment()).commit();
                            mAdView.setVisibility(View.VISIBLE);
                        break;

                        default:


                            return false;

                        }


                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair :
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;

            case R.id.menu_contato :
                composeEmail();
                break;
        }



        return super.onOptionsItemSelected(item);
    }




    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void composeEmail() {

        String to = "contatorightside@gmail.com";
        String subject = "Gostaria de enviar uma sugestão para o aplicativo Te Vi na Redentor";
        String message = "Gostaria que fosse includo...";

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Por favor, escolha seu aplicativo de e-mail!"));
    }

}
