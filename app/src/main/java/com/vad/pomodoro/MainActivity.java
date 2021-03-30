package com.vad.pomodoro;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int seconds;
    private boolean isStart = false;
    private TextView textView;
    private Button buttonStart;
    private ProgressBar progressBar;

    private AdView mAdView;

    public static final int REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION = 1059;
    private NotificationHelper notificationHelper;

//
//    private void requestPermission() {
//
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SYSTEM_ALERT_WINDOW)== PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Permission access", Toast.LENGTH_SHORT).show();
//        } else {
//            requestAlertWindow();
//        }
//
//    }
//
//    private void requestAlertWindow() {
//        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SYSTEM_ALERT_WINDOW)){
//            new AlertDialog.Builder(this).setTitle("Permission Requires").setMessage("Permission ALERT WINDOW")
//                    .setPositiveButton("Granted", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.SYSTEM_ALERT_WINDOW}, REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
//                        }
//                    }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            }).show();
//        }else {
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SYSTEM_ALERT_WINDOW}, REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(requestCode == REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION){
//            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

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

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        checkPermission();
        seconds = 1500;
        progressBar.setMax(seconds);
        progressBar.setProgress(seconds);

        if(savedInstanceState!=null){
            seconds = savedInstanceState.getInt("seconds");
            isStart = savedInstanceState.getBoolean("isStart");
        }

        //checkout state start timer
        if(loadStartingState()){
            isStart = loadStartingState();
            seconds = loadSeconds();
            buttonStart.setText(R.string.pause_text);
        }else{
            buttonStart.setText(R.string.start_text);
        }

        textView = (TextView) findViewById(R.id.textTimer);
        runVasiaRun();

        notificationHelper = new NotificationHelper(this);
    }

    //switch start and stop timer
    public void onStartTime(View view) {
        if(!loadStartingState()){
            isStart=true;
            //set timer
            scheduleNotification(seconds);
            audioValue();
            buttonStart.setText(R.string.pause_text);
        }else{
            isStart=false;
            buttonStart.setText(R.string.start_text);
            stopService();
        }
        startSave(isStart);
    }

    //reset Timer
    public void onResetTime(View view) {
        isStart=false;
        startSave(isStart);
        seconds=1500;
        progressBar.setProgress(seconds);
        stopService();
    }

    //start countdown
    private void runVasiaRun(){

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = seconds/60;
                int sec = seconds % 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, sec);
                textView.setText(time);

                if(isStart){
                    seconds--;
                    progressBar.setProgress(seconds);
                    if(seconds==0){
                        isStart=false;
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    // save state app
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", seconds);
        outState.putBoolean("isStart", isStart);
    }
    // set countdown timer
    private void scheduleNotification (int delay) {
        if(isStart){
            Intent service = new Intent(this, MyService.class);
            service.putExtra("delay", delay);
            startService(service);
        }
    }

    //stop service
    private void stopService(){
        Intent service = new Intent(this, MyService.class);
        stopService(service);
        notificationHelper.notificationClear(1);
    }

    //get audio value in app to start
    private void audioValue(){
        AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int value =  manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if(value<8){
            Toast toast = Toast.makeText(this, getResources().getString(R.string.volume_audion), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0,0);
            toast.show();
        }
    }

    // load seconds from service
    private int loadSeconds(){
        SharedPreferences sPref = getSharedPreferences("saveState", Context.MODE_PRIVATE);
        return sPref.getInt("seconds", 0);
    }

    // load state start timer service
    private boolean loadStartingState(){
        SharedPreferences sPref = getSharedPreferences("saveStateOnStart", Context.MODE_PRIVATE);
        return sPref.getBoolean("isStarting", false);
    }

    // save state timer start
    private void startSave(boolean isStart){
        SharedPreferences sPref = getSharedPreferences("saveStateOnStart", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isStarting", isStart);
        ed.commit();
    }
}