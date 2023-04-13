package com.vad.pomodoro.model;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.vad.pomodoro.R;
import com.vad.pomodoro.TikTakListener;

public class TikTakHandle implements TikTakListener {
    private SoundPool soundPool;
    private boolean isOnTicTak;
    private int soundId = 1;
    private final Context context;

    public TikTakHandle(Context context) {
        this.context = context;
        SaveConfiguration configuration = new SaveConfiguration(context);
        isOnTicTak = configuration.geSoundTikTak();

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(1)
                .build();
        soundPool.load(context, R.raw.tk, 1);
    }

    public void stop() {
        if (soundPool != null) {
            soundPool.stop(soundId);
            soundPool.release();
        }
    }

    public void pause() {
        if (soundPool != null) {
            soundPool.pause(soundId);
            Log.d("##tiktak", "pauseTickTak: " + soundId);
        }
    }

    public void resume() {
        if (isOnTicTak) {
            soundPool.resume(soundId);
            Log.d("##tiktak", "resumeTickTak: " + soundId);
        }
    }

    public void play() {
        if (isOnTicTak) {
            soundPool.play(soundId, 1, 1, 1, -1, 1);
            Log.d("##tiktak", "startTickTak: " + soundId);
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
        Log.d("##tiktak", "onSwitch: " + isOn);
        isOnTicTak = isOn;
        if (!isOn) {
            Log.d("##tiktak", "onSwitchif: " + isOn);
            stop();
        } else {
            play();
        }
    }
}
