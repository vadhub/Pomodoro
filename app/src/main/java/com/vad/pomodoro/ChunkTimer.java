package com.vad.pomodoro;

import android.os.CountDownTimer;

public class ChunkTimer extends CountDownTimer {

    private final TimerHandle timerHandle;
    private long millisInFuture;

    public ChunkTimer(long millisInFuture, long countDownInterval, TimerHandle timerHandle) {
        super(millisInFuture, countDownInterval);
        this.timerHandle = timerHandle;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        millisInFuture = millisUntilFinished;
        timerHandle.showTime(millisUntilFinished);
    }

    @Override
    public void onFinish() {
        timerHandle.stopTimer();
        System.out.println("FINISH");
    }
}
