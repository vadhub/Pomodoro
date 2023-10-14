package com.vad.pomodoro.ui;

import static android.content.Context.POWER_SERVICE;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.vad.pomodoro.CheckOnService;
import com.vad.pomodoro.KeepScreen;
import com.vad.pomodoro.PomodoroUpdate;
import com.vad.pomodoro.R;
import com.vad.pomodoro.TikTakHandler;
import com.vad.pomodoro.TimerHandle;
import com.vad.pomodoro.domain.MyService;
import com.vad.pomodoro.model.SaveConfiguration;

import java.util.concurrent.TimeUnit;

public class TimerFragment extends Fragment implements TimerHandle, CheckOnService, KeepScreen, TikTakHandler, PomodoroUpdate {

    private TextView textTime;
    private Button buttonStart;
    private Button buttonReset;
    private CircularProgressIndicator progressBar;
    private Context context;
    private int secondsInit;
    private MyService mService;

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buttonReset = view.findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(v -> {
            if (mService != null) {
                mService.reset();
                stopTimer();
            }
        });

        progressBar = (CircularProgressIndicator) view.findViewById(R.id.progressBar2);
        progressBar.setScaleX(-1f);

        buttonStart = (Button) view.findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener( v -> onStartTimer());

        textTime = (TextView) view.findViewById(R.id.textTimer);

        textTime.setTypeface(Typeface.createFromAsset(view.getContext().getAssets(), "fonts/Roboto-Thin.ttf"));

        roundTextView = (TextView) view.findViewById(R.id.numRoundTextView);

        oneRound = view.findViewById(R.id.oneRound);
        twoRound = view.findViewById(R.id.twoRound);
        threeRound = view.findViewById(R.id.threeRound);
        fourRound = view.findViewById(R.id.fourRound);
        configuration = new SaveConfiguration(view.getContext());

        indicatorRound = new IndicatorRound(view.getContext(), oneRound, twoRound, threeRound, fourRound, roundTextView, progressBar);

        if (configuration.getKeepScreen()) {
            int flags = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            getActivity().getWindow().addFlags(flags);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bindService();
        setTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unbindService(serviceConnection);
    }

    //switch start and stop timer
    public void onStartTimer() {
        if (mService != null) {
            mService.setTimer(buttonStart, this);
        }
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
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock  wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);

        setTimer();
        buttonStart.setText(getResources().getString(R.string.start_text));
        buttonStart.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24), null, null, null);
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
            getActivity().getWindow().addFlags(flags);
        } else {
            getActivity().getWindow().clearFlags(flags);
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
        if(mService != null) {
            mService.pomodoroUpdate();
            setTimer();
        }
    }

    private void bindService() {
        Intent intent = new Intent(context, MyService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void requestPermissionNotification() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
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
        new AlertDialog.Builder(context)
                .setTitle("Alert for Permission")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialog.dismiss();
                }).setNegativeButton("Exit", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
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
}