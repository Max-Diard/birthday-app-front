package com.android.birthday_app.adapter;

public class MonthItem extends ListItem {

    public int monthNumber;
    public String month;

    public MonthItem(int monthNumber, String month) {
        this.monthNumber = monthNumber;
        this.month = month;
    }

    @Override
    public int getType() {
        return TYPE_MONTH;
    }
}
