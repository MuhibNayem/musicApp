package com.example.music.Activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.music.R;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    AppCompatButton playbtn, pausebtn, nxtbtn, prevbtn, fastfrwrdbtn, rewindbtn;
    TextView txtStrt, txtStop, txtSnName;
    SeekBar musicSeek;
    BarVisualizer barVisualizer;
    String sname;
    public static MediaPlayer mediaPlayer;
    int position;
    ArrayList mySongs, mySongUri;
    ImageView imageView;
    Thread updateSeek;
    String currentTime;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        if (barVisualizer != null) {
            barVisualizer.release();
        }
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        playbtn = findViewById(R.id.playBtn);
        nxtbtn = findViewById(R.id.nxtBtn);
        prevbtn = findViewById(R.id.previousBtn);
        fastfrwrdbtn = findViewById(R.id.forwardBtn);
        rewindbtn = findViewById(R.id.rewaindBtn);


        txtStrt = findViewById(R.id.txtstart);
        txtStop = findViewById(R.id.txtend);
        txtSnName = findViewById(R.id.txtsn);

        musicSeek = findViewById(R.id.seekbar);
        barVisualizer = findViewById(R.id.blast);

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        mySongUri = (ArrayList) bundle.getParcelableArrayList("uri");
        String songName = i.getStringExtra("songName");
        position = bundle.getInt("position", 0);
        txtSnName.setSelected(true);

        Uri uri = Uri.parse(mySongUri.get(position).toString());
        txtSnName.setText(sname);
        sname = mySongs.get(position).toString();

        getSupportActionBar().setTitle(songName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateSeek = new Thread() {

            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        musicSeek.setProgress(currentPosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        updateSeek.start();
        musicSeek.setMax(mediaPlayer.getDuration());
        musicSeek.getProgressDrawable().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
        musicSeek.getThumb().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);

        musicSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    musicSeek.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        String endTime = creatTime(mediaPlayer.getDuration());
        txtStop.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = creatTime(mediaPlayer.getCurrentPosition());
                txtStrt.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        playbtn.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                playbtn.setBackgroundResource(R.drawable.ic_play_button);
                mediaPlayer.pause();
            } else {
                playbtn.setBackgroundResource(R.drawable.ic_pause);
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                nxtbtn.performClick();
            }
        });

        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1) {
            barVisualizer.setAudioSessionId(audioSessionId);
        }

        nxtbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                position = ((position + 1) % mySongs.size());
                Uri u = Uri.parse(mySongUri.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).toString();
                txtSnName.setText(sname);
                String endTime1 = creatTime(mediaPlayer.getDuration());
                txtStop.setText(endTime1);
                playbtn.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId != -1) {
                    barVisualizer.setAudioSessionId(audioSessionId);
                }
                musicSeek.invalidate();
                //musicSeek.setMax(mediaPlayer.getDuration());
                countinue();
                mediaPlayer.start();



            }
        });

        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1) < 0) ? (mySongs.size() - 1) : (position - 1);
                Uri u = Uri.parse(mySongUri.get(position).toString());

                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).toString();
                txtSnName.setText(sname);
                String endTime12 = creatTime(mediaPlayer.getDuration());
                txtStop.setText(endTime12);
                playbtn.setBackgroundResource(R.drawable.ic_pause);
                startAnimationBack(imageView);

                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId != -1) {
                    barVisualizer.setAudioSessionId(audioSessionId);
                }
                countinue();
                //musicSeek.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
            }
        });

        fastfrwrdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });

        rewindbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }
            }
        });

    }

    public void startAnimation(View view) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360);
        animator.setDuration(1000);
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(animator);
        animationSet.start();

    }

    public void startAnimationBack(View view) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 360, 0f);
        animator.setDuration(1000);
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(animator);
        animationSet.start();

    }

    public String creatTime(int duration) {
        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        time += min + ":";
        if (sec < 10) {
            time += "0";
        }
        time += sec;

        return time;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        finish();
    }

    public void countinue() {

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nxtbtn.performClick();
                //musicSeek.setMax(mediaPlayer.getDuration());
            }
        });
    }
    public void checkPlayer(){
        if (mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }


}