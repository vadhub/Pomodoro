package com.vad.pomodoro.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.vad.pomodoro.CheckOnService;
import com.vad.pomodoro.KeepScreen;
import com.vad.pomodoro.R;
import com.vad.pomodoro.TikTakHandler;
import com.vad.pomodoro.TimerHandle;
import com.vad.pomodoro.domain.MyService;
import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements TimerHandle, CheckOnService, KeepScreen, TikTakHandler {

    private TextView textTime;
    private Button buttonStart;
    private ProgressBar progressBar;
    private int secondsInit;
    private MyService mService;

    private TextView roundTextView;

    private ImageView oneRound;
    private ImageView twoRound;
    private ImageView threeRound;
    private ImageView fourRound;
    private ProgressBarAnimation anim;
    private IndicatorRound indicatorRound;

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
        mBanner.setAdSize(AdSize.stickySize(AdSize.FULL_SCREEN.getWidth()));
        AdRequest adRequest = new AdRequest.Builder().build();
        mBanner.loadAd(adRequest);

        Button resetBtn = findViewById(R.id.buttonReset);
        resetBtn.setOnClickListener(v -> {
            if (mService != null) {
                mService.reset();
                stopTimer();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        anim = new ProgressBarAnimation(progressBar, 0, 1500);
        anim.setDuration(600);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        textTime = (TextView) findViewById(R.id.textTimer);

        roundTextView = (TextView) findViewById(R.id.numRoundTextView);

        oneRound = findViewById(R.id.oneRound);
        twoRound = findViewById(R.id.twoRound);
        threeRound = findViewById(R.id.threeRound);
        fourRound = findViewById(R.id.fourRound);

        indicatorRound = new IndicatorRound(this, oneRound, twoRound, threeRound, fourRound, roundTextView, progressBar);

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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        float width = buttonStart.getMeasuredWidth() / 2f;
        float f = getResources().getDisplayMetrics().widthPixels/2f;

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(buttonStart, "translationX", f + width+10);
        objectAnimator.setDuration(500);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
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

    @Override
    public void stopTimer() {
        setTimer();
        buttonStart.setText(getResources().getString(R.string.start_text));
        buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24), null, null, null);
    }

    private void setTimer() {
        if (mService != null) {
            secondsInit = (int) TimeUnit.SECONDS.convert(mService.getMinutesInit(), TimeUnit.MINUTES);
        } else {
            secondsInit = (int) TimeUnit.SECONDS.convert(25, TimeUnit.MINUTES);
        }

        progressBar.setAnimation(anim);
        progressBar.setMax(secondsInit);
        progressBar.setProgress(secondsInit);
        textTime.setText(DateUtils.formatElapsedTime(secondsInit));
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
}