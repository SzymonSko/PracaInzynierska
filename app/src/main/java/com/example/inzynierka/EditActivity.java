package com.example.inzynierka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    private EditText EditUserName, EditMail, ConfPass;
    private Button btnSubmit;
    private String name, mail, join_date, pass, userpass, oldmail, newmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        EditUserName = findViewById(R.id.EditUserName);
        EditMail = findViewById(R.id.EditMail);
        ConfPass = findViewById(R.id.ConfPass);
        btnSubmit = findViewById(R.id.btnSubmit);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();



        showProfile(firebaseUser);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userpass = ConfPass.getText().toString();
                if (userpass.isEmpty()){
                    ConfPass.setError("Password cannot be empty");
                }

                else if(userpass.equals(pass)){
                    updateProfile(firebaseUser);
                }
                else{
                    ConfPass.setError("Password is not matching");
                }
            }
        });
    }



    private void showProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        oldmail = mAuth.getCurrentUser().getEmail();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered User");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails details = snapshot.getValue(UserDetails.class);
                if (details != null) {
                    name = details.name;
                    mail = details.mail;
                    join_date = details.join_date;
                    pass =details.pass;
                    EditUserName.setText(name);
                    EditMail.setText(mail);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditActivity.this, "Something went wrong, try again!", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void updateProfile(FirebaseUser firebaseUser) {
        newmail=EditMail.getText().toString();


   if(EditUserName.toString().isEmpty()){
        EditUserName.setError("User name cannot be empty");
        }
    else if(newmail.isEmpty()){
        EditMail.setError("Email cannot be empty");
        }
    else if(!Patterns.EMAIL_ADDRESS.matcher(EditMail.getText().toString()).matches()){
        Toast.makeText(EditActivity.this, "Wrong format, try again", Toast.LENGTH_SHORT).show();
        }
    else if (!newmail.equals(oldmail)){
        updateEmail(firebaseUser);
        updateprofile(firebaseUser);
    }
    else {
       updateprofile(firebaseUser);
   }

    }

    private void updateprofile(FirebaseUser firebaseUser) {
        name = EditUserName.getText().toString();
        mail = EditMail.getText().toString();

        UserDetails details = new UserDetails(name, mail, join_date, pass);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered User");
        String userID = firebaseUser.getUid();

        reference.child(userID).setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().
                            setDisplayName(name).build();
                    firebaseUser.updateProfile(profileUpdate);

                    Toast.makeText(EditActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

   private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(newmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(EditActivity.this, "Email changed succesfully, check your email to confirm!", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

}