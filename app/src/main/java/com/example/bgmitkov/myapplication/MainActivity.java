package com.example.bgmitkov.myapplication;

import android.app.AlertDialog;
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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    static final Uri uri = Uri.parse("content://media/external/audio/genres/all/members");
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
    public static final String DOWNLOAD_SONGS_ACTIVITY = "_download_songs_activity";
    public static final int DOWNLOAD_MUSIC_REQUEST_CODE = 2;
    static MyMediaPlayer mediaPlayer;

    ListView listView;
    MyListAdapter cursorAdapter;
    ListView genreList;
    GenreListAdapter genreListAdapter;
    Button pauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log2me(ON_CREATE, CALLED);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        listView = (ListView) findViewById(R.id._list_view);
        TextView runningSongHolder = (TextView) findViewById(R.id._text_view);
        pauseButton = (Button) findViewById(R.id._pause_button);
        mediaPlayer = new MyMediaPlayer(listView, runningSongHolder);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnErrorListener(new OnErrorListener());
        mediaPlayer.setOnCompletionListener(new OnCompleteListener());

        genreList = new ListView(this);
        genreListAdapter = new GenreListAdapter(this, null);
        genreList.setAdapter(genreListAdapter);

        cursorAdapter = new MyListAdapter(this, null);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new OnItemClickListener(mediaPlayer));
        mediaPlayer.setListView(listView);

        getSupportLoaderManager().initLoader(EXTERNAL_STORAGE_GENRE_LOADER_ID, null, this);

        log2me(ON_CREATE, FINISHED);
    }

    public void _get_songs() {
        log2me(GET_SONGS, CALLED);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("genre_selection", Context.MODE_PRIVATE);
        StringBuilder selection = new StringBuilder();
        selection.append(Members.GENRE_ID).append(" IN (");
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
                   selection.append(id);
                   selection.append(",");
                   hasSelection = true;
               }
            }
            if(hasSelection) {
                selection.deleteCharAt(selection.length() - 1);
            }
        } else {
            return;
        }

        selection.append(") AND ").append(Members.IS_MUSIC).append(" != 0 AND ").append(Members.DATA).append(" LIKE \'%.mp3\'");
        log2me("query created : ", selection.toString());

        LoaderManager.LoaderCallbacks<Cursor> externalStorageMusicLoader = new LocalStorageMusicLoader(this, uri, PROJECTION, selection.toString(), cursorAdapter);
        getSupportLoaderManager().restartLoader(EXTERNAL_STORAGE_MUSIC_LOADER_ID, null, externalStorageMusicLoader);
        log2me(GET_SONGS, FINISHED);
    }

    public void _download_songs_activity() {
        log2me(DOWNLOAD_SONGS_ACTIVITY, CALLED);
        Intent downloadSongsIntent = new Intent(this, DownloadMusicActivity.class);
        startActivityForResult(downloadSongsIntent, DOWNLOAD_MUSIC_REQUEST_CODE);
        log2me(DOWNLOAD_SONGS_ACTIVITY, FINISHED);
    }

    public void _start_settings_activity() {
        log2me(START_SETTINGS_ACTIVITY, CALLED);
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
    protected void onStart() {
        super.onStart();
        log2me("onStart()", CALLED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        log2me("onResume()", CALLED);
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
            case R.id.action_download_songs: _download_songs_activity();
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

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        log2me(ON_ACTIVITY_RESULT, CALLED + " For requestCode: " + requestCode + " and resultCode: " + resultCode);
        if(requestCode == PICK_GENRES_REQUEST || requestCode == DOWNLOAD_MUSIC_REQUEST_CODE) {
            _get_songs();
        }
    }

    public void _pause_player(View view) {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pauseButton.setText(" |> ");
        } else if(!cursorAdapter.isEmpty()){
            mediaPlayer.start();
            pauseButton.setText(" || ");
        } else {
            new AlertDialog.Builder(this).setMessage("No music to play").show();
        }
    }
}
