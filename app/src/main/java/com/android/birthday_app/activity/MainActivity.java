package com.android.birthday_app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
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

        String userString = getIntent().getStringExtra("user");
        if(userString == null){
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

        Button logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(click -> logout());

        FloatingActionButton addBirthday = binding.addBirthday;
        addBirthday.setOnClickListener(this::addBirthday);
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
            for (Birthday birthday : birthdaysInMonth) {
                listItem.add(new BirthdayItem(birthday));
            }
        }

       return listItem;
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("Islogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("Islogin", false).apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void addBirthday(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Add Birthday");

        View viewDialog = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_add_birthday, null);
        final EditText inputFirstname = viewDialog.findViewById(R.id.input_firstname);
        final EditText inputLastname = viewDialog.findViewById(R.id.input_lastname);
        final DatePicker inputDatePicker = viewDialog.findViewById(R.id.input_date);
        builder.setView(viewDialog);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Date dateBirthday = null;
            JSONObject jsonObject = new JSONObject();
            String firstname = inputFirstname.getText().toString();
            String lastname = inputLastname.getText().toString();

            try{
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String month = (inputDatePicker.getMonth() + 1) < 10 ? "0" + (inputDatePicker.getMonth() + 1) : ""+(inputDatePicker.getMonth() + 1);
                String day = inputDatePicker.getDayOfMonth() < 10 ? "0" + inputDatePicker.getDayOfMonth() : ""+inputDatePicker.getDayOfMonth();
                String dateString = inputDatePicker.getYear() + "-" + month + "-" + day;
                dateBirthday = format.parse(dateString);

//                jsonObject.put("id", null);
                jsonObject.put("firstname", firstname);
                jsonObject.put("lastname", lastname);
                jsonObject.put("date", dateString);
//                Birthday birthday = new Birthday(jsonObject);
                callApiToAddBirthday(jsonObject);

            } catch (Exception e ){
                e.getMessage();
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
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    Snackbar.make(binding.getRoot(), response.headers().get("errorMessage"), Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }
}