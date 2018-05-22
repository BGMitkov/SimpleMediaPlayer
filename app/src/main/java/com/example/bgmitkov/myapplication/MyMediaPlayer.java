package com.example.bgmitkov.myapplication;

import android.media.MediaPlayer;
import android.widget.ListView;

/**
 * Created by bgmitkov on 22.5.2018 г..
 */

final class MyMediaPlayer extends MediaPlayer {

    private int lastPosition;
    private ListView listView;

    MyMediaPlayer(ListView listView) {
        super();
        lastPosition = -1;
        this.listView = listView;
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
}
