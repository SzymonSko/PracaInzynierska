package com.example.inzynierka;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScannedActivity extends AppCompatActivity {
    private ImageView target_image_show;
    private TextView score;
    private Button btnAdd, btnDelete;
    private EditText wind;
    private Spinner guntype;
    DatabaseReference historyDB, historyKey, historyName;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned);

        target_image_show = findViewById(R.id.target_image_show);
        score = findViewById(R.id.score);
        btnAdd = findViewById(R.id.btnAdd);
        btnDelete = findViewById(R.id.btnDelete);
        wind = findViewById(R.id.wind);
        guntype = findViewById(R.id.guntype);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userID = firebaseUser.getUid();
        historyDB = FirebaseDatabase.getInstance().getReference().child("Registered User");
        historyKey = historyDB.child(userID);
        historyName = historyKey.child("Score History");
        String[] textgun = getResources().getStringArray(R.array.Type_of_gun);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, textgun);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guntype.setAdapter(adapter);
        Python py = Python.getInstance();
        PyObject pyobj2 = py.getModule("scanner");
        PyObject obj2 = pyobj2.callAttr("check");
        PyObject obj3 = pyobj2.callAttr("points");
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        byte[] data = obj2.toJava(byte[].class);
        Bitmap decodeImage = BitmapFactory.decodeByteArray(data, 0, data.length);


        target_image_show.setImageBitmap(decodeImage);
        score.setText("You scored " + obj3.toString() + " points, Congrtaz!");
        String scores = obj3.toString();

        String date = ISO_8601_FORMAT.format(new Date());



        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String windSpeed = wind.getText().toString();
                String gun = guntype.getSelectedItem().toString();

                Bitmap bitmap = ((BitmapDrawable) target_image_show.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                StorageReference fileRef = storageRef.child("image.jpg");
                UploadTask uploadTask = fileRef.putBytes(baos.toByteArray());
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadUri = uri;
                                        String imageUrl = downloadUri.toString();
                                        ScoreHistory scorehistory = new ScoreHistory(date, scores, windSpeed, gun, imageUrl);
                                        historyName.push().setValue(scorehistory);
                                    }
                                });
                                startActivity(new Intent(ScannedActivity.this, MainActivity.class));
                                Toast.makeText(ScannedActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ScannedActivity.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                            }
                        });
            }

        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScannedActivity.this, MainActivity.class));
                Toast.makeText(ScannedActivity.this, "Session is not going to be saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
