package com.example.bgmitkov.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Audio.Genres;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

public class Settings extends AppCompatActivity {

    private static final String _LOG_TAG = "=-= SettingsActivity";
    static final String[] PROJECTION = new String[]{
            Genres._ID,
            Genres.NAME,
    };
    static final String SELECTION = "(" + Genres._ID + " IN" +
            " (SELECT " + Genres.Members.GENRE_ID + " FROM audio_genres_map ))";
    private static final String TAG = "Settings";
    private static final int EXTERNAL_STORAGE_GENRE_LOADER_ID = 2;
    public static final String ON_PAUSE = "onPause()";
    public static final String CALLED = "Called";
    public static final String FINISHED = "Finished";
    public static final String ON_STOP = "onStop()";
    private ListView settings = null;
    private GenreListAdapter genreListAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> externalStorageGenreLoader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar mySettingsToolbar = (Toolbar) findViewById(R.id.my_settings_toolbar);
        setSupportActionBar(mySettingsToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        settings = (ListView) findViewById(R.id._genre_list_view);

        genreListAdapter = new GenreListAdapter(this, null);
        externalStorageGenreLoader = isExternalStorageReadable() ? new LocalStorageMusicLoader(getApplicationContext(), Genres.getContentUri("external"), PROJECTION, SELECTION, genreListAdapter) : null;
        settings.setAdapter(genreListAdapter);
        LoaderManager loaderManager = getSupportLoaderManager();

        if (isExternalStorageReadable())
            loaderManager.initLoader(EXTERNAL_STORAGE_GENRE_LOADER_ID, null, externalStorageGenreLoader);
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
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("genre_selection", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String name = checkBox.getText().toString();
        editor.putBoolean(name, checkBox.isChecked());
        editor.apply();
    }

    @Override
    protected void onPause() {
        log2me(ON_PAUSE, CALLED);
        super.onPause();
        Intent result = new Intent();
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        log2me(ON_STOP, CALLED);
    }

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }
}
