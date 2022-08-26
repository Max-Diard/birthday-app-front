package com.android.birthday_app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.birthday_app.R;
import com.android.birthday_app.databinding.ActivityMainBinding;
import com.android.birthday_app.databinding.ActivityRegisterBinding;
import com.android.birthday_app.util.Constants;
import com.android.birthday_app.util.Util;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button buttonRegister = binding.registerButton;

        buttonRegister.setOnClickListener(view -> {
            String username = binding.inputUsername.getText().toString();
            String email = binding.inputEmail.getText().toString();
            String password = binding.inputPassword.getText().toString();
            String confirmation = binding.inputConfirmation.getText().toString();

            if (username != null && username.length() >= 3) {
                if (Util.validateEmail(email)) {
                    if (Util.checkPasswordAndConfirmation(password, confirmation)) {
                        // Construction de l'objet qui transitera dans le body de la requête POST
                        JSONObject jsonObject = new JSONObject();
                        Log.d("TAG", password);
                        try {
                            jsonObject.put("appUser", null);
                            jsonObject.put("username", username);
                            jsonObject.put("password", password);
                            jsonObject.put("email", email);
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        Log.d("TAG", jsonObject.toString());
                        // Appel API
                        callApi(jsonObject);
                    } else {
                        String passwordError = "Password and confirmation must be the same and at least 5 characters long.";
                        Snackbar.make(binding.getRoot(), passwordError, Snackbar.LENGTH_LONG).show();
                        binding.inputPassword.setError(passwordError);
                        binding.inputConfirmation.setError(passwordError);
                    }
                } else {
                    String emailError = "Please enter a valid email.";
                    Snackbar.make(binding.getRoot(), emailError, Snackbar.LENGTH_LONG).show();
                    binding.inputEmail.setError(emailError);
                }
            } else {
                String usernameError = "Username must be at least 3 characters long.";
                Snackbar.make(binding.getRoot(), usernameError, Snackbar.LENGTH_LONG).show();
                binding.inputUsername.setError(usernameError);
            }
        });
    }

    public void callApi(JSONObject jsonObject) {
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), Constants.APPLICATION_JSON);

        Request request = new Request
                .Builder()
                .url(Constants.ROOT_API + "/users")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(RegisterActivity.this,  e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Redirection vers Login
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String headersError = response.headers().get("errorMessage");
                            Snackbar.make(binding.getRoot(), headersError, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}