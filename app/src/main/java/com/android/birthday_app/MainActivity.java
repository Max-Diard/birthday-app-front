package com.android.birthday_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.birthday_app.databinding.ActivityMainBinding;
import com.android.birthday_app.model.AppUser;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String userString = getIntent().getStringExtra("user");
        if(userString == null){
            SharedPreferences preferences = this.getSharedPreferences("loginFile", Context.MODE_PRIVATE);
            userString = preferences.getString("userData", "");
        }

        try {
            AppUser user = new AppUser(new JSONObject(userString));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(click -> logout());
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("Islogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("Islogin", false).apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}