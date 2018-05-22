package com.example.bgmitkov.myapplication;

import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;


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

        mediaPlayer = new MyMediaPlayer(listView);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        String[] fromColumns = {MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.DATA};
        int[] toViews = {R.id._display_name,R.id._file_path};

        cursorAdapter = new MyListAdapter(this, null);
        boolean externalStorage = isExternalStorageReadable();
        internalStorageMusicLoader = new LocalStorageMusicLoader(this, MediaStore.Audio.Media.INTERNAL_CONTENT_URI, PROJECTION,SELECTION, cursorAdapter);
        externalStorageMusicLoader = externalStorage?new LocalStorageMusicLoader(this,  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION,SELECTION, cursorAdapter):null;

        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new OnItemClickListener(mediaPlayer));

        mediaPlayer.setOnErrorListener(new OnErrorListener());

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(INTERNAL_STORAGE_MUSIC_LOADER_ID, null, internalStorageMusicLoader);
        if(externalStorage) loaderManager.initLoader(EXTERNAL_STORAGE_MUSIC_LOADER_ID, null, externalStorageMusicLoader);
    }

    public void _play_music(ListView view) {

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
