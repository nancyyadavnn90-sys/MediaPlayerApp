package com.example.mediaplayerapp;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import android.widget.MediaController;

public class MainActivity extends Activity {

    MediaPlayer mediaPlayer;
    VideoView videoView;
    Uri audioUri;

    static final int PICK_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openFile = findViewById(R.id.btnOpenFile);
        Button play = findViewById(R.id.btnPlay);
        Button pause = findViewById(R.id.btnPause);
        Button stop = findViewById(R.id.btnStop);
        Button restart = findViewById(R.id.btnRestart);
        Button openUrl = findViewById(R.id.btnOpenUrl);

        EditText urlInput = findViewById(R.id.videoUrl);
        videoView = findViewById(R.id.videoView);

        // Open Audio File
        openFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_AUDIO);
        });

        // Play
        play.setOnClickListener(v -> {
            if (audioUri != null) {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(this, audioUri);
                }
                mediaPlayer.start();
            }
        });

        // Pause
        pause.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        // Stop
        stop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });

        // Restart
        restart.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            }
        });

        // Load Video
        openUrl.setOnClickListener(v -> {
            String url = urlInput.getText().toString();

            if (!url.isEmpty()) {
                Uri uri = Uri.parse(url);
                videoView.setVideoURI(uri);

                MediaController controller = new MediaController(this);
                videoView.setMediaController(controller);
                controller.setAnchorView(videoView);

                videoView.start();
            } else {
                Toast.makeText(this, "Enter a valid URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle audio selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_AUDIO && resultCode == RESULT_OK) {
            audioUri = data.getData();
            Toast.makeText(this, "Audio Selected", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}