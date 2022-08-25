package com.android.birthday_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.birthday_app.R;
import com.android.birthday_app.model.Birthday;

import java.util.List;

public class BirthdayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ListItem> mListItem;

    public BirthdayAdapter(Context context, List<ListItem> listItem) {
        this.mContext = context;
        this.mListItem = listItem;
    }

    @Override
    public int getItemViewType(int position) {
        return mListItem.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ListItem.TYPE_BIRTHDAY:
                View viewBirthday = LayoutInflater.from(mContext).inflate(R.layout.birthday, parent, false);
                return new ViewHolderBirthday(viewBirthday);
            case ListItem.TYPE_MONTH:
                View viewMonth = LayoutInflater.from(mContext).inflate(R.layout.month, parent, false);
                return new ViewHolderMonth(viewMonth);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ListItem.TYPE_BIRTHDAY:
                Birthday birthday = ((BirthdayItem) mListItem.get(position)).birthday;
                ViewHolderBirthday viewHolderBirthday = (ViewHolderBirthday) holder;
                viewHolderBirthday.mTextViewName.setText(birthday.getIdentity());
                viewHolderBirthday.mTextViewDate.setText(birthday.getBirthdayDay());
                viewHolderBirthday.mTextViewAge.setText(birthday.getAge());
                break;
            case ListItem.TYPE_MONTH:
                MonthItem monthItem = (MonthItem) mListItem.get(position);
                ViewHolderMonth viewHolderMonth = (ViewHolderMonth) holder;
                viewHolderMonth.month.setText(monthItem.month);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    public class ViewHolderBirthday extends RecyclerView.ViewHolder {
        private TextView mTextViewDate;
        private TextView mTextViewAge;
        private TextView mTextViewName;

        public ViewHolderBirthday(View itemView) {
            super(itemView);
            this.mTextViewDate = itemView.findViewById(R.id.day);
            this.mTextViewAge = itemView.findViewById(R.id.age);
            this.mTextViewName = itemView.findViewById(R.id.person);
        }
    }

    public class ViewHolderMonth extends RecyclerView.ViewHolder {
        private TextView month;

        public ViewHolderMonth(View itemView) {
            super(itemView);
            this.month = itemView.findViewById(R.id.month);
        }
    }
}
