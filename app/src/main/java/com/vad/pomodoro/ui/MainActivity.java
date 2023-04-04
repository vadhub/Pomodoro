package com.vad.pomodoro.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vad.pomodoro.CheckOnService;
import com.vad.pomodoro.KeepScreen;
import com.vad.pomodoro.domain.MyService;
import com.vad.pomodoro.R;
import com.vad.pomodoro.TimerHandle;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements TimerHandle, CheckOnService, KeepScreen {

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

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.BinderTimer binderTimer = (MyService.BinderTimer) service;
            mService = binderTimer.getService();
            mService.setIndicator(indicatorRound);
            Log.d("##service", "onServiceConnected"+secondsInit);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private void bindService() {
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d("##service", "bindService");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        Log.d("##service", "onCreate");

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
        objectAnimator.setDuration(600);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService();
        setTimer();
        Log.d("##4", textTime.getX()+" "+textTime.getWidth());
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
            Log.d("##service", "1");
        } else {
            Log.d("##service", "2");
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
        if (isCheck) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }
}