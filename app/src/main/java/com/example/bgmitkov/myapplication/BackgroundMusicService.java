package com.example.bgmitkov.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by bgmitkov on 2.6.2018 г..
 */

final public class BackgroundMusicService extends Service {
    public static final String ACTION_PLAY_SONG = "com.example.bgmitkov.myapplication.action.PLAY_SONG";
    public static final String ACTION_PAUSE_OR_RESUME = "com.example.bgmitkov.myapplication.action.PAUSE_OR_RESUME";
    public static final String ACTION_STOP = "com.example.bgmitkov.myapplication.action.STOP";
    public static final String ON_START_COMMAND = "onStartCommand";
    public static final String CALLED = "Called";
    public static final String NEXT_SONG = "NextSong";
    public static final String PLAYLIST = "playlist";
    public static final int NOTIFICATION_ID = 0;
    public static final String ON_DESTROY = "onDestroy()";
    private static final String _LOG_TAG = "=-= BackgroundMusicService";
    private BackgroundMediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new BackgroundMediaPlayer(this);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnErrorListener(new OnErrorListener());
        mediaPlayer.setOnCompletionListener(new OnCompleteListener(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = null;

        if (intent != null) {
            action = intent.getAction();
            switch (action) {
                case ACTION_PLAY_SONG:
                    int nextSong = intent.getIntExtra(NEXT_SONG, -1);
                    if (nextSong != -1) {
                        mediaPlayer.playNextSong(nextSong, 0);
                    }
                    break;
                case ACTION_PAUSE_OR_RESUME:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    } else if(!mediaPlayer.startIfPaused()) {
                         stopSelf();
                    }
                    break;
                case ACTION_STOP:
                    NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
                    stopSelf();
            }
        } else {
            mediaPlayer.restartIfNotPaused();
        }

        log2me(ON_START_COMMAND, CALLED + " with action: " + action);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        log2me(ON_DESTROY, CALLED);
        mediaPlayer.safeState();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }
}
