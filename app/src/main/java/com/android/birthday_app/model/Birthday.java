package com.android.birthday_app.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.temporal.ChronoUnit;
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

    public String getIdentity() {
        return firstname + " " + lastname;
    }

    public String getBirthdayDay() {
        return date.getDate() < 10 ? "0" + date.getDate() : String.valueOf(date.getDate());
    }

    public String getAge() {
        long timeBetween = new Date().getTime() - this.date.getTime();
        double yearsBetween = timeBetween / 3.15576e+10;
        int age = (int) Math.floor(yearsBetween);
        return age + " ans.";
    }

    public int getBirthdayMonth(){
        return this.date.getMonth();
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
