package com.example.inzynierka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    Animation top_animation, bot_animation;
    ImageView splash_image;
    TextView splash_welcome, splash_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        top_animation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bot_animation = AnimationUtils.loadAnimation(this,R.anim.bot_animation);

        splash_image = findViewById(R.id.splash_image);
        splash_welcome = findViewById(R.id.splash_welcome);
        splash_text = findViewById(R.id.splash_text);

        splash_image.setAnimation(top_animation);
        splash_welcome.setAnimation(top_animation);
        splash_text.setAnimation(bot_animation);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}