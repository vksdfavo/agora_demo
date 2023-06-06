package com.example.newdemo.payment_getways;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import com.example.newdemo.databinding.ActivityVideoPlayerBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

public class VideoPlayerActivity extends AppCompatActivity {
    ActivityVideoPlayerBinding binding;
    ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playVideo();

    }

    private void playVideo() {
        String videoPath = "https://firebasestorage.googleapis.com/v0/b/new-sdk-a2133.appspot.com/o/car_racing_motor_race_auto_race_667.mp4?alt=media&token=4a950d38-0d53-4cef-bf9c-50f36bd5f1b8";
        Uri videoUri = Uri.parse(videoPath);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player = new ExoPlayer.Builder(this).build();
        binding.styledPlayerView.setPlayer(player);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.styledPlayerView.setPlayer(null);
        player.release();
    }

}