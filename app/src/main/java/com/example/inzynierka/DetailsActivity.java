package com.example.inzynierka;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    TextView WelcomeText, DateText, ScoreText, WindText, GunText;
    ImageView HistoryImage;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        WelcomeText = findViewById(R.id.WelcomeText);
        DateText = findViewById(R.id.DateText);
        ScoreText = findViewById(R.id.ScoreText);
        WindText = findViewById(R.id.WindText);
        GunText = findViewById(R.id.GunText);
        HistoryImage = findViewById(R.id.HistoryImage);



        WelcomeText.setText("Shoting data from day: "+getIntent().getStringExtra("udata").toString());
        DateText.setText("Date of shooting: "+getIntent().getStringExtra("udata").toString());
        ScoreText.setText("Score: "+getIntent().getStringExtra("uscore").toString());
        WindText.setText("Wind speed: " +getIntent().getStringExtra("uwindSpeed").toString());
        GunText.setText("Type of gun: "+getIntent().getStringExtra("ugun").toString());
        String imageUrl = getIntent().getStringExtra("uimage");
        Glide.with(DetailsActivity.this).load(imageUrl).into(HistoryImage);



    }

}