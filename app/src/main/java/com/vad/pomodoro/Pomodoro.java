package com.vad.pomodoro;

import android.util.Log;

public class Pomodoro {
    private final static int WORK = 10;
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
        if (round % 2 == 0) {
            currentState = SHORT;
            Log.d("Pomodoro", "short");
            if (round == 8) {
                round = 0;
                currentState = LONG;
                Log.d("Pomodoro", "LONG");
            }
        } else {
            Log.d("Pomodoro", "work");
            currentState = WORK;
        }
        listener.change(round);
    }

    public void finishRound() {
        round++;
        listener.relax();
        changeRound();
        timeListener.changeTime(currentState);
    }

    public int getMinutes() {
        return currentState;
    }
}
