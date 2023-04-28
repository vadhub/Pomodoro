package com.vad.pomodoro.model;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import com.vad.pomodoro.R;
import com.vad.pomodoro.TikTakListener;

public class TikTakHandle implements TikTakListener {
    private SoundPool soundPool;
    private int soundId;
    private boolean isComplete;
    private final SaveConfiguration configuration;
    private boolean isPlay;

    public TikTakHandle(Context context) {

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build();

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> isComplete = true);

        soundId = soundPool.load(context, R.raw.tk, 1);
        Log.d("##tiktak", "set " + soundId);
        configuration = new SaveConfiguration(context);
        isPlay = configuration.geSoundTikTak();
    }

    public void pause() {
        Log.d("##tiktak", "volumeLow: " + soundId);
        soundPool.setVolume(soundId, 0f, 0f);
    }

    public void play() {
        if (isPlay) {
            if (isComplete) {
                soundId = soundPool.play(soundId, 0.99f, 0.99f, 1, -1, 0.99f);
                Log.d("##tiktak", "set " + soundId);
                isComplete = false;
            }
            soundPool.setVolume(soundId, 0.99f, 0.99f);
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
        configuration.saveSoundTikTak(isOn);
        isPlay = configuration.geSoundTikTak();
        if (!isPlay) {
            pause();
            return;
        }
        if (!isOn) {
            Log.d("##tiktak", "onSwitchif: " + false);
            pause();
        } else {
            play();
        }
    }
}
