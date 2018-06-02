package com.example.bgmitkov.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Genres;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

public class Settings extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXTERNAL_STORAGE_GENRE_LOADER_ID = 2;
    private static final String _LOG_TAG = "=-= SettingsActivity";
    public static final String CALLED = "Called";
    public static final String ON_STOP = "onStop()";
    public static final String ON_CREATE_LOADER = "onCreateLoader()";
    public static final String ON_LOAD_FINISHED = "onLoadFinished";
    public static final String ON_LOADER_RESET = "onLoaderReset()";
    static final String[] PROJECTION = new String[]{
            Genres._ID,
            Genres.NAME,
    };
    static final String SELECTION = "(" + Genres._ID + " IN" +
            " (SELECT " + Genres.Members.GENRE_ID + " FROM audio_genres_map ))";


    GenreListAdapter genreListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar mySettingsToolbar = (Toolbar) findViewById(R.id.my_settings_toolbar);
        setSupportActionBar(mySettingsToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        ListView settings = (ListView) findViewById(R.id._genre_list_view);
        genreListAdapter = new GenreListAdapter(this, null);
        settings.setAdapter(genreListAdapter);
        getSupportLoaderManager().initLoader(EXTERNAL_STORAGE_GENRE_LOADER_ID, null, this);
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
    protected void onStop() {
        super.onStop();
        log2me(ON_STOP, CALLED);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        log2me(ON_CREATE_LOADER, "id:" + id);
        return new CursorLoader(this, Genres.getContentUri("external"), PROJECTION, SELECTION, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        log2me(ON_LOAD_FINISHED, "genres loaded");
        genreListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        log2me(ON_LOADER_RESET, "loader with id: " + loader.getId() + " has been reset");
        genreListAdapter.swapCursor(null);
    }
}
