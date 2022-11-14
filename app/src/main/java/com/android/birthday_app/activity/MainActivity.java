package com.android.birthday_app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.birthday_app.R;
import com.android.birthday_app.adapter.BirthdayAdapter;
import com.android.birthday_app.adapter.BirthdayItem;
import com.android.birthday_app.adapter.ListItem;
import com.android.birthday_app.adapter.MonthItem;
import com.android.birthday_app.databinding.ActivityMainBinding;
import com.android.birthday_app.model.AppUser;
import com.android.birthday_app.model.Birthday;
import com.android.birthday_app.util.Constants;
import com.android.birthday_app.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private List<ListItem> mListItem;
    private AppUser user;
    private BirthdayAdapter birthdayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Récupération des data liées à l'utilisateur connecté
        String userString = getIntent().getStringExtra("user");
        // Si c'est null c'est qu'on est pas passé par login activity, donc on récupère les données utilisateur dans le fichier SharedPreference
        if (userString == null) {
            SharedPreferences preferences = this.getSharedPreferences("loginFile", Context.MODE_PRIVATE);
            userString = preferences.getString("userData", "");
        }

        try {
            this.user = new AppUser(new JSONObject(userString));
            mListItem = getListItem(this.user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        birthdayAdapter = new BirthdayAdapter(this, mListItem);
        recyclerView.setAdapter(birthdayAdapter);

        FloatingActionButton addBirthday = binding.addBirthday;
        addBirthday.setOnClickListener(this::addBirthdayDialogBuilder);
    }


    private List<ListItem> getListItem(AppUser user) {
        List<ListItem> listItem = new ArrayList<>();

        List<Birthday> birthdayList = user.getBirthdays();
        List<String> monthList = Arrays.asList(new DateFormatSymbols(Locale.FRENCH).getMonths());

        //On itére sur la liste des mois
        for (int i = 0; i < monthList.size(); i++) {
            List<Birthday> birthdaysInMonth = new ArrayList<>();
            String month = monthList.get(i).toUpperCase();
            // On ajoute le mois à la liste générale
            listItem.add(new MonthItem(i + 1, month));

            // Ont itére sur la liste des anniversaires
            for (int j = 0; j < birthdayList.size(); j++) {
                int birthdayMonth = birthdayList.get(j).getBirthdayMonth();

                // Si même mois alors tu add
                if(birthdayMonth == i){
                    birthdaysInMonth.add(birthdayList.get(j));
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // On trie les anniversaires du mois
                birthdaysInMonth.sort((a, b) -> {
                    return Integer.compare(Integer.parseInt(a.getBirthdayDay()), Integer.parseInt(b.getBirthdayDay()));
                });
            }
            // On ajoute les anniversaires un par à un, à la liste générale
            if (birthdaysInMonth.size() > 0){
                for (Birthday birthday : birthdaysInMonth) {
                    listItem.add(new BirthdayItem(birthday));
                }
            } else {
                Birthday birthday = new Birthday("Pas d'anniversaire");
                listItem.add(new BirthdayItem(birthday));
            }

        }

       return listItem;
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("loginFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogin", false);
        editor.putString("userData", "");
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void addBirthdayDialogBuilder(View view) {
        // Création de la fenêtre modale
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Add new Birthday");
        View viewDialog = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_add_birthday, null);
        final EditText inputFirstname = viewDialog.findViewById(R.id.input_firstname);
        final EditText inputLastname = viewDialog.findViewById(R.id.input_lastname);
        final DatePicker inputDatePicker = viewDialog.findViewById(R.id.input_date);
        builder.setView(viewDialog);

        // Action sur le bouton de soumission
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Nouvel objet JSON qui transitera dans le body de la requête POST
            JSONObject jsonObject = new JSONObject();
            String firstname = inputFirstname.getText().toString();
            String lastname = inputLastname.getText().toString();
            if (Util.checkEmptyString(firstname) && (Util.checkEmptyString(lastname))) {
                // Construction de la date d'anniversaire au bon format
                String month = Util.zeroManager(inputDatePicker.getMonth() + 1);
                String day = Util.zeroManager(inputDatePicker.getDayOfMonth());
                String dateString = inputDatePicker.getYear() + "-" + month + "-" + day;

                try {
                    jsonObject.put("firstname", firstname);
                    jsonObject.put("lastname", lastname);
                    jsonObject.put("date", dateString);
                    // Appel API pour ajouter le nouvel anniversaire
                    callApiToAddBirthday(jsonObject);
                } catch (Exception e ){
                    e.getMessage();
                }
            } else {
                Snackbar.make(binding.getRoot(), "Firstname and lastname must be at least 1 character.", Snackbar.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.create().show();
    }

    private void callApiToAddBirthday(JSONObject jsonObject){
        Long idUser = user.getId();
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), Constants.APPLICATION_JSON);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(Constants.ROOT_API + "/users/" + idUser + "/birthdays").post(requestBody).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("TAG", "Marche pas");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.isSuccessful()){
                    Log.d("TAG", "Bravo");
                    try {
                        Birthday birthday = new Birthday(new JSONObject(response.body().string()));

                        BirthdayItem birthdayItem = new BirthdayItem(birthday);

                        runOnUiThread(() -> {
                            mListItem.add(birthdayItem);
                            mListItem = Util.changeListItem(mListItem);
                            birthdayAdapter.setListItems(mListItem);
                            refreshUserData();
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(binding.getRoot(), response.headers().get("errorMessage"), Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    public void refreshUserData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.ROOT_API + "/users/" + user.getId())
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("TAG", "Marche pas");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        runOnUiThread(() -> {
                            Log.d("TAG", jsonData);
                            SharedPreferences preferences = getSharedPreferences("loginFile", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("userData", jsonData);
                            editor.apply();
                        });
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_button) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}