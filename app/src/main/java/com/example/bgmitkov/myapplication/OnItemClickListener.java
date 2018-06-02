package com.example.bgmitkov.myapplication;

import android.app.AlertDialog;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Created by bgmitkov on 19.5.2018 г..
 */

final class OnItemClickListener implements AdapterView.OnItemClickListener {

    public static final String TAG = "=-= MainActivity";
    MyMediaPlayer mediaPlayer;

    OnItemClickListener(MyMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mediaPlayer.reset();
        ListView listView = mediaPlayer.getListView();
        TextView path = (TextView) view.findViewById(R.id._file_path);
        String filePath = path.getText().toString();
        File file = new File(filePath);

        if(!file.exists()) {
            new AlertDialog.Builder(listView.getContext()).setMessage("Song file is missing!").show();
            return;
        }

        TextView name = (TextView) view.findViewById(R.id._display_name);
        Uri myUri = Uri.fromFile(file);
        TextView runningSongHolder = mediaPlayer.getNameHolder();
        try{
            mediaPlayer.setDataSource(parent.getContext(), myUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            runningSongHolder.setText(name.getText().toString());
            mediaPlayer.setLastPosition(position);
            mediaPlayer.seekTo(mediaPlayer.getDuration() - 5000);
        } catch (IOException e) {
            Log.d(TAG, "onItemClick(): IOException occured for file: " + filePath);
        } catch (IllegalStateException e) {
            Log.d(TAG, "onItemClick():Media Player is in unexpected state!");
        }
    }
}
