package com.example.bgmitkov.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by bgmitkov on 31.5.2018 г..
 */

final class AsyncDownloadHTML extends AsyncTask<String, Void, List<String>> {
    public static final String ASYNC_DOWNLOAD_HTML = "AsyncDownloadHTML";
    public static final String QUOTATION_MARK = "\"";
    public static final String HREF = "href";
    private static final String _LOG_TAG = "=-= MainActivity";
    ListView listView;
    private Context context;
    private ProgressDialog progressDialog;
    String address;

    AsyncDownloadHTML(Context context, ListView listView) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        this.listView = listView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Getting info...");
        progressDialog.show();
        progressDialog.setCancelable(true);
    }

    @Override
    protected List<String> doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;
        InputStream is = null;
        OutputStream out = null;
        StringBuilder bs = null;
        long total = 0;
        List<String> result = new LinkedList<String>();
        if(!params[0].endsWith("/")) {
            address = params[0] + "/";
        } else {
            address = params[0];
        }

        try {
           /* File file = File.createTempFile("song1", ".mp3");*/
            URL url = new URL(address);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            int respond = httpURLConnection.getResponseCode();
            if (respond != HttpURLConnection.HTTP_OK) {
                log2me(ASYNC_DOWNLOAD_HTML, "Server returned HTTP respond : " + respond);
                return null;
            }
            log2me(ASYNC_DOWNLOAD_HTML, "Server returned HTTP respond : " + respond);

            //File outputDir = context.getCacheDir();
            //File.createTempFile("download_website",".html", outputDir);
            //out = context.openFileOutput("download_website.html", Context.MODE_PRIVATE);
            is = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(is);
            //byte[] buffer = new byte[8192];
            //int len = is.read(buffer);
            while (scanner.hasNext()) {
                //out.write(buffer, 0, len);
                //total+=len;
                //len = is.read(buffer);
                String line = scanner.nextLine();


                if (line.contains(".mp3")) {
                    int lastIndex = 0;
                    int hrefIndex = line.indexOf("href", lastIndex);
                    while (hrefIndex != -1) {
                        int firstQuotation = line.indexOf(QUOTATION_MARK, hrefIndex);
                        int secondQuotation = line.indexOf(QUOTATION_MARK, firstQuotation + 1);
                        String songName = line.substring(firstQuotation + 1, secondQuotation);
                        log2me(ASYNC_DOWNLOAD_HTML, "Found link: " + songName);
                        result.add(address + songName);
                        lastIndex = hrefIndex;
                        hrefIndex = line.indexOf(HREF, lastIndex + HREF.length());
                    }
                }
            }
            //out.flush();
            log2me(ASYNC_DOWNLOAD_HTML, "Website html download finished");
        } catch (MalformedURLException e) {
            log2me(ASYNC_DOWNLOAD_HTML, "URL is invalid");
        } catch (IOException e) {
            log2me(ASYNC_DOWNLOAD_HTML, "Failed to open connection to url: " + params[0]);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log2me(ASYNC_DOWNLOAD_HTML, "doInBackground: Failed to close the inputStream");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log2me(ASYNC_DOWNLOAD_HTML, "doInBackground: Failed to close outputstream: ");
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<String> result) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_item, R.id._display_name, result);
        listView.setAdapter(arrayAdapter);
        progressDialog.setMessage("Found " + result.size() + " songs");
    }

    public void log2me(String where, String what) {
        Log.v(_LOG_TAG + "." + where, what);
    }
}
