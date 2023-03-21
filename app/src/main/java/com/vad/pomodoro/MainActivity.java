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
    private ProgressBar progressBar;
    private int secondsInit;
    private MyService mService;
    private TextView roundTextView;

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
        textTime = (TextView) findViewById(R.id.textTimer);
        roundTextView = (TextView) findViewById(R.id.numRoundTextView);

        setTimer();

        textTime.setText(DateUtils.formatElapsedTime(secondsInit));

    }

    //switch start and stop timer
    public void onStartTimer(View view) {
        bindService();
        if (mService != null) mService.setTimer(buttonStart, this);
    }

    //reset Timer
    public void onResetTimer(View view) {
        if (mService != null) mService.timerReset();
        buttonStart.setText(getResources().getString(R.string.start_text));
        buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24), null, null, null);
        progressBar.setProgress(secondsInit);
        textTime.setText(DateUtils.formatElapsedTime(secondsInit));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    public void showTime(long timeUntilFinished) {
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished);
        textTime.setText(DateUtils.formatElapsedTime(seconds));
        progressBar.setProgress(seconds);
    }

    @Override
    public void stopTimer() {
        setTimer();
        buttonStart.setText(getResources().getString(R.string.start_text));
        buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24), null, null, null);
        textTime.setText(DateUtils.formatElapsedTime(secondsInit));
        roundTextView.setText("round #"+mService.getRound());
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