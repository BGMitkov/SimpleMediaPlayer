package com.example.bgmitkov.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by bgmitkov on 26.5.2018 г..
 */

public class GenreListAdapter extends CursorAdapter {
    public GenreListAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.genre_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id._genre_name);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        checkBox.setText(name);
        checkBox.setChecked(prefs.getBoolean(name, true));
    }
}
