package com.example.inzynierka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email, password, repassword;

    private Button btnRegister, btnLogToExist;
    private ImageView ImageView_show_hiden_password, ImageView_show_hiden_password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogToExist = findViewById(R.id.btnLogToExist);

        //Show hiden password
        ImageView imageViewShowPass= findViewById(R.id.ImageView_show_hiden_password);
        imageViewShowPass.setImageResource(R.drawable.show);
        imageViewShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowPass.setImageResource(R.drawable.show);
                }
                else{
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowPass.setImageResource(R.drawable.hide);
                }
            }
        });
        ImageView imageViewShowPass2= findViewById(R.id.ImageView_show_hiden_password2);
        imageViewShowPass2.setImageResource(R.drawable.show);
        imageViewShowPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    repassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowPass2.setImageResource(R.drawable.show);
                }
                else{
                    repassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowPass2.setImageResource(R.drawable.hide);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = username.getText().toString();
                String mail = email.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String join_date = sdf.format(new Date());

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(repass)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();

                }
                else if(mail.isEmpty()){
                    email.setError("Email cannot be empty");
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                    Toast.makeText(RegisterActivity.this, "Wrong format, try again", Toast.LENGTH_SHORT).show();
                }
                else if(pass.isEmpty()){
                    password.setError("Password  cannot be empty");
                }
                else if(!pass.equals(repass)){
                    repassword.setError( "Passwords did not match");
                }
                else if(pass.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password needs to be longer then 6 characters", Toast.LENGTH_SHORT).show();
                }
                else{
                    register(name, mail, pass, repass, join_date);
                }
            }
        });
        btnLogToExist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });


    }

    private void register(String name, String mail, String pass, String repass, String join_date) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    UserDetails details = new UserDetails(name, mail, join_date, pass);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered User");

                    reference.child(firebaseUser.getUid()).setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "Registred succesfuly, Check your mail", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Registred failed, Try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}