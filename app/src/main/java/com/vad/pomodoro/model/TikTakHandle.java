package com.vad.pomodoro.model;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.vad.pomodoro.R;
import com.vad.pomodoro.TikTakListener;

import java.io.IOException;

public class TikTakHandle implements TikTakListener {
    private SoundPool soundPool;
    private boolean isOnTicTak = true;
    private int soundId;

    public TikTakHandle(Context context) {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        soundId = soundPool.load(context, R.raw.tk, 1);
    }

    public void stopTikTak() {
        if (soundPool != null) {
            soundPool.stop(soundId);
        }
    }

    public void startTickTak() {
        if (isOnTicTak) {
            soundPool.play(soundId, 1, 1, 0, -1, 1);
        }
    }

    public void cancel() {
        if (soundPool != null) {
            soundPool.stop(soundId);
            soundPool.release();
            soundPool = null;
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
