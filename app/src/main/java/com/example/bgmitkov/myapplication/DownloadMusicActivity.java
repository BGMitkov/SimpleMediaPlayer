package com.example.bgmitkov.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class DownloadMusicActivity extends AppCompatActivity {
    private static final String _LOG_TAG = "=-= MainActivity";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_music);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.download_songs_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if(ab!=null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        listView = (ListView) findViewById(R.id._browsing_music_list);
        listView.setOnItemClickListener(new DownloadSongOnClickListener());
    }

    public void _display_songs(View view) {
        EditText etLink = (EditText) findViewById(R.id._music_link);
        String url = etLink.getText().toString();
        new AsyncDownloadHTML(this, listView).execute(url);
    }

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }
}
