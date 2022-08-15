package com.vad.pomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        progressBar.setMax(20);
        progressBar.setProgress(20);
        textTime = (TextView) findViewById(R.id.textTimer);
        textTime.setText("25:00");

    }

    //switch start and stop timer
    public void onStartTimer(View view) {

    }

    //reset Timer
    public void onResetTimer(View view) {

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
        progressBar.setProgress(secondsInit);
        buttonStart.setText("start");
        textTime.setText(
                String.format(Locale.ENGLISH, "%d:%d", TimeUnit.SECONDS.toMinutes(secondsInit), TimeUnit.SECONDS.toSeconds(secondsInit)));
    }

}