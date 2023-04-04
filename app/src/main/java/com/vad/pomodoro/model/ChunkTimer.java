package com.vad.pomodoro.model;

import android.os.CountDownTimer;

import com.vad.pomodoro.TimerHandle;

public class ChunkTimer extends CountDownTimer {

    private TimerHandle[] timerHandles;

    public void setTimerHandles(TimerHandle[] timerHandles) {
        this.timerHandles = timerHandles;
    }

    public ChunkTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public ChunkTimer(long millisInFuture, long countDownInterval, TimerHandle[] timerHandles) {
        super(millisInFuture, countDownInterval);
        this.timerHandles = timerHandles;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        for (TimerHandle handle: timerHandles) {
            handle.showTime(millisUntilFinished);
        }
    }

    @Override
    public void onFinish() {
        for (TimerHandle handle: timerHandles) {
            handle.stopTimer();
        }
        System.out.println("FINISH");
    }
}
