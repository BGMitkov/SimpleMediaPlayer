package com.example.bgmitkov.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * Created by bgmitkov on 30.5.2018 г..
 */

final class DownloadSongOnClickListener implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = parent.getContext();
        if(!Tools.isConnected(context)) {
            new AlertDialog.Builder(context, 0).setMessage("No internet connection!").show();
            return;
        }

        TextView tvName = (TextView) view.findViewById(R.id._display_name);
        String link = tvName.getText().toString();
        new AsyncDownloadSong(parent.getContext()).execute(link);
    }
}
