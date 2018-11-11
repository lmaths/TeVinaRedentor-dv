package com.rightside.tevinaredentor.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rightside.tevinaredentor.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText passwordEmail;
    private Button resetPassword;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Ajuda para se conectar");
        setSupportActionBar( toolbar );

        passwordEmail = (EditText) findViewById(R.id.etPasswordEmail);
        resetPassword = (Button) findViewById(R.id.buttonPasswordReset);
        firebaseAuth = FirebaseAuth.getInstance();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = passwordEmail.getText().toString().trim();

                if (useremail.equals("")) {
                    Toast.makeText(ResetPasswordActivity.this,"Por favor entre com seu email registrado", Toast.LENGTH_SHORT).show();


                } else {
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this,"Senha resetada, enviada para o email", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(ResetPasswordActivity.this,"Erro ao enviar reset de senha", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });


    }
}
