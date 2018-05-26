package com.example.bgmitkov.myapplication;

import android.content.SharedPreferences;
import android.database.Cursor;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Audio.Genres;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

public class Settings extends AppCompatActivity {

    private static final String TAG = "Settings";
    private ListView settings = null;
    private GenreListAdapter genreListAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> externalStorageGenreLoader = null;

    private static final int EXTERNAL_STORAGE_GENRE_LOADER_ID = 4;
    static final String[] PROJECTION = new String[]{
            Genres._ID,
            Genres.NAME,
            };
    static final String SELECTION = "(" + Genres._ID + " IN" +
            " (SELECT " + Genres.Members.GENRE_ID + " FROM audio_genres_map ))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        genreListAdapter = new GenreListAdapter(this, null);

        externalStorageGenreLoader = isExternalStorageReadable()?new LocalStorageMusicLoader(getApplicationContext(), Genres.getContentUri("external"), PROJECTION,SELECTION, genreListAdapter):null;

        settings = (ListView) findViewById(R.id._genre_list_view);
        settings.setAdapter(genreListAdapter);

        LoaderManager loaderManager = getSupportLoaderManager();
        if(isExternalStorageReadable()) loaderManager.initLoader(EXTERNAL_STORAGE_GENRE_LOADER_ID, null, externalStorageGenreLoader);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void genre_click(View view) {
        CheckBox checkBox = (CheckBox) view;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        String name = checkBox.getText().toString();
        editor.putBoolean(name, checkBox.isChecked());
        Log.d(TAG, "genre_click: genre " + name + " is set to " + checkBox.isChecked());
    }
}
