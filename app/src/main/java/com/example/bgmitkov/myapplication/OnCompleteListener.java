package com.example.bgmitkov.myapplication;

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
    @Override
    public void onCompletion(MediaPlayer mp) {
        MyMediaPlayer mmp = (MyMediaPlayer) mp;
        mmp.reset();
        ListView listView = mmp.getListView();
        int lastPosition = mmp.getLastPosition();
        ListAdapter listAdapter = listView.getAdapter();

        File file;
        View nextView;
        String filePath;
        int nextSongPosition = lastPosition;

        do {
            nextSongPosition += 1;
            if(nextSongPosition >= listAdapter.getCount()) {
                nextSongPosition = 0;
            }
            nextView = listAdapter.getView(nextSongPosition,null,listView);
            TextView path = (TextView) nextView.findViewById(R.id._file_path);
            filePath = path.getText().toString();
            file = new File(filePath);
        } while(!file.exists());

        listView.smoothScrollToPositionFromTop(nextSongPosition, 0);

        TextView name = (TextView) nextView.findViewById(R.id._display_name);
        TextView runningSongHolder = mmp.getTextView();
        Uri myUri = Uri.fromFile(file);
        try{
            mmp.setDataSource(listView.getContext(), myUri);
            mmp.prepare();
            mmp.start();
            runningSongHolder.setText(name.getText().toString());
            mmp.setLastPosition(nextSongPosition);
        } catch (IOException e) {
            System.out.println(filePath);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.out.println("wrong state");
            e.printStackTrace();
        }
    }
}
