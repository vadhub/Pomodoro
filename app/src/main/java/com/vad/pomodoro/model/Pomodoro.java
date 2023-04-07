package com.vad.pomodoro.model;

import android.util.Log;

import com.vad.pomodoro.RoundListener;
import com.vad.pomodoro.TimeListener;

public class Pomodoro {
    private final static int WORK = 25;
    private final static int SHORT = 5;
    private final static int LONG = 15;
    private final RoundListener listener;
    private final TimeListener timeListener;
    private int currentState = WORK;

    public Pomodoro(RoundListener listener, TimeListener timeListener) {
        this.listener = listener;
        this.timeListener = timeListener;
    }

    private int round = 1;

    public void changeRound() {
        round++;
        if (round % 2 == 0) {
            currentState = SHORT;
            if (round == 8) {
                round = 0;
                currentState = LONG;
            }
        } else {
            currentState = WORK;
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
}