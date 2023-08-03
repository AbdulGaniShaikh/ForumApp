package com.miniproject.forumapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ActivitySplashScreen extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView imageView;
    TextView textView;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spashscreen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        imageView = findViewById(R.id.splashlogo);
        textView = findViewById(R.id.splashtext);

        topAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_bottom_anim);
        bottomAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_top_anim);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    imageView.setAnimation(topAnim);
                    textView.setAnimation(bottomAnim);
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fAuth = FirebaseAuth.getInstance();
                if (fAuth.getCurrentUser()!=null){
                    Intent intent = new Intent(getApplicationContext(), ActivityHome.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(getApplicationContext(), ActivityAuthentication.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        myThread.start();

    }
}