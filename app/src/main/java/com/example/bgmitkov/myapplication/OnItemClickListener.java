package com.example.bgmitkov.myapplication;

import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Created by bgmitkov on 19.5.2018 г..
 */

final class OnItemClickListener implements AdapterView.OnItemClickListener {

    MyMediaPlayer mediaPlayer;

    OnItemClickListener(MyMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mediaPlayer.reset();
        ListView listView = mediaPlayer.getListView();
        int p = mediaPlayer.getLastPosition();

        View lastItem = (View) listView.getChildAt(p);
        if(lastItem != null) {
            TextView text = (TextView) lastItem.findViewById(R.id._display_name);
            text.setTextColor(Color.BLACK);

            System.out.println("Previous song at " + p + " was : " + text.getText().toString());
        }

        System.out.println("Position of playing song : " + position);


        TextView path = (TextView) view.findViewById(R.id._file_path);
        String filePath = path.getText().toString();
        File file = new File(filePath);
        System.out.println(filePath);

        TextView name = (TextView) view.findViewById(R.id._display_name);

        /*ImageView image = item.getImage();
        image.setVisibility(View.VISIBLE);
        image.setImageResource(R.drawable.volume_up);
        image.setBackgroundResource(R.drawable.volume_up);*/
        Uri myUri = Uri.fromFile(file);
        try{
            mediaPlayer.setDataSource(parent.getContext(), myUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            name.setTextColor(Color.GREEN);
            mediaPlayer.setLastPosition(position);
        } catch (IOException e) {
            System.out.println(filePath);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.out.println("wrong state");
            e.printStackTrace();
        }

    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
