package com.example.bgmitkov.myapplication;

import android.media.MediaPlayer;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by bgmitkov on 22.5.2018 г..
 */

final class MyMediaPlayer extends MediaPlayer {

    private int lastPosition;
    private ListView listView;
    private TextView nameHolder;

    MyMediaPlayer(ListView listView, TextView nameHolder) {
        super();
        lastPosition = -1;
        this.listView = listView;
        this.nameHolder = nameHolder;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public TextView getNameHolder() {
        return nameHolder;
    }

    public void setNameHolder(TextView nameHolder) {
        this.nameHolder = nameHolder;
    }
}
