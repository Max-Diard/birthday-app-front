package com.android.birthday_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.birthday_app.adapter.BirthdayAdapter;
import com.android.birthday_app.adapter.BirthdayItem;
import com.android.birthday_app.adapter.ListItem;
import com.android.birthday_app.adapter.MonthItem;
import com.android.birthday_app.databinding.ActivityMainBinding;
import com.android.birthday_app.model.AppUser;
import com.android.birthday_app.model.Birthday;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private List<ListItem> mListItem;

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
            mListItem = getListItem(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        BirthdayAdapter birthdayAdapter = new BirthdayAdapter(this, mListItem);
        recyclerView.setAdapter(birthdayAdapter);

        Button logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(click -> logout());
    }

    private List<ListItem> getListItem(AppUser user) {
        List<ListItem> listItem = new ArrayList<>();
        List<Birthday> birthdayList = user.getBirthdays();
        List<String> monthList = Arrays.asList(new DateFormatSymbols(Locale.FRENCH).getMonths());

        for (int i = 0; i < birthdayList.size(); i++) {
            listItem.add(new BirthdayItem(birthdayList.get(i)));
        }

        for (int i = 0; i < monthList.size(); i++) {
            listItem.add(new MonthItem(i + 1, monthList.get(i)));
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
}