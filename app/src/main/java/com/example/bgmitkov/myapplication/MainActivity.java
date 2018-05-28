package com.example.bgmitkov.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Audio.Genres;
import android.provider.MediaStore.Audio.Genres.Members;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String _LOG_TAG = "=-= MainActivity";
    static final int PICK_GENRES_REQUEST = 1;

    static final String[] GENRE_PROJECTION = new String[]{
            Genres._ID,
            Genres.NAME};
    static final String GENRE_SELECTION = "(" + Genres._ID + " IN" +
            " (SELECT " + Members.GENRE_ID + " FROM audio_genres_map ))";
    static final String[] PROJECTION = new String[]{
            Members._ID,
            Members.DATA,
            Members.DISPLAY_NAME,
            Members.MIME_TYPE,
            Members.GENRE_ID};
    private static final int EXTERNAL_STORAGE_MUSIC_LOADER_ID = 0;
    private static final int EXTERNAL_STORAGE_GENRE_LOADER_ID = 1;
    public static final String ON_START = "onStart";
    public static final String CALLED = "Called";
    public static final String FINISHED = "Finished";
    public static final String ON_ACTIVITY_RESULT = "onActivityResult";
    public static final String ON_CREATE = "onCreate";
    public static final String START_SETTINGS_ACTIVITY = "_start_settings_activity";
    public static final String ON_STOP = "onStop()";
    public static final String GET_SONGS = "_get_songs";
    public static final String ON_CREATE_LOADER = "onCreateLoader";
    public static final String ON_LOAD_FINISHED = "onLoadFinished";
    public static final String ON_LOADER_RESET = "onLoaderReset()";
    static String SELECTION;
    ListView listView;
    MyMediaPlayer mediaPlayer;
    MyListAdapter cursorAdapter;
    GenreListAdapter genreListAdapter;

    /*static final String[] PROJECTION = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.MIME_TYPE};
    static final String SELECTION = "((" + MediaStore.Audio.Media.IS_MUSIC + " != 0) AND" +
            " (" + MediaStore.Audio.Media.DATA + " LIKE \'%.mp3\'))";*/
    ListView genreList;
    SharedPreferences prefs;
    private LoaderManager.LoaderCallbacks<Cursor> externalStorageMusicLoader;
    private LoaderManager.LoaderCallbacks<Cursor> externalStorageGenreLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.show();
        log2me(ON_CREATE, CALLED);
        listView = (ListView) findViewById(R.id._list_view);
        TextView runningSongHolder = (TextView) findViewById(R.id._text_view);

        mediaPlayer = new MyMediaPlayer(listView, runningSongHolder);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        genreList = new ListView(this);
        genreListAdapter = new GenreListAdapter(this, null);
        genreList.setAdapter(genreListAdapter);
//        externalStorageGenreLoader = isExternalStorageReadable() ? new LocalStorageMusicLoader(getApplicationContext(), Genres.getContentUri("external"), GENRE_PROJECTION, GENRE_SELECTION, genreListAdapter) : null;
        prefs = getApplicationContext().getSharedPreferences("genre_selection", Context.MODE_PRIVATE);

        listView.setOnItemClickListener(new OnItemClickListener(mediaPlayer));
        mediaPlayer.setOnErrorListener(new OnErrorListener());
        mediaPlayer.setOnCompletionListener(new OnCompleteListener());
        cursorAdapter = new MyListAdapter(this, null);
        listView.setAdapter(cursorAdapter);

        getSupportLoaderManager().initLoader(EXTERNAL_STORAGE_GENRE_LOADER_ID, null, this);

        log2me(ON_CREATE, FINISHED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        log2me(ON_START, CALLED);
    }

    public void _get_songs() {
        log2me(GET_SONGS, CALLED);

        SELECTION = Members.GENRE_ID + " IN (";
        StringBuilder sb = new StringBuilder(SELECTION);
        int genresCount = genreListAdapter.getCount();
        log2me(GET_SONGS, "Genres retrieved : " + genresCount);

        if (genresCount > 0) {
            boolean hasSelection = false;
            for (int i = 0; i < genresCount; i++) {
                View genre = genreListAdapter.getView(i, null, genreList);
                CheckBox cbGenre = (CheckBox) genre.findViewById(R.id._genre_name);
                String genreName = cbGenre.getText().toString();
               if(prefs.getBoolean(genreName, true)) {
                   String id = ((TextView) genre.findViewById(R.id._genre_id)).getText().toString();
                   sb.append(id);
                   sb.append(",");
                   hasSelection = true;
               }
            }
            if(hasSelection) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } else {
            return;
        }

        sb.append(") AND ").append(Members.IS_MUSIC).append(" != 0 AND ").append(Members.DATA).append(" LIKE \'%.mp3\'");
        SELECTION = sb.toString();

        log2me("query created : ", SELECTION);
        Uri uri = Uri.parse("content://media/external/audio/genres/all/members");
        externalStorageMusicLoader = isExternalStorageReadable() ? new LocalStorageMusicLoader(this, uri, PROJECTION, SELECTION, cursorAdapter) : null;
        getSupportLoaderManager().initLoader(EXTERNAL_STORAGE_MUSIC_LOADER_ID, null, externalStorageMusicLoader);
        log2me(GET_SONGS, FINISHED);
    }

    public void _download_songs(View view) {
        new AsyncDownloadSongs(this, listView).execute("http://m.yaht.net/repo/muzic/01-Misunderstood.mp3");
    }

    public void _start_settings_activity() {
        log2me(START_SETTINGS_ACTIVITY, CALLED);
        mediaPlayer.reset();
        Intent pickGenresIntent = new Intent(this, Settings.class);
        startActivityForResult(pickGenresIntent, PICK_GENRES_REQUEST);
        log2me(START_SETTINGS_ACTIVITY, FINISHED);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        log2me(ON_ACTIVITY_RESULT, CALLED);
        if(requestCode == PICK_GENRES_REQUEST) {
            if(resultCode == RESULT_OK) {
                super.onActivityResult(requestCode, resultCode, data);
                mediaPlayer.reset();
            }
        }
        log2me(ON_ACTIVITY_RESULT, FINISHED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        log2me("onPause()", CALLED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        log2me(ON_STOP, CALLED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log2me("onDestroy()", CALLED);
    }

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        log2me(ON_CREATE_LOADER, "id:" + id);
        return new CursorLoader(this, Genres.getContentUri("external"), GENRE_PROJECTION, GENRE_SELECTION, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        log2me(ON_LOAD_FINISHED, "genres loaded");
        genreListAdapter.swapCursor(data);
        _get_songs();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        log2me(ON_LOADER_RESET, "loader with id: " + loader.getId() + " has been reset");
        genreListAdapter.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: _start_settings_activity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
