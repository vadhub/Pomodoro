package com.vad.pomodoro;

public interface TimerHandle {
    void showTime(String timeUntilFinished);
    void stopTimer();
    void resumeTimer(long millisInFuture);
}
