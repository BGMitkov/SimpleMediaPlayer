package com.example.bgmitkov.myapplication;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;

/**
 * Created by bgmitkov on 18.5.2018 г..
 */

final class LocalStorageMusicLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String _LOG_TAG = "=-= MainActivity";

    private Context context;
    private String[] projection;
    private String selection;
    private Uri uri;
    private CursorAdapter cursorAdapter;

    LocalStorageMusicLoader(Context context, Uri uri, String[] projection, String selection, CursorAdapter cursorAdapter) {
        this.context = context;
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.cursorAdapter = cursorAdapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        log2me("onCreateLoader()", "Loader created id-" + id);
        return new CursorLoader(context, uri, projection,selection,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
        log2me("onLoadFinished()", "load finished with Cursor data loaded : " + data.toString());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        log2me("onLoaderReset()", "loader " + loader.getId() + " was reset");
        cursorAdapter.swapCursor(null);
    }

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }
}
