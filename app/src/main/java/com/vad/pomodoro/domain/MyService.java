package com.vad.pomodoro.domain;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.vad.pomodoro.R;
import com.vad.pomodoro.RoundListener;
import com.vad.pomodoro.TimeListener;
import com.vad.pomodoro.TimerHandle;
import com.vad.pomodoro.model.AlarmHandler;
import com.vad.pomodoro.model.ChunkTimer;
import com.vad.pomodoro.model.Pomodoro;
import com.vad.pomodoro.model.TikTakHandle;
import com.vad.pomodoro.ui.IndicatorRound;
import com.vad.pomodoro.ui.TomatoNotificationService;

import java.util.concurrent.TimeUnit;

public class MyService extends Service implements TimerHandle, RoundListener, TimeListener {


    private AudioManager manager;
    private final int idNotification = 0x11234c;
    private TomatoNotificationService notificationService;
    private NotificationCompat.Builder nb;
    private boolean isStart = false;
    private boolean isCanceled = false;
    private boolean isShowNotification;
    private ChunkTimer chunkTimer;
    private int minutesInit;
    private long millisLeft;
    private Pomodoro pomodoro;
    private IndicatorRound indicatorRound;
    private final IBinder binder = new BinderTimer();
    private TikTakHandle tikTakHandle;
    private AlarmHandler alarmHandler;

    public class BinderTimer extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void change(int round) {
        indicatorRound.changeRound(round);
    }

    @Override
    public void changeTime(int time) {
        minutesInit = time;
    }

    public void onSwitch(boolean isOn) {
        tikTakHandle.onSwitch(isOn);
    }

    public void pomodoroUpdate() {
        pomodoro = new Pomodoro(this, this, this);
        minutesInit = pomodoro.getMinutes();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pomodoro = new Pomodoro(this, this, this);
        alarmHandler = new AlarmHandler(this);
        tikTakHandle = new TikTakHandle(this);
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
            alarmHandler.stopAlarm();
            buttonStart.setText(getResources().getString(R.string.start_text));
            buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24), null, null, null);
            chunkTimer.cancel();
            isCanceled = true;
            tikTakHandle.pause();
            Log.d("##service", "1");
        } else if (!isStart && isCanceled) {
            tikTakHandle.play();
            checkAudioValue();
            buttonStart.setText(getResources().getString(R.string.pause_text));
            buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_pause_24), null, null, null);
            chunkTimer = null;
            chunkTimer = new ChunkTimer(millisLeft, 1000, new TimerHandle[]{this, handle});
            chunkTimer.start();
            isCanceled = false;
            Log.d("##service", "2");
        } else {
            alarmHandler.stopAlarm();
            tikTakHandle.play();
            checkAudioValue();
            chunkTimer = null;
            chunkTimer = new ChunkTimer(TimeUnit.MILLISECONDS.convert(minutesInit, TimeUnit.MINUTES), 1000,  new TimerHandle[]{this, handle});
            chunkTimer.start();
            isCanceled = false;
            buttonStart.setText(getResources().getString(R.string.pause_text));
            buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_pause_24), null, null, null);
            Log.d("##service", "3");
        }

        isStart = !isStart;
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

    public void reset() {
        isStart = false;
        isCanceled = false;
        chunkTimer.cancel();
        chunkTimer = null;
        tikTakHandle.pause();
        minutesInit = pomodoro.getMinutes();
    }

    //start signal of timeout
    @Override
    public void stopTimer() {
        //play gong
        alarmHandler.playAlarm();
        tikTakHandle.pause();
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
        alarmHandler.cancelAlarm();

        tikTakHandle.cancel();

        chunkTimer = null;
        clearNotification();
        notificationService = null;

    }
}