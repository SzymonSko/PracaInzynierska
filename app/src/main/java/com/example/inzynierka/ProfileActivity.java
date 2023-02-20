package com.example.inzynierka;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private TextView ProfileWelcome, TextProfile, TextMail, TextDate;
    private String name, mail, join_date;
    private ImageButton btnBack, btnSettings, btnEdit;
    private ImageView ImageViewProfile;
    private Uri uriImage;
    private  int SELECT_IMAGE_CODE = 1;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//        getSupportActionBar().setTitle("Profile");

        ProfileWelcome = findViewById(R.id.ProfileWelcome);
        TextProfile = findViewById(R.id.TextProfile);
        TextMail = findViewById(R.id.TextMail);
        TextDate = findViewById(R.id.TextDate);
        btnBack = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);
        btnEdit = findViewById(R.id.btnEdit);
        ImageViewProfile = findViewById(R.id.ImageViewProfile);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(ProfileActivity.this, "Something went wrong, Try later!", Toast.LENGTH_LONG).show();
        }
        else{
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Fetching data");
            progress.setMessage("Please hold ...");
            progress.show();



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showUserProfile(firebaseUser);
                    progress.cancel();
                }
            }, 2000);


        }
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered User");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails details = snapshot.getValue(UserDetails.class);
                if(details != null){
                    name = details.name;
                    mail = details.mail;
                    join_date = details.join_date;
                    ProfileWelcome.setText("Welcome  "+ name + "!");

                    TextProfile.setText(name);
                    TextMail.setText(mail);
                    TextDate.setText(join_date);

                    Uri uri = firebaseUser.getPhotoUrl();
                    Picasso.get().load(uri).into(ImageViewProfile);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();

//                startActivity(new Intent(ProfileActivity.this, UploadProfilePicture.class));
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditActivity.class));
            }
        });

    }

    private void choosePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pick Image"), SELECT_IMAGE_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Confirm setting avatar");
            builder.setMessage("If You want to set that photo as your avatar click Continue.");
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    uriImage = data.getData();
                    ImageViewProfile.setImageURI(uriImage);
                    upload();


                }

            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                }

            });
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
                }
            });
            dialog.show();



        }
    }

    private void upload() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        StorageReference reference = storageReference.child(mAuth.getCurrentUser().getUid()+(uriImage));

        reference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUri = uri;
                        firebaseUser = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUri).build();
                        firebaseUser.updateProfile(profileUpdates);
                    }
                });
                Toast.makeText(ProfileActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                    }
                });
    }

}