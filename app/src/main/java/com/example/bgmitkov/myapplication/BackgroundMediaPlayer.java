package com.example.bgmitkov.myapplication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by bgmitkov on 2.6.2018 г..
 */

final class BackgroundMediaPlayer extends MediaPlayer {

    public static final String PLAYLIST = "playlist";
    public static final String PLAYLIST_SIZE = "playlistSize";
    public static final String LAST_SONG = "lastSong";
    public static final String LAST_POSITION = "lastPosition";
    public static final int NOTIFICATION_ID = 0;
    public static final String SAFE_STATE = "safeState()";
    public static final String CALLED = "Called";
    public static final String IS_PAUSED = "isPaused";
    public static final String PLAY_NEXT_SONG = "playNextSong()";
    private static final String _LOG_TAG = "=-= BackgroundMediaPlayer";
    private SharedPreferences prefs;
    private Context context;
    private NotificationCompat.Builder notificationBuilder;

    BackgroundMediaPlayer(Context context) {
        log2me("BackgroundMediaPlayer()", CALLED);
        this.context = context;
        prefs = context.getSharedPreferences(PLAYLIST, Context.MODE_PRIVATE);
        notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.baseline_music_note_white_18)
                .setContentTitle("MyMusicPlayer")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0))
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, null, PendingIntent.getService(context, 0, new Intent(BackgroundMusicService.ACTION_STOP, null, context, BackgroundMusicService.class), 0))
                .setOngoing(true);
    }

    public int getCurrentSongIndex() {
        return prefs.getInt(LAST_SONG, 0);
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(LAST_SONG, currentSongIndex);
        editor.apply();
    }

    public void playNextSong(int nextSong, int from) {
        log2me(PLAY_NEXT_SONG, CALLED);
        int playlistSize = prefs.getInt(PLAYLIST_SIZE, 0);
        log2me(PLAY_NEXT_SONG, "Playlist size = " + playlistSize);
        if (playlistSize == 0) {
            return;
        }

        int songToPlay = nextSong;
        if (nextSong >= playlistSize) {
            songToPlay = 0;
        }

        String songPath = prefs.getString(String.valueOf(songToPlay), null);
        if (songPath == null) {
            return;
        }
        File songFile = new File(songPath);
        if (!songFile.exists()) {
            playNextSong(songToPlay + 1, 0);
            return;
        }

        Uri songUri = Uri.fromFile(songFile);
        log2me(PLAY_NEXT_SONG, "reset()");
        reset();
        try {
            log2me(PLAY_NEXT_SONG, "setDataSource()");
            setDataSource(context, songUri);
        } catch (IOException e) {
            log2me(PLAY_NEXT_SONG, "IOException occurred. setting datasource: " + songUri.toString() + "/n Message: " + e.getMessage());
        } catch (IllegalStateException e) {
            log2me(PLAY_NEXT_SONG, "IllegalStateException occurred. Media Player is wrong state. Message: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log2me(PLAY_NEXT_SONG, "IllegalArgumentException occurred. Setting up the data source. Message: " + e.getMessage());
        } catch (SecurityException e) {
            log2me(PLAY_NEXT_SONG, "SecurityException occurred. Message: " + e.getMessage());
        }

        try {
            log2me(PLAY_NEXT_SONG, "prepare()");
            prepare();
        } catch (IOException e) {
            log2me(PLAY_NEXT_SONG, "IOException occurred. Preparing media player. Message: " + e.getMessage());
        } catch (IllegalStateException e) {
            log2me(PLAY_NEXT_SONG, "IllegalStateException occurred. Preparing media player. Message: " + e.getMessage());
        }

        try {
            log2me(PLAY_NEXT_SONG, "start()");
            start();
        } catch (IllegalStateException e) {
            log2me(PLAY_NEXT_SONG, "IllegalStateException occurred. Starting media player. Message: " + e.getMessage());
        }
        setCurrentSongIndex(songToPlay);
        try {
            log2me(PLAY_NEXT_SONG, "seekTo()");
            seekTo(from);
        } catch (IllegalStateException e) {
            log2me(PLAY_NEXT_SONG, "IllegalStateException occurred. SeekTo media player. The internal player engine has not been initialized. Message: " + e.getMessage());
        }

        log2me(PLAY_NEXT_SONG, "setNotification()");
        setNotification(songFile.getName());
    }

    public void restartIfNotPaused() {
        if (!prefs.getBoolean(IS_PAUSED, false)) {
            int lastSong = prefs.getInt(LAST_SONG, 0);
            int lastPosition = prefs.getInt(LAST_POSITION, 0);
            playNextSong(lastSong, lastPosition);
        }
    }

    public void safeState() {
        log2me(SAFE_STATE, CALLED);
        prefs.edit().putInt(LAST_POSITION, getCurrentPosition()).apply();
    }

    public void setNotification(String currentSong) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationBuilder.setContentText(currentSong);
        notificationManagerCompat.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }

    @Override
    public void pause() throws IllegalStateException {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_PAUSED, true);
        editor.putInt(LAST_POSITION, getCurrentPosition());
        editor.apply();
        super.pause();
    }

    public boolean startIfPaused() {
        if (prefs.getBoolean(IS_PAUSED, false)) {
            int lastSong = prefs.getInt(LAST_SONG, 0);
            int lastPosition = prefs.getInt(LAST_POSITION, 0);
            playNextSong(lastSong, lastPosition);
            prefs.edit().putBoolean(IS_PAUSED, false).apply();
            return true;
        }
        return false;
    }
}
