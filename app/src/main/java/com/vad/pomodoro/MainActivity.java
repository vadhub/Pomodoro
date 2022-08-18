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

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements TimerHandle {

    private TextView textTime;
    private Button buttonStart;
    private Button buttonUnbind;
    private ProgressBar progressBar;
    private int secondsInit;
    private MyService mService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.ENGLISH);

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
    protected void onStart() {
        super.onStart();
        bindService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonUnbind = (Button) findViewById(R.id.buttonUnbind);
        if (mService != null)
            secondsInit = mService.getSecondsInit();
        else
            secondsInit = (int) TimeUnit.MINUTES.toSeconds(25);
        progressBar.setMax(secondsInit);
        progressBar.setProgress(secondsInit);
        textTime = (TextView) findViewById(R.id.textTimer);
        textTime.setText(dateFormat.format(secondsInit));

    }

    //switch start and stop timer
    public void onStartTimer(View view) {
        bindService();
        buttonUnbind.setEnabled(true);
        if (mService != null) mService.setTimer(buttonStart, this);
    }

    //reset Timer
    public void onResetTimer(View view) {
        if (mService != null) mService.timerReset();
        buttonStart.setText("start");
        progressBar.setProgress(secondsInit);
        textTime.setText(dateFormat.format(secondsInit));
    }

    public void onUnbind(View view) {
        unbindService(serviceConnection);
        buttonUnbind.setEnabled(false);
    }

    @Override
    public void showTime(long timeUntilFinished) {

        textTime.setText(dateFormat.format(timeUntilFinished));

        progressBar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished));
    }

    @Override
    public void stopTimer() {
        if (mService != null)
            secondsInit = mService.getSecondsInit();
        else
            secondsInit = (int) TimeUnit.MINUTES.toSeconds(25);
        progressBar.setProgress(secondsInit);
        buttonStart.setText("start");
        textTime.setText(dateFormat.format(secondsInit));
    }
}