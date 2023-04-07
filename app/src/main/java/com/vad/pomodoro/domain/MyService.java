package com.vad.pomodoro.domain;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.vad.pomodoro.R;
import com.vad.pomodoro.RoundListener;
import com.vad.pomodoro.TikTakListener;
import com.vad.pomodoro.TimeListener;
import com.vad.pomodoro.TimerHandle;
import com.vad.pomodoro.model.ChunkTimer;
import com.vad.pomodoro.model.Pomodoro;
import com.vad.pomodoro.ui.IndicatorRound;
import com.vad.pomodoro.ui.TomatoNotificationService;

import java.util.concurrent.TimeUnit;

public class MyService extends Service implements TimerHandle, RoundListener, TimeListener {

    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerTikTak;
    private AudioManager manager;
    private final int idNotification = 0x11234c;
    private TomatoNotificationService notificationService;
    private NotificationCompat.Builder nb;
    private boolean isStart = false;
    private boolean isCanceled = false;
    private boolean isOnTicTak = true;
    private boolean isShowNotification;
    private ChunkTimer chunkTimer;
    private int minutesInit;
    private long millisLeft;
    private final Pomodoro pomodoro = new Pomodoro(this, this);
    private IndicatorRound indicatorRound;
    private final IBinder binder = new BinderTimer();

    @Override
    public void change(int round) {
        indicatorRound.changeRound(round);
    }

    @Override
    public void changeTime(int time) {
        minutesInit = time;
    }

    public void onSwitch(boolean isOn) {
        isOnTicTak = isOn;

        if (!isOn) {
            stopTikTak();
        }
    }

    public class BinderTimer extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.gong);
        mediaPlayerTikTak = MediaPlayer.create(this, R.raw.tiktak);
        mediaPlayerTikTak.setLooping(true);

        notificationService = new TomatoNotificationService(this);
        nb = notificationService.showNotification();
        minutesInit = pomodoro.getMinutes();
        chunkTimer = new ChunkTimer(TimeUnit.MILLISECONDS.convert(minutesInit, TimeUnit.MINUTES), 1000);
        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        showNotification();
    }

    @Override
    public void showTime(long timeUntilFinished) {
        millisLeft = timeUntilFinished;
        if (isShowNotification) {
            showNotification(DateUtils.formatElapsedTime((int) TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished)));
        }
    }

    public void setTimer(Button buttonStart, TimerHandle handle) {
        if (isStart && !isCanceled) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            stopTikTak();

            buttonStart.setText(getResources().getString(R.string.start_text));
            buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24), null, null, null);
            chunkTimer.cancel();
            isCanceled = true;
        } else if (!isStart && isCanceled) {
            startTickTak();
            checkAudioValue();
            buttonStart.setText(getResources().getString(R.string.pause_text));
            buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_pause_24), null, null, null);
            chunkTimer = null;
            chunkTimer = new ChunkTimer(millisLeft, 1000, new TimerHandle[]{this, handle});
            chunkTimer.start();
            isCanceled = false;
        } else {
            startTickTak();
            checkAudioValue();
            chunkTimer = null;
            chunkTimer = new ChunkTimer(TimeUnit.MILLISECONDS.convert(minutesInit, TimeUnit.MINUTES), 1000,  new TimerHandle[]{this, handle});
            chunkTimer.start();
            isCanceled = false;
            buttonStart.setText(getResources().getString(R.string.pause_text));
            buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_pause_24), null, null, null);
        }

        isStart = !isStart;
    }

    private void stopTikTak() {
        if (mediaPlayerTikTak != null && mediaPlayerTikTak.isPlaying()) {
            mediaPlayerTikTak.start();
        }
    }

    private void startTickTak() {
        if (isOnTicTak) mediaPlayerTikTak.start();
    }

    private void checkAudioValue() {
        int value = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (value < 8) {
            Toast.makeText(this, getResources().getString(R.string.volume_audion), Toast.LENGTH_SHORT).show();
        }
    }

    public void setIndicator(IndicatorRound indicatorRound) {
        this.indicatorRound = indicatorRound;
    }

    //start signal of timeout
    @Override
    public void stopTimer() {
        stopTikTak();
        //play gong
        mediaPlayer.start();
        pomodoro.finishRound();
        isStart = false;
        isCanceled = false;
    }

    //show notification
    private void showNotification(String time) {
        nb.setContentText(time);
        if (notificationService != null)
            notificationService.getNotificationManager().notify(idNotification, nb.build());
    }

    public int getMinutesInit() {
        return minutesInit;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @SuppressLint("NotificationId0")
    public void clearNotification() {
        isShowNotification = false;
        startForeground(0, null);
        notificationService.notificationClear(idNotification);
    }

    public void showNotification() {
        isShowNotification = true;
        startForeground(idNotification, nb.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        if (mediaPlayerTikTak != null) {
            mediaPlayerTikTak.stop();
            mediaPlayerTikTak = null;
        }

        chunkTimer = null;
        clearNotification();
        notificationService = null;

    }
}