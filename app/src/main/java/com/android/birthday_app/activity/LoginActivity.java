package com.android.birthday_app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.birthday_app.R;
import com.android.birthday_app.databinding.ActivityLoginBinding;
import com.android.birthday_app.util.Constants;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
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
        Button registerButton = binding.buttonRegister;

        loginButton.setOnClickListener(click -> {
            binding.inputPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);
            binding.progressBar.setVisibility(View.VISIBLE);

            String username = binding.inputUsername.getText().toString();
            String password = binding.inputPassword.getText().toString();
            // Lancer appel API pour authentification
            callApi(username, password);
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirection
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
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

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), Constants.APPLICATION_JSON);

        this.httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.ROOT_API + "/login")
                .post(requestBody)
                .build();

        this.httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                binding.progressBar.setVisibility(View.INVISIBLE);

                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Impossible de se connecter, erreur serveur.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                binding.progressBar.setVisibility(View.INVISIBLE);

                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        Log.d("TAG", jsonData);
                        runOnUiThread(() -> {
                            // Récupération et envoie des data liées à ce user dans la MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("user", jsonData);
                            // Mémorisation du status d'authentification et des données utilisateur
                            SharedPreferences preferences = getSharedPreferences("loginFile", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isLogin", true);
                            editor.putString("userData", jsonData);
                            editor.apply();
                            // Lancement de la nouvelle activité
                            startActivity(intent);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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