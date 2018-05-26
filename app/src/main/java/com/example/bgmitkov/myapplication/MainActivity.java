package com.example.bgmitkov.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    ListView listView = null;
    MyMediaPlayer mediaPlayer = null;
    MyListAdapter cursorAdapter = null;


    private LoaderManager.LoaderCallbacks<Cursor> externalStorageMusicLoader = null;
    private LoaderManager.LoaderCallbacks<Cursor> internalStorageMusicLoader = null;
    private static final int INTERNAL_STORAGE_MUSIC_LOADER_ID = 1;
    private static final int EXTERNAL_STORAGE_MUSIC_LOADER_ID = 2;

    static final String[] PROJECTION = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.MIME_TYPE};
    static final String SELECTION = "((" + MediaStore.Audio.Media.IS_MUSIC + " != 0) AND" +
            " (" + MediaStore.Audio.Media.DATA + " LIKE \'%.mp3\'))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id._list_view);
        TextView runningSongHolder = (TextView) findViewById(R.id._text_view);

        mediaPlayer = new MyMediaPlayer(listView, runningSongHolder);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        /*String[] fromColumns = {MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.DATA};
        int[] toViews = {R.id._display_name,R.id._file_path};*/

        cursorAdapter = new MyListAdapter(this, null);

        internalStorageMusicLoader = new LocalStorageMusicLoader(this, MediaStore.Audio.Media.INTERNAL_CONTENT_URI, PROJECTION,SELECTION, cursorAdapter);
        externalStorageMusicLoader = isExternalStorageReadable()?new LocalStorageMusicLoader(this,  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION,SELECTION, cursorAdapter):null;

        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new OnItemClickListener(mediaPlayer));
        mediaPlayer.setOnErrorListener(new OnErrorListener());
        mediaPlayer.setOnCompletionListener(new OnCompleteListener());

    }

    public void _get_songs(View view) {
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(INTERNAL_STORAGE_MUSIC_LOADER_ID, null, internalStorageMusicLoader);
        if(isExternalStorageReadable()) loaderManager.initLoader(EXTERNAL_STORAGE_MUSIC_LOADER_ID, null, externalStorageMusicLoader);

    }

    public void _download_songs(View view) {
        new AsyncDownloadSongs(this, listView).execute("http://m.yaht.net/repo/muzic/01-Misunderstood.mp3");
    }

    public void _start_settings_activity(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
