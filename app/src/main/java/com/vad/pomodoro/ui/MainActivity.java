package com.vad.pomodoro.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.vad.pomodoro.CheckOnService;
import com.vad.pomodoro.KeepScreen;
import com.vad.pomodoro.PomodoroUpdate;
import com.vad.pomodoro.R;
import com.vad.pomodoro.TikTakHandler;
import com.vad.pomodoro.TimerHandle;
import com.vad.pomodoro.domain.MyService;
import com.vad.pomodoro.model.SaveConfiguration;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements TimerHandle, CheckOnService, KeepScreen, TikTakHandler, PomodoroUpdate {

    private TextView textTime;
    private Button buttonStart;
    private Button buttonReset;
    private CircularProgressIndicator progressBar;
    private int secondsInit;
    private MyService mService;
    private long backPressedTime;

    private TextView roundTextView;

    private ImageView oneRound;
    private ImageView twoRound;
    private ImageView threeRound;
    private ImageView fourRound;
    private IndicatorRound indicatorRound;
    private SaveConfiguration configuration;

    private boolean permissionPostNotification;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.BinderTimer binderTimer = (MyService.BinderTimer) service;
            mService = binderTimer.getService();
            mService.setIndicator(indicatorRound);
            if (!permissionPostNotification) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionNotification();
                }
            }
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        BannerAdView mBanner = (BannerAdView) findViewById(R.id.adView);
        mBanner.setAdUnitId("R-M-2304842-1");
        mBanner.setAdSize(getAdSize(mBanner));
        AdRequest adRequest = new AdRequest.Builder().build();
        mBanner.loadAd(adRequest);

        buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(v -> {
            if (mService != null) {
                mService.reset();
                stopTimer();
            }
        });

        progressBar = (CircularProgressIndicator) findViewById(R.id.progressBar2);
        progressBar.setScaleX(-1f);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        textTime = (TextView) findViewById(R.id.textTimer);

        textTime.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));

        roundTextView = (TextView) findViewById(R.id.numRoundTextView);

        oneRound = findViewById(R.id.oneRound);
        twoRound = findViewById(R.id.twoRound);
        threeRound = findViewById(R.id.threeRound);
        fourRound = findViewById(R.id.fourRound);
        configuration = new SaveConfiguration(this);

        indicatorRound = new IndicatorRound(this, oneRound, twoRound, threeRound, fourRound, roundTextView, progressBar);

        if (configuration.getKeepScreen()) {
            int flags = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            getWindow().addFlags(flags);
        }
    }

    private BannerAdSize getAdSize(BannerAdView mBanner) {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // Calculate the width of the ad, taking into account the padding in the ad container.
        int adWidthPixels = mBanner.getWidth();
        if (adWidthPixels == 0) {
            // If the ad hasn't been laid out, default to the full screen width
            adWidthPixels = displayMetrics.widthPixels;
        }
        final int adWidth = Math.round(adWidthPixels / displayMetrics.density);

        return BannerAdSize.stickySize(this, adWidth);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void requestPermissionNotification() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            permissionPostNotification = true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private final ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            permissionPostNotification = true;
        } else {
            permissionPostNotification = false;
            showPermissionDialog();
        }
    });

    public void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Alert for Permission")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialog.dismiss();
                }).setNegativeButton("Exit", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.just_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            OptionDialogFragment dialogFragment = new OptionDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "settings_fragment");
            return true;
        } else if (item.getItemId() == R.id.timer) {
            PomodoroSettingsDialog dialogFragment = new PomodoroSettingsDialog();
            dialogFragment.show(getSupportFragmentManager(), "settings_pomodoro_fragment");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        float width = buttonStart.getMeasuredWidth() / 2f;
        float f = getResources().getDisplayMetrics().widthPixels / 2f;

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(buttonStart, "translationX", f + width + 10);
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(buttonReset, "translationX", f + width + 10);
        objectAnimator1.setDuration(300);
        objectAnimator1.setInterpolator(new DecelerateInterpolator());
        objectAnimator1.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService();
        setTimer();
    }

    //switch start and stop timer
    public void onStartTimer(View view) {
        if (mService != null) {
            mService.setTimer(buttonStart, this);
        }
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void stopTimer() {
        Log.d("##main", "stopTimer: ok");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);

        setTimer();
        buttonStart.setText(getResources().getString(R.string.start_text));
        buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24), null, null, null);
    }

    private void setTimer() {
        if (mService != null) {
            secondsInit = (int) TimeUnit.SECONDS.convert(mService.getMinutesInit(), TimeUnit.MINUTES);
        } else {
            secondsInit = (int) TimeUnit.SECONDS.convert(configuration.getPomodoro(), TimeUnit.MINUTES);
        }

        progressBar.setMax(secondsInit);
        progressBar.setProgress(secondsInit);
        textTime.setText(DateUtils.formatElapsedTime(secondsInit));
        ObjectAnimator.ofInt(progressBar, "progress", secondsInit)
                .setDuration(600)
                .start();
    }

    @Override
    public void accept(boolean isCheck) {
        if (isCheck) {
            mService.showNotification();
        } else {
            mService.clearNotification();
        }
    }

    @Override
    public void keep(boolean isCheck) {

        int flags = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        if (isCheck) {
            getWindow().addFlags(flags);
        } else {
            getWindow().clearFlags(flags);
        }
    }

    @Override
    public void onSwitch(boolean isOn) {
        if (mService != null) {
            mService.onSwitch(isOn);
        }
    }

    @Override
    public void update() {
        if (mService != null) {
            mService.pomodoroUpdate();
            setTimer();
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, getResources().getText(R.string.back_press), Toast.LENGTH_LONG).show();
        }

        backPressedTime = System.currentTimeMillis();

    }
}