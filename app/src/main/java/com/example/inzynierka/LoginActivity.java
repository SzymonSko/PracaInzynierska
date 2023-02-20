package com.example.inzynierka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email1, password1;
    private Button btnLogin, btnCreateacc, btnForgot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email1 = findViewById(R.id.email1);
        password1 = findViewById(R.id.password1);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateacc = findViewById(R.id.btnCreateacc);
        btnForgot = findViewById(R.id.btnForgot);
        //Show hiden password
        ImageView imageViewShowPass= findViewById(R.id.ImageView_show_hiden_password);
        imageViewShowPass.setImageResource(R.drawable.show);
        imageViewShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password1.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowPass.setImageResource(R.drawable.show);
                }
                else{
                    password1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowPass.setImageResource(R.drawable.hide);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btnCreateacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
            }
        });
    }

    private void login() {
        String user = email1.getText().toString().trim();
        String pass = password1.getText().toString().trim();

        if(user.isEmpty()){
            email1.setError("Email cannot be empty");

        }
        if(pass.isEmpty()){
            password1.setError("Password cannot be empty");

        }
        if (pass.isEmpty() || user.isEmpty()){
            Toast.makeText(LoginActivity.this, "Email or password are empty", Toast.LENGTH_SHORT).show();
        }
        else

            mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Login Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }
}