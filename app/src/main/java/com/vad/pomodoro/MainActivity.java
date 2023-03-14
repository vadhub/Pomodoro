package com.vad.pomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements TimerHandle {

    private TextView textTime;
    private Button buttonStart;
    private Button buttonUnbind;
    private ProgressBar progressBar;
    private int secondsInit;
    private MyService mService;
    private TextView roundTextView;
    private int round = 0;

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
        textTime = (TextView) findViewById(R.id.textTimer);
        roundTextView = (TextView) findViewById(R.id.numRoundTextView);

        setTimer();
        textTime.setText(DateUtils.formatElapsedTime(secondsInit));

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
        textTime.setText(DateUtils.formatElapsedTime(secondsInit));
    }

    public void onUnbind(View view) {
        unbindService(serviceConnection);
        buttonUnbind.setEnabled(false);
    }

    @Override
    public void showTime(long timeUntilFinished) {
        textTime.setText(DateUtils.formatElapsedTime(secondsInit));
        progressBar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished));
    }

    @Override
    public void stopTimer() {
        roundTextView.setText(round++ + "");
        setTimer();
        buttonStart.setText("start");
        textTime.setText(DateUtils.formatElapsedTime(secondsInit));
    }

    private void setTimer() {
        if (mService != null)
            secondsInit = mService.getMinutesInit();
        else
            secondsInit = (int) TimeUnit.SECONDS.convert(25, TimeUnit.MINUTES);

        progressBar.setMax(secondsInit);
        progressBar.setProgress(secondsInit);
    }
}