package com.rightside.tevinaredentor.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.rightside.tevinaredentor.R;
import com.rightside.tevinaredentor.helper.ConfiguracaoFirebase;
import com.rightside.tevinaredentor.helper.UsuarioFirebase;
import com.rightside.tevinaredentor.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.sql.Array;
import java.util.Arrays;

public class LoginActivity extends IntroActivity {

    //login

    private EditText campoEmail, campoSenha;
    private TextView txt;
    private Button botaoEntrar;
    private ProgressBar progressBar;
    private CadastroActivity cadastro;
    private Usuario usuario;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private LoginButton loginButton;
    private CallbackManager callbackManager;



    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
     setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        verificarUsuarioLogado();
        inicializarComponentes();




       //facebook login

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile");

        //Fazer login do usuario
       progressBar.setVisibility( View.GONE );
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoEmail = campoEmail.getText().toString();
                String textosenha = campoSenha.getText().toString();

                if( !textoEmail.isEmpty() ){
                    if( !textosenha.isEmpty() ){

                        usuario = new Usuario();
                        usuario.setEmail( textoEmail );
                        usuario.setSenha( textosenha );
                        validarLogin( usuario );

                    }else{
                        Toast.makeText(LoginActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,
                            "Preencha o e-mail!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void verificarUsuarioLogado(){
       autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
       if( autenticacao.getCurrentUser() != null ){
           startActivity(new Intent(getApplicationContext(), MainActivity.class));
           finish();
       }
    }

    public void validarLogin( Usuario usuario ){

        progressBar.setVisibility( View.VISIBLE );
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ){
                    progressBar.setVisibility( View.GONE );
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this,
                            "Erro ao fazer login",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility( View.GONE );
                }

            }
        });


    }

    public void abrirCadastro(View view){
        Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity( i );
    }

    public void abrirPassword (View view) {
        Intent i = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(i);
    }

    public void inicializarComponentes(){

        campoEmail   = findViewById(R.id.editLoginEmail);
        campoSenha   = findViewById(R.id.editLoginSenha);
        botaoEntrar  = findViewById(R.id.buttonEntrar);
        progressBar  = findViewById(R.id.progressLogin);
        loginButton = findViewById(R.id.login_button);





    }


    public void buttonclickLoginFb(View v) {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser myuserobj = auth.getCurrentUser();
                    String idUsuario = task.getResult().getUser().getUid();

                    if (autenticacao.getCurrentUser() != null) {
                        progressBar.setVisibility( View.VISIBLE );
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Toast.makeText(getApplicationContext(), "Bem vindo novamente", Toast.LENGTH_SHORT).show();

                    } else {
                        progressBar.setVisibility( View.VISIBLE );
                        usuario = new Usuario();
                        usuario.setEmail(myuserobj.getEmail());
                        usuario.setNome(myuserobj.getDisplayName());
                        usuario.setId( idUsuario );
                        usuario.salvar();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Toast.makeText(getApplicationContext(), "Cadastro realizado, bem vindo!!!", Toast.LENGTH_SHORT).show();

                    }





                } else {
                    Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);


        super.onActivityResult(requestCode, resultCode, data);

    }

    private void UpdateUI(FirebaseUser myuserobj) {
        txt.setText(myuserobj.getEmail());


    }


}
