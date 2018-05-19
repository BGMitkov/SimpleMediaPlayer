package com.example.bgmitkov.myapplication;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * Created by bgmitkov on 19.5.2018 г..
 */

final class OnErrorListener implements MediaPlayer.OnErrorListener {

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("MainActivity", "WHAT: " + what + " EXTRA: " + extra);
        return false;
    }
}
