package com.example.inzynierka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private String name, mail, join_date, pass;
    private EditText newPass, confPass, oldPass;
    private Button btnChange, btnDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        newPass = findViewById(R.id.newPass);
        confPass = findViewById(R.id.confPass);
        oldPass = findViewById(R.id.oldPass);
        btnChange = findViewById(R.id.btnChange);
        btnDelete = findViewById(R.id.btnDelete);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userID = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered User");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails details = snapshot.getValue(UserDetails.class);
                if(details != null){
                    pass = details.pass;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (firebaseUser.equals("")) {
            Toast.makeText(SettingsActivity.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
        }

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass(firebaseUser);

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();


            }
        });
    }

    private void showAlert() {
        String oldPwd = oldPass.getText().toString();
        if (oldPwd.equals(pass)) {


            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle("Confirm removing data");
            builder.setMessage("If You want to delete your profile and all related data click Continue. You can undo that action");
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    deleteAcc(firebaseUser);
                    deleteUserData();


                }

            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }

            });
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
                }
            });
            dialog.show();

        }
        else{
            Toast.makeText(SettingsActivity.this, "Wrong Password", Toast.LENGTH_LONG).show();
        }
    }


    private void deleteAcc(FirebaseUser firebaseUser) {

            firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mAuth.signOut();

                        Toast.makeText(SettingsActivity.this, "Account hes been deleted", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(SettingsActivity.this, "Failed deleting user", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
    }

    private void deleteUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered User");
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        reference.child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void changePass(FirebaseUser firebaseUser) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered User");
        String userID = firebaseUser.getUid();

        String newPwd = newPass.getText().toString();
        String confPwd = confPass.getText().toString();
        String oldPwd = oldPass.getText().toString();

        if (newPwd.isEmpty()) {
            newPass.setError("Password cannot be empty");
        }
        else if (confPwd.isEmpty()) {
            confPass.setError("Confirmation cannot be empty");
        }
        else if (!newPwd.equals(confPwd)) {
            confPass.setError("Password is not matching");
        }
        else if (oldPwd.equals(newPwd)) {
           oldPass.setError("Old password is same as new one!");
        } else if (oldPwd.equals(pass)) {

            firebaseUser.updatePassword(newPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Password has benn changed", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        HashMap hashMap = new HashMap();
                        hashMap.put("pass", newPwd);
                        reference.child(userID).updateChildren(hashMap);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(SettingsActivity.this, "Provided password is not matching user", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
  }
}