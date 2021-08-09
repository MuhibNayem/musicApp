package com.example.music.adapters;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.music.R;

import java.util.List;

public class MyAdapter extends ArrayAdapter {

    List<String> listTitle;
    List<String> uri;
    List<String> image;
    Context context;

    public MyAdapter(@NonNull Context context, List<String> title,List<String>uri) {
        super(context, R.layout.list_item,title);
        this.listTitle = title;
        this.context = context;
        this.uri= uri;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        TextView songName = view.findViewById(R.id.txtsongname);
        songName.setText(listTitle.get(position));
        //image.setImageURI(Uri.parse(listTitle.get(position)));

        return view;
    }
}
