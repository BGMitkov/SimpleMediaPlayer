package com.example.bgmitkov.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

    ListView listView = null;
    private static final int INTERNAL_STORAGE_MUSIC_LOADER_ID = 1;
    private static final int EXTERNAL_STORAGE_MUSIC_LOADER_ID = 2;

    static final String[] PROJECTION = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,  MediaStore.Audio.Media.MIME_TYPE};
    static final String SELECTION = "((" + MediaStore.Audio.Media.IS_MUSIC + " != 0) AND" +
            " (" + MediaStore.Audio.Media.DATA + " LIKE \'%.mp3\'))";

    private LoaderManager.LoaderCallbacks<Cursor> externalStorageMusicLoader = null;
    private LoaderManager.LoaderCallbacks<Cursor> internalStorageMusicLoader = null;


    //TextView resultView = null;
    CursorLoader cursorLoader = null;
    CursorAdapter cursorAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id._musicList);
        ContentLoadingProgressBar progressBar = new ContentLoadingProgressBar(this);
        progressBar.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT,
                DrawerLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        listView.setEmptyView(progressBar);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        String[] fromColumns = {MediaStore.Audio.Media.DATA};
        int[] toViews = {android.R.id.text1};

        cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, fromColumns, toViews, 0);
        boolean externalStorage = isExternalStorageReadable();
       // externalStorage = false;
        internalStorageMusicLoader = new LocalStorageMusicLoader(this, MediaStore.Audio.Media.INTERNAL_CONTENT_URI, PROJECTION,SELECTION, cursorAdapter);
        externalStorageMusicLoader = externalStorage?new LocalStorageMusicLoader(this,  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION,SELECTION, cursorAdapter):null;

        listView.setAdapter(cursorAdapter);

        //resultView = (TextView) findViewById(R.id._music);

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(INTERNAL_STORAGE_MUSIC_LOADER_ID, null, internalStorageMusicLoader);
        if(externalStorage) loaderManager.initLoader(EXTERNAL_STORAGE_MUSIC_LOADER_ID, null, externalStorageMusicLoader);
    }

    /*public void _get_music(View view) {
        getSupportLoaderManager().initLoader(1, null, this);
    }*/

    /*@Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        *//*String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";*//*
        *//*String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION
        };*//*
        //return ContentResolverCompat.query(getContentResolver(),MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null,null);
        cursorLoader = new android.content.CursorLoader(this, uri, PROJECTION, SELECTION, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);

        *//*data.moveToFirst();
        StringBuilder res = new StringBuilder();
        while (!data.isAfterLast()) {
            res.append(
                    "\n" +
                            data.getString(data.getColumnIndex("_ID")) +
                            "-" +
                            data.getString(data.getColumnIndex("ALBUM")) +
                            "-" +
                            data.getString(data.getColumnIndex("ARTIST")) +
                            "-" +
                            data.getString(data.getColumnIndex("DURATION"))
            );

            data.moveToNext();
        }
        resultView.setText(res);*//*
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }*/

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
