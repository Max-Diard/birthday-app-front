package com.android.birthday_app.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppUser {

    private Long id;
    private String username;
    private String email;
    private List<Birthday> birthdays = new ArrayList<>();

    public AppUser(JSONObject jsonData) throws JSONException {
        this.id = jsonData.getLong("id");
        this.username = jsonData.getString("username");
        this.email = jsonData.getString("email");

        JSONArray jsonArray = jsonData.getJSONArray("birthdays");

        for (int i = 0; i < jsonArray.length(); i++){
            Birthday birthday = new Birthday((JSONObject) jsonArray.get(i));
            this.birthdays.add(birthday);
        }
    }

    public List<Birthday> getBirthdays() {
        return birthdays;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", birthdays=" + birthdays.toString() +
                '}';
    }
}
