package com.example.bgmitkov.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Created by bgmitkov on 23.5.2018 г..
 */

public class OnCompleteListener implements MediaPlayer.OnCompletionListener {
    private Context context;

    OnCompleteListener(Context context) {
        this.context = context;
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        BackgroundMediaPlayer player = (BackgroundMediaPlayer) mp;
        int nextSong = player.getCurrentSongIndex() + 1;
        player.playNextSong(nextSong, 0);
        /*boolean exists;
        do {
            nextSong += 1;
            if(nextSong >= listAdapter.getCount()) {
                nextSong = 0;
            }
            nextView = listAdapter.getView(nextSong,null,listView);
            TextView path = (TextView) nextView.findViewById(R.id._file_path);
            filePath = path.getText().toString();
            file = new File(filePath);
            exists = file.exists();
            if(!exists) {
                new AlertDialog.Builder(listView.getContext()).setMessage("Song file is missing: " + filePath).show();
            }
        } while(!exists);*/

        /*listView.smoothScrollToPositionFromTop(nextSong, 0);*/

        /*TextView name = (TextView) nextView.findViewById(R.id._display_name);
        TextView runningSongHolder = player.getNameHolder();
        Uri myUri = Uri.fromFile(file);
        try{
            player.setDataSource(listView.getContext(), myUri);
            player.prepare();
            player.start();
            runningSongHolder.setText(name.getText().toString());
            player.setLastPosition(nextSong);
        } catch (IOException e) {
            System.out.println(filePath);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.out.println("wrong state");
            e.printStackTrace();
        }*/

    }
}
