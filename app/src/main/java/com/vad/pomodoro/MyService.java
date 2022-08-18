package com.vad.pomodoro;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyService extends Service implements TimerHandle {

    private MediaPlayer mediaPlayer;
    private AudioManager manager;
    private final int idNotification = 1;
    private TomatoNotificationService notificationService;
    private NotificationCompat.Builder nb;
    private boolean isStart = false;
    private boolean isCanceled = false;
    private ChunkTimer chunkTimer;
    private int secondsInit = 20;
    private long millisLeft;
    private IBinder binder = new BinderTimer();

    public class BinderTimer extends Binder {

        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.gong);
        mediaPlayer.setLooping(true);
        notificationService = new TomatoNotificationService(this);
        nb = notificationService.showNotification();
        chunkTimer = new ChunkTimer(TimeUnit.SECONDS.toMillis(secondsInit), 1000, this);
        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        startForeground(idNotification, nb.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void showTime(long timeUntilFinished) {
        millisLeft = timeUntilFinished;
        showNotification(
                String.format(Locale.ENGLISH, "%d:%d", TimeUnit.MILLISECONDS.toMinutes(timeUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished))
        );
    }

    public void setTimer(Button buttonStart) {

        if (isStart && !isCanceled) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            buttonStart.setText("start");
            chunkTimer.cancel();
            isCanceled = true;
        } else if (!isStart && isCanceled) {
            checkAudioValue();
            buttonStart.setText("pause");
            chunkTimer = null;
            chunkTimer = new ChunkTimer(millisLeft, 1000, this);
            chunkTimer.start();
            isCanceled = false;
        } else {
            checkAudioValue();
            chunkTimer.start();
            isCanceled = false;
            buttonStart.setText("pause");
        }

        isStart = !isStart;
    }

    public void timerReset() {
        isStart = false;
        isCanceled = false;
        chunkTimer.cancel();
    }

    private void checkAudioValue() {
        int value = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (value < 8) {
            Toast.makeText(this, getResources().getString(R.string.volume_audion), Toast.LENGTH_SHORT).show();
        }
    }

    //start signal of timeout
    @Override
    public void stopTimer() {
        //play gong
        mediaPlayer.start();
        secondsInit = 20;
        isStart = false;
        isCanceled = false;
    }

    //show notification
    private void showNotification(String time) {
        nb.setContentText(time);
        notificationService.getNotificationManager().notify(idNotification, nb.build());
    }

    public int getSecondsInit() {
        return secondsInit;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        chunkTimer = null;
        notificationService.notificationClear(idNotification);
        notificationService = null;

    }
}