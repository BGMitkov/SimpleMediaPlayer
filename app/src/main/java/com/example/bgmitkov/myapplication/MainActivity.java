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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
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
    private LoaderManager.LoaderCallbacks<Cursor> externalStorageMusicLoader = null;
    private LoaderManager.LoaderCallbacks<Cursor> externalStorageGenreLoader = null;

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

        genreList = new ListView(this);
        genreListAdapter = new GenreListAdapter(this, null);
        externalStorageGenreLoader = isExternalStorageReadable() ? new LocalStorageMusicLoader(getApplicationContext(), Genres.getContentUri("external"), GENRE_PROJECTION, GENRE_SELECTION, genreListAdapter) : null;
        genreList.setAdapter(genreListAdapter);
        getSupportLoaderManager().initLoader(EXTERNAL_STORAGE_GENRE_LOADER_ID, null, externalStorageGenreLoader);
        prefs = getApplicationContext().getSharedPreferences("genre_selection", Context.MODE_PRIVATE);

        listView.setOnItemClickListener(new OnItemClickListener(mediaPlayer));
        mediaPlayer.setOnErrorListener(new OnErrorListener());
        mediaPlayer.setOnCompletionListener(new OnCompleteListener());
    }

    public void initializeLoader(String genreId, int loaderId) {
        Uri uri = Members.getContentUri("external", Long.parseLong(genreId));
        externalStorageMusicLoader = isExternalStorageReadable() ? new LocalStorageMusicLoader(this, uri, PROJECTION, SELECTION, cursorAdapter) : null;
        getSupportLoaderManager().initLoader(loaderId, null, externalStorageMusicLoader);
    }

    public void _get_songs(View view) {
        mediaPlayer.reset();
        cursorAdapter = new MyListAdapter(this, null);
        listView.setAdapter(cursorAdapter);
        /*SELECTION = " ( " + Members.AUDIO_ID + " IN (SELECT " + Members.AUDIO_ID + " FROM audio_meta LEFT JOIN audio_genres_map as genres ON audio_meta." +
                Media._ID + " == genres.audio_id WHERE genres." + Genres._ID + " IN (";*/
        SELECTION = Members.GENRE_ID + " IN (";
        StringBuilder sb = new StringBuilder(SELECTION);
        int genresCount = genreListAdapter.getCount();
        if (genresCount > 0) {
            View genre = genreListAdapter.getView(0, null, genreList);
            String id = ((TextView) genre.findViewById(R.id._genre_id)).getText().toString();
            sb.append(id);
        } else {
            return;
        }
        if (genresCount > 1) {
            for (int i = 1; i < genresCount; i++) {
                View genre = genreListAdapter.getView(i, null, genreList);
                CheckBox cbGenre = (CheckBox) genre.findViewById(R.id._genre_name);
                String genreName = cbGenre.getText().toString();
               if(prefs.getBoolean(genreName, true)) {
                   String id = ((TextView) genre.findViewById(R.id._genre_id)).getText().toString();
                   sb.append(",");
                   sb.append(id);
               }
            }
        }

        sb.append(") AND ").append(Members.IS_MUSIC).append(" != 0 AND ").append(Members.DATA).append(" LIKE \'%.mp3\'");
        SELECTION = sb.toString();

        Log.d(TAG, "_get_songs: query created : " + SELECTION);
        Uri uri = Uri.parse("content://media/external/audio/genres/all/members");
        externalStorageMusicLoader = isExternalStorageReadable() ? new LocalStorageMusicLoader(this, uri, PROJECTION, SELECTION, cursorAdapter) : null;
        getSupportLoaderManager().initLoader(EXTERNAL_STORAGE_MUSIC_LOADER_ID, null, externalStorageMusicLoader);
    }

    public void _download_songs(View view) {
        new AsyncDownloadSongs(this, listView).execute("http://m.yaht.net/repo/muzic/01-Misunderstood.mp3");
    }

    public void _start_settings_activity(View view) {
        mediaPlayer.reset();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
