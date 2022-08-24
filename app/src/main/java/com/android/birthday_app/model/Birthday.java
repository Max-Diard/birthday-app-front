package com.android.birthday_app.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Birthday {
    private Long id;
    //Todo: Gérer Date
    private String date;

    private String firstname;
    private String lastname;

    public Birthday(JSONObject jsonData) throws JSONException {
        this.id = jsonData.getLong("id");
        //Todo: Gérer Date
        this.date = jsonData.getString("date");
        this.firstname = jsonData.getString("firstname");
        this.lastname = jsonData.getString("lastname");
    }

    @Override
    public String toString() {
        return "Birthday{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}
