package com.vad.pomodoro;

import android.util.Log;

public class Pomodoro {
    private final static int WORK = 10;
    private final static int SHORT = 5;
    private final static int LONG = 15;
    private final RoundListener listener;
    private final TimeListener timeListener;
    private int currentState = WORK;
    private boolean isRelax = false;

    public Pomodoro(RoundListener listener, TimeListener timeListener) {
        this.listener = listener;
        this.timeListener = timeListener;
    }

    private int round = 1;

    public void changeRound() {
        round++;
        if (round % 2 == 0) {
            currentState = SHORT;
            isRelax = true;
            Log.d("Pomodoro", "short");
            if (round == 8) {
                round = 0;
                currentState = LONG;
                Log.d("Pomodoro", "LONG");
            }
        } else {
            Log.d("Pomodoro", "work");
            currentState = WORK;
            isRelax = false;
        }
        listener.change(round, isRelax);
    }

    public void finishRound() {
        changeRound();
        timeListener.changeTime(currentState);
    }

    public int getMinutes() {
        return currentState;
    }
}
