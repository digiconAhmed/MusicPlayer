package com.malaika.musicplayer;

//import android.icu.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private ImageView artistImage;
    private TextView leftTime;
    private TextView rightTime;
    private SeekBar seekBar;
    private Button playButton,prevButton,nextButton;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUI();
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                leftTime.setText(dateFormat.format(new Date(currentPos)));
                rightTime.setText(dateFormat.format(new Date(duration-currentPos)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setUpUI(){
        artistImage = (ImageView)findViewById(R.id.imageView);
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.uddi_jamp3);
        leftTime = (TextView) findViewById(R.id.leftTime);
        rightTime = (TextView) findViewById(R.id.rightTime);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        playButton = (Button)findViewById(R.id.play_button);
        prevButton = (Button)findViewById(R.id.previous_button);
        nextButton = (Button)findViewById(R.id.next_button);

        playButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.previous_button:
                    backMusic();
                break;
            case R.id.play_button:
                if (mediaPlayer.isPlaying()){
                    pauseMusic();
                }else {
                    startMusic();
                }
                break;
            case R.id.next_button:
                nextMusic();
                break;
        }
    }
    public void pauseMusic(){
        if (mediaPlayer != null){
            mediaPlayer.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }
    public void startMusic(){
        if (mediaPlayer!=null){
            mediaPlayer.start();
            updateThread();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }
    public void backMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
        }
    }
    public void nextMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getDuration()-100);
        }
    }
    public void updateThread(){
        thread = new Thread(){
            @Override
            public void run() {
                try{
                    while (mediaPlayer!=null && mediaPlayer.isPlaying()) {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);
                                //update time
                                leftTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss")
                                        .format(new Date(mediaPlayer.getCurrentPosition()))));
                                rightTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss")
                                .format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()))));
                            }
                        });
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}


