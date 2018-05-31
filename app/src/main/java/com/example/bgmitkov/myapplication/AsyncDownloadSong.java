package com.example.bgmitkov.myapplication;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaDescription;
import android.media.MediaExtractor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.CREATOR;
import static android.content.ContentValues.TAG;

/**
 * Created by bgmitkov on 25.5.2018 г..
 */

final class AsyncDownloadSong extends AsyncTask<String, Integer, String> {
    private static final String _LOG_TAG = "=-= MainActivity";
    public static final String ASYNC_DOWNLOAD_SONG = "AsyncDownloadSong";

    private Context context;
    private ProgressDialog progressDialog;
    File file;

    AsyncDownloadSong(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
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
        InputStream is = null;
        OutputStream out = null;
        StringBuilder bs = null;
        long total = 0;
        String fileName = params[0].substring(params[0].lastIndexOf("/") + 1);
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
            log2me(ASYNC_DOWNLOAD_SONG, "doInBackground: Server responded with: " + respond);
            //Log.d(TAG, "doInBackground: Server responded with: " + respond);
            int lengthOfFile = httpURLConnection.getContentLength();
            //Log.d(TAG, "doInBackground: Length of File" + lengthOfFile);
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            file = new File(root, fileName);
            out = new FileOutputStream(file);
           // out = context.openFileOutput("01-Misunderstood.mp3", Context.MODE_PRIVATE);
            is = httpURLConnection.getInputStream();
            byte[] buffer = new byte[8192];
            int len = is.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                total+=len;
                publishProgress((int)((total*100)/lengthOfFile));
                len = is.read(buffer);
            }
            out.flush();
            Log.d(TAG, "doInBackground: Download finished");
        } catch (MalformedURLException e) {
            Log.d(TAG, "doInBackground: URL is invalid");
        } catch (IOException e) {
            Log.d(TAG, "doInBackground: Failed to open connection to url: " + params[0]);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(TAG, "doInBackground: Failed to close the inputStream");
                }
            }
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.d(TAG, "doInBackground: Failed to close outputstream: ");
                }
            }
        }
        return "" + total;
    }

    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setMessage("Downloading..." + progress[0] +"%...Please wait!");
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.setMessage("Downloaded " + s + " bytes");
        //File file = context.getFileStreamPath("01-Misunderstood.mp3");
        String[] paths = new String[] {file.getAbsolutePath()};
        String[] mimeTypes = new String[] {"audio/mpeg"};
        MediaScannerConnection.scanFile(context, paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                log2me(ASYNC_DOWNLOAD_SONG,"onScanCompleted: scanned file on path : " + path);
                //Log.d(TAG, "onScanCompleted: scanned file on path : " + path);
                log2me(ASYNC_DOWNLOAD_SONG, "onScanCompleted: scanned file's uri : " + uri);
                //Log.d(TAG, "onScanCompleted: scanned file's uri : " + uri);
            }
        });
    }

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }
}
