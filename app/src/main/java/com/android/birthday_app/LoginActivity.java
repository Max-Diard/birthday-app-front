package com.android.birthday_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.birthday_app.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Button loginButton = binding.buttonLogin;

        loginButton.setOnClickListener(click -> {
            String username = binding.inputUsername.getText().toString();
            String password = binding.inputPassword.getText().toString();
            // Lancer appel API pour authentification
            callApi(username, password);
        });
    }

    private void callApi(String username, String password) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), mediaType);

        this.httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.16:8080/api/v1/login")
                .post(requestBody)
                .build();

        this.httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(LoginActivity.this, "Invalid Credentials : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                if (response.isSuccessful()) {
                    Log.d("TAG", "OK COOL");
                    runOnUiThread(() -> {
                        try {
                            String jsonData = response.body().string();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("user", jsonData);

                            SharedPreferences preferences = getSharedPreferences("loginFile", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isLogin", true);
                            editor.putString("userData", jsonData);
                            editor.apply();

                            startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                } else {
                    runOnUiThread(() -> {
                        String headersError = response.headers().get("errorMessage");
                        Snackbar.make(binding.getRoot(), headersError, Snackbar.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}