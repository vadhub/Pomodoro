package com.vad.pomodoro.model;

import android.content.Context;

import com.vad.pomodoro.RoundListener;
import com.vad.pomodoro.TimeListener;

public class Pomodoro {
    private int work;
    private int mShort;
    private int mLong;
    private final RoundListener listener;
    private final TimeListener timeListener;
    private int currentState;
    private int round = 1;

    private final SaveConfiguration configuration;

    public Pomodoro(RoundListener listener, TimeListener timeListener, Context context) {
        this.listener = listener;
        this.timeListener = timeListener;
        configuration = new SaveConfiguration(context);

        work = configuration.getPomodoro();
        mShort = configuration.getShort();
        mLong = configuration.getLong();

        currentState = work;
    }

    public void changeRound() {
        round++;
        if (round % 2 == 0) {
            currentState = mShort;
            if (round == 8) {
                round = 0;
                currentState = mLong;
            }
        } else {
            currentState = work;
        }
        listener.change(round);
    }

    public void finishRound() {
        changeRound();
        timeListener.changeTime(currentState);
    }

    public int getMinutes() {
        return currentState;
    }

    public int reset() {
        return currentState;
    }

    public void updateTime() {
        work = configuration.getPomodoro();
        this.mShort = configuration.getShort();
        this.mLong = configuration.getLong();
    }
}
