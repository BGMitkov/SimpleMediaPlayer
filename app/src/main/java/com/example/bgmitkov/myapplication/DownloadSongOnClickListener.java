package com.example.bgmitkov.myapplication;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by bgmitkov on 30.5.2018 г..
 */

final class DownloadSongOnClickListener implements AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tvName = (TextView) view.findViewById(R.id._display_name);
        TextView tvLink = (TextView) view.findViewById(R.id._file_path);
        String link = tvName.getText().toString();
        new AsyncDownloadSong(parent.getContext()).execute(link);
    }
}
