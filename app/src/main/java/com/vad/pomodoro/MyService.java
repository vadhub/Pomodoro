package com.vad.pomodoro;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.TimeUnit;

public class MyService extends Service implements TimerHandle {

    private MediaPlayer mediaPlayer;
    private AudioManager manager;
    private final int idNotification = 0x11234c;
    private TomatoNotificationService notificationService;
    private NotificationCompat.Builder nb;
    private boolean isStart = false;
    private boolean isCanceled = false;
    private ChunkTimer chunkTimer;
    private int minutesInit;
    private long millisLeft;
    private final Pomodoro pomodoro = new Pomodoro();
    private final IBinder binder = new BinderTimer();

    public class BinderTimer extends Binder {

        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.gong);
        notificationService = new TomatoNotificationService(this);
        nb = notificationService.showNotification();
        minutesInit = pomodoro.getMinutes();
        chunkTimer = new ChunkTimer(TimeUnit.MILLISECONDS.convert(minutesInit, TimeUnit.MINUTES), 1000);
        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        startForeground(idNotification, nb.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        chunkTimer = new ChunkTimer(TimeUnit.MILLISECONDS.convert(minutesInit, TimeUnit.MINUTES), 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void showTime(long timeUntilFinished) {
        millisLeft = timeUntilFinished;
        showNotification(DateUtils.formatElapsedTime((int) TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished)));
    }

    public void setTimer(Button buttonStart, TimerHandle handle) {
        System.out.println("##setTimer-------"+ minutesInit + millisLeft);
        if (isStart && !isCanceled) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            buttonStart.setText(getResources().getString(R.string.start_text));
            buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24), null, null, null);
            chunkTimer.cancel();
            isCanceled = true;
        } else if (!isStart && isCanceled) {
            checkAudioValue();
            buttonStart.setText(getResources().getString(R.string.pause_text));
            buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_pause_24), null, null, null);
            chunkTimer = null;
            chunkTimer = new ChunkTimer(millisLeft, 1000, new TimerHandle[]{this, handle});
            chunkTimer.start();
            isCanceled = false;
        } else {
            checkAudioValue();
            chunkTimer.setTimerHandles(new TimerHandle[]{this, handle});
            chunkTimer.start();
            isCanceled = false;
            buttonStart.setText(getResources().getString(R.string.pause_text));
            buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_pause_24), null, null, null);
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
        minutesInit = (int) TimeUnit.SECONDS.convert(pomodoro.getMinutes(), TimeUnit.MINUTES);
        System.out.println("##pomod" + minutesInit);
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