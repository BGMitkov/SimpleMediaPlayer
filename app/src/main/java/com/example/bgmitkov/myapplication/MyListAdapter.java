package com.example.bgmitkov.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by bgmitkov on 22.5.2018 г..
 */

final class MyListAdapter extends CursorAdapter {
    public static final String TAG = "MainActivity";

    public MyListAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id._display_name);
        TextView tvPath = (TextView) view.findViewById(R.id._file_path);

        String name = "ID-" + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.GENRE_ID)) + " / " +
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.DISPLAY_NAME));
        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.DATA));

        tvName.setText(name);
        tvPath.setText(path);
    }
}
