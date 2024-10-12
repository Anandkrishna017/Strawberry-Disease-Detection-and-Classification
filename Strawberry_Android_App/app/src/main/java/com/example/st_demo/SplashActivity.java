package com.example.st_demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 1000; // Splash screen duration in milliseconds

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Post a delayed action to start the main activity after SPLASH_DURATION milliseconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainActivity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish(); // Finish the current activity
            }
        }, SPLASH_DURATION);
    }
}
