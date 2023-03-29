package com.vad.pomodoro;

public class Pomodoro {
    private final static int WORK = 25;
    private final static int SHORT = 10;
    private final static int LONG = 15;
    private final RoundListener listener;
    private int currentState = WORK;

    public Pomodoro(RoundListener listener) {
        this.listener = listener;
    }

    private int round = 0;

    public void changeRound() {
        int minutes;
        round++;
        if (round % 2 == 0) {
            minutes = SHORT;

            if (round == 8) {
                round = 1;
                minutes = LONG;
            }
        } else {
            minutes = WORK;
        }
        listener.change(round);
        currentState = minutes;
    }

    public int getMinutes() {
        return currentState;
    }
}
