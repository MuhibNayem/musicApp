package com.example.music.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.music.R;
import com.example.music.adapters.MyAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String currentTitle,currentTitleArt,uri;
    ArrayList<String> arrayList;
    ArrayList<String> arrayListUri;
    ArrayList<String> albumArtUris;
    Uri songsUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewSong);

        runtimePermission();
    }

    public void runtimePermission(){
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        doStuff();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void doStuff() {
        arrayList=new ArrayList<>();
        arrayListUri=new ArrayList<>();
        displaySong();

        MyAdapter adapter = new MyAdapter(this,arrayList,arrayListUri);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = (String)listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                .putExtra("songName",songName)
                .putExtra("songs",arrayList)
                .putExtra("position",position)
                .putExtra("uri",arrayListUri));
            //finish();
            }
        });

    }

    void displaySong(){

        ContentResolver contentResolver = getContentResolver();
        songsUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Cursor songsCursor = contentResolver.query(songsUri,null,null,null,null);

        if (songsCursor != null && songsCursor.moveToFirst()){
            int songTitle = songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            do {
                currentTitle = songsCursor.getString(songTitle);
                uri = songsCursor.getString(songsCursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                arrayList.add(currentTitle);
                arrayListUri.add(uri);

            }while (songsCursor.moveToNext());

        }


    }

}