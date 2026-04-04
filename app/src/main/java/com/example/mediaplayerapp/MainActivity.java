package com.example.mediaplayerapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    VideoView videoView;
    EditText etUrl;

    Uri audioUri = null;
    Uri videoUri = null;

    // 🎵 Modern Audio Picker
    ActivityResultLauncher<Intent> audioPicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    audioUri = result.getData().getData();
                    Toast.makeText(this, "Audio Selected", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        etUrl = findViewById(R.id.etUrl);

        Button btnOpenFile = findViewById(R.id.btnOpenFile);
        Button btnOpenURL = findViewById(R.id.btnOpenURL);
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnPause = findViewById(R.id.btnPause);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnRestart = findViewById(R.id.btnRestart);

        mediaPlayer = new MediaPlayer();

        // 🔐 Runtime Permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_AUDIO}, 1);
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        // 🎵 OPEN AUDIO FILE
        btnOpenFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            audioPicker.launch(intent);
        });

        // 🎬 OPEN VIDEO URL (UPDATED)
        btnOpenURL.setOnClickListener(v -> {

            // Stop audio if playing
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            String url = etUrl.getText().toString().trim();

            if (url.isEmpty()) {
                Toast.makeText(this, "Enter URL first", Toast.LENGTH_SHORT).show();
                return;
            }

            videoUri = Uri.parse(url);
            videoView.setVideoURI(videoUri);

            MediaController controller = new MediaController(this);
            videoView.setMediaController(controller);
            controller.setAnchorView(videoView);

            videoView.start();
        });

        // ▶️ PLAY
        btnPlay.setOnClickListener(v -> {
            try {
                if (audioUri != null) {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(this, audioUri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(this, "Playing Audio", Toast.LENGTH_SHORT).show();
                } else if (videoUri != null) {
                    videoView.start();
                } else {
                    Toast.makeText(this, "Select Audio or Video first", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                android.util.Log.e("MediaPlayerError", "Error playing audio", e);
            }
        });

        // ⏸ PAUSE
        btnPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            if (videoView.isPlaying()) {
                videoView.pause();
            }
        });

        // ⏹ STOP (FIXED)
        btnStop.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }

            if (videoView.isPlaying()) {
                videoView.pause();
                videoView.seekTo(0);
            }
        });

        // 🔁 RESTART (FIXED)
        btnRestart.setOnClickListener(v -> {
            if (audioUri != null) {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            }

            if (videoUri != null) {
                videoView.seekTo(0);
                videoView.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}