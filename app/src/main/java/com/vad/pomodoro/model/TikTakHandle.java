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
    private int soundId = 1;
    boolean isPlay;

    public TikTakHandle(Context context) {

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build();

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> isPlay = true);

        soundId = soundPool.load(context, R.raw.tk, 1);
        Log.d("##tiktak", "set " + soundId);
        SaveConfiguration configuration = new SaveConfiguration(context);
        configuration.geSoundTikTak();
    }

    public void pause() {
        Log.d("##tiktak", "volumeLow: " + soundId);
        soundPool.setVolume(soundId, 0f, 0f);
    }

    public void play() {
        if (isPlay) {
            soundId = soundPool.play(soundId, 0.99f, 0.99f, 1, -1, 0.99f);
            Log.d("##tiktak", "set " + soundId);
            isPlay = false;
        }
        soundPool.setVolume(soundId, 0.99f, 0.99f);
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
        if (!isOn) {
            Log.d("##tiktak", "onSwitchif: " + false);
            pause();
        } else {
            play();
        }
    }
}
