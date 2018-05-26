package com.example.bgmitkov.myapplication;

import android.media.MediaPlayer;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by bgmitkov on 22.5.2018 г..
 */

final class MyMediaPlayer extends MediaPlayer {

    private int lastPosition;
    private ListView listView;
    private TextView textView;

    MyMediaPlayer(ListView listView, TextView textView) {
        super();
        lastPosition = -1;
        this.listView = listView;
        this.textView = textView;
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

    public TextView getTextView() {
        return textView;
    }
}
