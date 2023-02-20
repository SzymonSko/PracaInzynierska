package com.example.inzynierka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailforgot;
    private Button btnReset;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        mAuth = FirebaseAuth.getInstance();
        emailforgot = findViewById(R.id.emailforgot);
        btnReset = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailforgot.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    emailforgot.setError("Email is required");
                    emailforgot.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgotActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    emailforgot.setError("Valid email is required");
                    emailforgot.requestFocus();
                }
                else{
                    resetPassword();
                }
            }
        });
    }

    private void resetPassword() {
        String email = emailforgot.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
                }
            }
        });
    }
}