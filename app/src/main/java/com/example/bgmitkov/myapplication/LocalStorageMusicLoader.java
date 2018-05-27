package com.example.bgmitkov.myapplication;


import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;

/**
 * Created by bgmitkov on 18.5.2018 г..
 */

final class LocalStorageMusicLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";

    private Context context;
    private String[] projection;
    private String selection;
    private Uri uri;
    private CursorAdapter cursorAdapter;
    private MergeCursor mergeCursor;

    LocalStorageMusicLoader(Context context, Uri uri, String[] projection, String selection, CursorAdapter cursorAdapter) {
        this.context = context;
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.cursorAdapter = cursorAdapter;
        Log.d(TAG, "LocalStorageMusicLoader: created");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: Loader created id-" + id);
        return new CursorLoader(context, uri, projection,selection,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
        Log.d(TAG, "onLoadFinished: load finished with Cursor data loaded : " + data.toString());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: loader " + loader.getId() + " was reset");
        cursorAdapter.swapCursor(null);
    }
}
