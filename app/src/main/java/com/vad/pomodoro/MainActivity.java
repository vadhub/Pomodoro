package com.vad.pomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements TimerHandle {

    private TextView textTime;
    private Button buttonStart;
    private ProgressBar progressBar;
    private int secondsInit;
    private MyService mService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.BinderTimer binderTimer = (MyService.BinderTimer) service;
            mService = binderTimer.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private void bindService() {
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        progressBar.setMax(20);
        progressBar.setProgress(20);
        textTime = (TextView) findViewById(R.id.textTimer);
        if (mService != null) secondsInit = mService.getSecondsInit();
        textTime.setText(String.format(Locale.ENGLISH, "%d:%d", TimeUnit.SECONDS.toMinutes(secondsInit),
                TimeUnit.SECONDS.toSeconds(secondsInit)));

    }

    //switch start and stop timer
    public void onStartTimer(View view) {
        bindService();
        if (mService != null) mService.setTimer(buttonStart);

    }

    //reset Timer
    public void onResetTimer(View view) {
        unbindService(serviceConnection);
        if (mService != null) mService.timerReset();
        buttonStart.setText("start");
        progressBar.setProgress(secondsInit);
        textTime.setText(
                String.format(Locale.ENGLISH, "%d:%d", TimeUnit.SECONDS.toMinutes(secondsInit),
                        TimeUnit.SECONDS.toSeconds(secondsInit)));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void showTime(long timeUntilFinished) {

        textTime.setText(
                String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(timeUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished))
        );

        progressBar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished));
    }

    @Override
    public void stopTimer() {
        if (mService != null) secondsInit = mService.getSecondsInit();
        progressBar.setProgress(secondsInit);
        buttonStart.setText("start");
        textTime.setText(
                String.format(Locale.ENGLISH, "%d:%d", TimeUnit.SECONDS.toMinutes(secondsInit), TimeUnit.SECONDS.toSeconds(secondsInit)));
    }
}