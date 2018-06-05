package com.example.bgmitkov.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * Created by bgmitkov on 19.5.2018 г..
 */

final class OnItemClickListener implements AdapterView.OnItemClickListener {

    public static final String _LOG_TAG = "=-= OnItemClickListener";
    public static final String ON_ITEM_CLICK = "onItemClick()";
    public static final String CALLED = "Called";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = parent.getContext();
        Intent backgroundMusicService = new Intent(BackgroundMusicService.ACTION_PLAY_SONG, null, context, BackgroundMusicService.class);
        backgroundMusicService.putExtra(BackgroundMusicService.NEXT_SONG, position);
        context.startService(backgroundMusicService);
        log2me(ON_ITEM_CLICK, CALLED);
    }

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }

}
