package com.example.bgmitkov.myapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Output;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.zip.Inflater;

import static android.content.ContentValues.TAG;

/**
 * Created by bgmitkov on 25.5.2018 г..
 */

final class AsyncDownloadSongs extends AsyncTask<String, Integer, String> {

    private Context context;
    private ListView listView;
    private ProgressDialog progressDialog;

    AsyncDownloadSongs(Context context, ListView listView) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        this.listView = listView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Downloading...");
        progressDialog.show();
        progressDialog.setCancelable(true);
    }

    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection httpURLConnection = null;
        StringBuilder bs = null;
        long total = 0;
        try {
           /* File file = File.createTempFile("song1", ".mp3");*/
            URL url = new URL(params[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            int respond = httpURLConnection.getResponseCode();
            if (respond != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP respond : " + respond;
            }
            Log.d(TAG, "doInBackground: Server responded with: " + respond);

            int lengthOfFile = httpURLConnection.getContentLength();
            Log.d(TAG, "doInBackground: Length of File" + lengthOfFile);
            try (InputStream is = httpURLConnection.getInputStream();
                 OutputStream out = context.openFileOutput("01-Misunderstood.mp3", Context.MODE_PRIVATE)) {
                byte[] buffer = new byte[8192];
                int len = is.read(buffer);
                while (len != -1) {
                    out.write(buffer, 0, len);
                    total+=len;
                    publishProgress((int)((total*100)/lengthOfFile));
                    len = is.read(buffer);
                }
            }
            Log.d(TAG, "doInBackground: Download finished");
        } catch (MalformedURLException e) {
            Log.d(TAG, "doInBackground: URL is invalid");
        } catch (IOException e) {
            Log.d(TAG, "doInBackground: Failed to open connection to url: " + params[0]);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return "" + total;
    }

    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setMessage("Downloading..." + progress[0] +"%");
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.setMessage("Downloaded " + s + " bytes");
        File file = context.getFileStreamPath("01-Misunderstood.mp3");
        /*Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);*/
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, file.getName());
        contentValues.put(MediaStore.Audio.Media.IS_MUSIC, 1);
        contentValues.put(MediaStore.Audio.Media.DATA, file.getAbsolutePath());
        contentValues.put(MediaStore.MediaColumns.SIZE, Integer.parseInt(s));

        context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
        /*MediaScannerConnection.scanFile(context,new String[]{file.getAbsolutePath()},new String[]{});*/
    }
}
