package com.vad.pomodoro;

public interface TimerHandle {
    void showTime(long timeUntilFinished);
    void stopTimer();
}
