package com.vad.pomodoro.model;

import android.content.Context;
import android.media.MediaPlayer;

import com.vad.pomodoro.R;
import com.vad.pomodoro.TikTakListener;

public class TikTakHandle implements TikTakListener {
    private MediaPlayer mediaPlayerTikTak;
    private boolean isOnTicTak = true;

    public TikTakHandle(Context context) {
        mediaPlayerTikTak = MediaPlayer.create(context, R.raw.tiktak);
        mediaPlayerTikTak.setLooping(true);
    }

    public void stopTikTak() {
        if (mediaPlayerTikTak != null && mediaPlayerTikTak.isPlaying()) {
            mediaPlayerTikTak.stop();
        }
    }

    public void startTickTak() {
        if (isOnTicTak) {
            mediaPlayerTikTak.start();
        }
    }

    public void cancel() {
        if (mediaPlayerTikTak != null) {
            mediaPlayerTikTak.stop();
            mediaPlayerTikTak = null;
        }
    }

    @Override
    public void onSwitch(boolean isOn) {
        isOnTicTak = isOn;
        if (!isOn) {
            stopTikTak();
        }
    }
}
