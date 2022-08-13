package com.vad.pomodoro;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;

import java.util.concurrent.TimeUnit;

public class ChunkTimer extends CountDownTimer {

    private final TimerHandle timerHandle;
    private long millisInFuture;
    private StateTimer stateTimer = StateTimer.STOP;

    public ChunkTimer(long millisInFuture, long countDownInterval, TimerHandle timerHandle) {
        super(millisInFuture, countDownInterval);
        this.timerHandle = timerHandle;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onTick(long millisUntilFinished) {
        stateTimer = StateTimer.START;
        millisInFuture = millisUntilFinished;
        timerHandle.showTime(
                String.format("%d: %d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)))
        );
    }

    @Override
    public void onFinish() {
        timerHandle.stopTimer();
        stateTimer = StateTimer.STOP;
    }

    public void onPause() {
        stateTimer = StateTimer.PAUSE;
        cancel();
    }

    public void onResume() {
        stateTimer = StateTimer.RESUME;
        timerHandle.resumeTimer(millisInFuture);
    }

    public StateTimer stateTimer() {
        return stateTimer;
    }
}
