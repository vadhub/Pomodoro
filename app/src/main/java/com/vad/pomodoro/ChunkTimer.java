package com.vad.pomodoro;

import android.os.CountDownTimer;

public class ChunkTimer extends CountDownTimer {

    private TimerHandle[] timerHandles;

    public void setTimerHandles(TimerHandle[] timerHandles) {
        this.timerHandles = timerHandles;
    }

    public ChunkTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
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
