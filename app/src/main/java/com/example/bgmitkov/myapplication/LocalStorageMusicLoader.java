package com.example.bgmitkov.myapplication;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

/**
 * Created by bgmitkov on 18.5.2018 г..
 */

final class LocalStorageMusicLoader implements LoaderManager.LoaderCallbacks<Cursor> {

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
        return new CursorLoader(context, uri, projection,selection,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
