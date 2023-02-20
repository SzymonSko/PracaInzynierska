package com.example.inzynierka;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UploadProfilePicture extends AppCompatActivity {
    private ImageView ProfileImage;
    private Button btnChoose, btnUpload;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    int SELECT_IMAGE_CODE = 1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);

        ProfileImage.findViewById(R.id.ProfileImage);
        btnChoose.findViewById(R.id.btnChoose);
        btnUpload.findViewById(R.id.btnUpload);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("ProfilePics");

        Uri uri = firebaseUser.getPhotoUrl();



        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               choosePhoto();
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

            uriImage = data.getData();
            ProfileImage.setImageURI(uriImage);
        }
    }

}
