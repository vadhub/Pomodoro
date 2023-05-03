package com.vad.pomodoro.model;

import android.content.Context;
import android.media.MediaPlayer;

import com.vad.pomodoro.R;

import java.io.IOException;

public class AlarmHandler {
    private MediaPlayer mediaPlayer;

    public AlarmHandler(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.rington);
    }

    public void playAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancelAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
