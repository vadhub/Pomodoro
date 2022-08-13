package com.vad.pomodoro;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements TimerHandle {

    private final int secondsInit = 1500;
    private ChunkTimer chunkTimer;
    private boolean isStart = false;
    private TextView textView;
    private Button buttonStart;
    private ProgressBar progressBar;
    private AudioManager manager;


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
        chunkTimer = new ChunkTimer(TimeUnit.SECONDS.toMillis(10), 1000, this);
        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        progressBar.setMax(secondsInit);
        progressBar.setProgress(secondsInit);
        textView = (TextView) findViewById(R.id.textTimer);
    }

    //switch start and stop timer
    public void onStartTimer(View view) {
        if (isStart) {
            audioValue();
            if (chunkTimer.stateTimer().equals(StateTimer.PAUSE)) {
                chunkTimer.onResume();
            } else {
                chunkTimer.start();
            }
            buttonStart.setText(R.string.pause_text);
        } else {
            chunkTimer.onPause();
            buttonStart.setText(R.string.start_text);
        }
        isStart = !isStart;
    }

    //reset Timer
    public void onResetTimer(View view) {
        isStart = false;
        progressBar.setProgress(secondsInit);
    }


    //get audio value in app to start
    private void audioValue() {
        int value = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (value < 8) {
            Toast.makeText(this, getResources().getString(R.string.volume_audion), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showTime(String timeUntilFinished) {
        textView.setText(timeUntilFinished);

    }

    @Override
    public void stopTimer() {
        chunkTimer.onFinish();
    }

    @Override
    public void resumeTimer(long millisInFuture) {
        chunkTimer = new ChunkTimer(TimeUnit.SECONDS.toMillis(millisInFuture), 1000, this);
        chunkTimer.start();
    }
}