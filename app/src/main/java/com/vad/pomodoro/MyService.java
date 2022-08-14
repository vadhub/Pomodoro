package com.vad.pomodoro;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyService extends Service implements TimerHandle {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private final long[] pattern = {500, 500, 500};
    private NotificationHelper notificationHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.gong);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer.setLooping(true);
        notificationHelper = new NotificationHelper(MyService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null&&vibrator!=null){
            mediaPlayer.stop();
            vibrator.cancel();
        }
    }

    @Override
    public void showTime(long timeUntilFinished) {
        showNotification(
                String.format(Locale.ENGLISH,"%d:%d", TimeUnit.MILLISECONDS.toMinutes(timeUntilFinished),
                TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished))
        );
    }

    //start signal of timeout
    @Override
    public void stopTimer(){
        //play gong
        mediaPlayer.start();

        //vibrate start
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 1));
        } else {
            //deprecated in API 26
            vibrator.vibrate(pattern, 1);
        }
    }

    //show notification
    private void showNotification(String time){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationCompat.Builder nb = notificationHelper.getChannelNotificationBuilder("Time",time);
            startForeground(1, nb.build());
        }else {
            Notification.Builder nb = notificationHelper.getNotification("Time", time);
            startForeground(1, nb.build());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}