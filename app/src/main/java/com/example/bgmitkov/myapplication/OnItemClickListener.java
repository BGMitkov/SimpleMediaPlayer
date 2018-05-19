package com.example.bgmitkov.myapplication;

import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Created by bgmitkov on 19.5.2018 г..
 */

final class OnItemClickListener implements AdapterView.OnItemClickListener {

    MediaPlayer mediaPlayer;

    OnItemClickListener(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mediaPlayer.reset();

        //Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        TextView textView = (TextView) view;
        String filePath = textView.getText().toString();
        File file = new File(filePath);
        Uri myUri = Uri.fromFile(file);
        try{
            //mediaPlayer = MediaPlayer.create(parent.getContext(),R.raw.song);
            mediaPlayer.setDataSource(parent.getContext(), myUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            System.out.println(filePath);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }
}
