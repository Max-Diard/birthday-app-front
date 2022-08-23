package com.android.birthday_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash_sreen);
        if (true) {
            startActivity(new Intent(this, LoginActivity.class));

        } else {
            startActivity(new Intent(this, MainActivity.class));

        }
        finish();
    }
}