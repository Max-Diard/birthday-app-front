package com.android.birthday_app.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Birthday {
    private Long id;
    //Todo: Gérer Date
    private Date date;
    private String firstname;
    private String lastname;

    public Birthday(JSONObject jsonData) throws JSONException {
        this.id = jsonData.getLong("id");
        //Todo: Gérer Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.date = format.parse(jsonData.getString("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.firstname = jsonData.getString("firstname");
        this.lastname = jsonData.getString("lastname");
    }

    public String getIdenty() {
        return firstname + " " + lastname;
    }

    public int getBirthdayDay() {
        return date.getDay();
    }

    public Long getAge() {
        return new Date().getTime() - date.getTime();
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
