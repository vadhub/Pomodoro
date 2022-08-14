package com.vad.pomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements TimerHandle {

    private int secondsInit = 20;
    private long millisLeft;
    private ChunkTimer chunkTimer;
    private TextView textTime;
    private Button buttonStart;
    private ProgressBar progressBar;
    private AudioManager manager;
    private boolean isStart = false;
    private boolean isCanceled = false;

    public static final int REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION = 1059;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // You don't have permission
                    checkPermission();
                } else {
                    // Do as per your logic
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        chunkTimer = new ChunkTimer(TimeUnit.SECONDS.toMillis(secondsInit), 1000, this);
        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        progressBar.setMax(secondsInit);
        progressBar.setProgress(secondsInit);
        textTime = (TextView) findViewById(R.id.textTimer);
        textTime.setText(
                String.format(Locale.ENGLISH, "%d: %d", TimeUnit.SECONDS.toMinutes(secondsInit),
                        TimeUnit.SECONDS.toSeconds(secondsInit)));
    }

    //switch start and stop timer
    public void onStartTimer(View view) {

        if (isStart && !isCanceled) {
            buttonStart.setText("start");
            chunkTimer.cancel();
            isCanceled = true;
        } else if (!isStart && isCanceled) {
            buttonStart.setText("pause");
            chunkTimer = null;
            chunkTimer = new ChunkTimer(millisLeft, 1000, this);
            chunkTimer.start();
            isCanceled = false;
        } else {
            chunkTimer.start();
            isCanceled = false;
            buttonStart.setText("pause");
        }

        isStart = !isStart;
        System.out.println(isCanceled);
    }

    //reset Timer
    public void onResetTimer(View view) {
        isStart = false;
        isCanceled = false;
        chunkTimer.cancel();
        buttonStart.setText("start");
        progressBar.setProgress(secondsInit);
        textTime.setText(
                String.format(Locale.ENGLISH, "%d:%d", TimeUnit.SECONDS.toMinutes(secondsInit),
                        TimeUnit.SECONDS.toSeconds(secondsInit)));
    }


    //get audio value in app to start
    private void audioValue() {
        int value = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (value < 8) {
            Toast.makeText(this, getResources().getString(R.string.volume_audion), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void showTime(long timeUntilFinished) {

        millisLeft = timeUntilFinished;

        textTime.setText(
                String.format("%d: %d", TimeUnit.MILLISECONDS.toMinutes(timeUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished))
        );

        progressBar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds(timeUntilFinished));
    }

    @Override
    public void stopTimer() {
        secondsInit = 20;
        isStart = false;
        isCanceled = false;
        progressBar.setProgress(secondsInit);
        buttonStart.setText("start");
    }
}