package com.vad.pomodoro;

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
    private int indicate = 1;

    public void changeRound() {
        round++;
        if (round % 2 == 0) {
            currentState = SHORT;

            if (round == 8) {
                round = 0;
                indicate = 0;
                currentState = LONG;
            }
        } else {
            indicate++;
            currentState = WORK;
        }
        listener.change(indicate);
    }

    public void finishRound() {
        indicate++;
        listener.change(indicate);
        timeListener.changeTime(currentState);
    }

    public int getMinutes() {
        return currentState;
    }
}
