package com.vad.pomodoro;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Locale;

public class MyService extends Service {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private int seconds;
    private boolean isStart;
    private final long[] pattern = {500, 500, 500};
    private SharedPreferences sPref;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.rington1);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        seconds = intent.getIntExtra("delay", 0);
        runVasiaRun();
        isStart = loadStartingState();
        saveStateOnStart(isStart);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null&&vibrator!=null){
            mediaPlayer.stop();
            isStart=false;
            vibrator.cancel();
        }
    }

    private void runVasiaRun(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = seconds/60;
                int sec = seconds % 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, sec);
                outTime(time);

                if(isStart){
                    seconds--;
                    if(seconds==0){
                        isStart=false;
                        saveStateOnStart(isStart);
                        startCall();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    //start signal of timeout
    private void startCall(){
        //play rington
        mediaPlayer.start();

        //start pushActivity
        Intent pushActivity = new Intent(this, PushActivity.class);
        pushActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(pushActivity);

        //vibrate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 1));
        } else {
            //deprecated in API 26
            vibrator.vibrate(pattern, 1);
        }
    }

    //show notification
    private void outTime(String time){
        saveState(seconds);
        NotificationHelper notificationHelper;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationHelper = new NotificationHelper(MyService.this);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotificationBuilder("Time",time);
            startForeground(1, nb.build());
        }else {
            notificationHelper = new NotificationHelper(MyService.this);
            Notification.Builder nb = notificationHelper.getNotification("Time", time);
            startForeground(1, nb.build());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void saveState(int seconds){
        sPref = getSharedPreferences("saveState", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("seconds", seconds);
        ed.commit();
    }

    private void saveStateOnStart(boolean isStart){
        sPref = getSharedPreferences("saveStateOnStart", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isStarting", isStart);
        ed.commit();
    }

    private boolean loadStartingState(){
        SharedPreferences sPref = getSharedPreferences("saveStateOnStart", Context.MODE_PRIVATE);
        return sPref.getBoolean("isStarting", false);
    }
}