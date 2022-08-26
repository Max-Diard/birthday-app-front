package com.android.birthday_app.util;

import android.os.Build;

import com.android.birthday_app.adapter.BirthdayItem;
import com.android.birthday_app.adapter.ListItem;
import com.android.birthday_app.adapter.MonthItem;
import com.android.birthday_app.model.Birthday;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {

    public static List<ListItem> changeListItem(List<ListItem> listItem) {
        List<ListItem> principalList = new ArrayList<>();

        List<ListItem> monthList = null;
        List<ListItem> birthdayList = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            monthList = listItem.stream().filter(listItem1 -> {
                if (listItem1.getType() == ListItem.TYPE_MONTH) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

            birthdayList = listItem.stream().filter(listItem1 -> {
                if (listItem1.getType() == ListItem.TYPE_BIRTHDAY) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }

        //On itére sur la liste des mois
        for (int i = 0; i < monthList.size(); i++) {
            List<Birthday> birthdaysInMonth = new ArrayList<>();

            String month = ((MonthItem) monthList.get(i)).month.toUpperCase();
            // On ajoute le mois à la liste générale
            principalList.add(new MonthItem(i + 1, month));

            // Ont itére sur la liste des anniversaires
            for (int j = 0; j < birthdayList.size(); j++) {
                int birthdayMonth = ((BirthdayItem) birthdayList.get(j)).birthday.getBirthdayMonth();

                // Si même mois alors tu add
                if (birthdayMonth == i) {
                    birthdaysInMonth.add(((BirthdayItem) birthdayList.get(j)).birthday);
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
                principalList.add(new BirthdayItem(birthday));
            }
        }
        return principalList;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean checkEmptyString(String stringToCheck) {
        return stringToCheck.length() > 0;
    }

    public static boolean checkPasswordAndConfirmation(String password, String confirmation) {
        return password.length() > 5 && password.equals(confirmation);
    }

    public static String zeroManager(int n) {
        return n < 10 ? "0" + n : n + "";
    }
}
