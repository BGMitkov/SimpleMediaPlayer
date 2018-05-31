package com.example.bgmitkov.myapplication;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by bgmitkov on 31.5.2018 г..
 */

final class MyArrayAdapter extends ArrayAdapter<String> {
    MyArrayAdapter(@NonNull Context context, @LayoutRes int resource,
                   @NonNull List<String> objects) {
        super(context,resource,objects);
    }

    @Nullable
    @Override
    public String getItem(int position) {
        String item = (String) super.getItem(position);
        return item.substring(item.lastIndexOf("/") + 1);
    }
}
