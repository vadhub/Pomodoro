package com.vad.pomodoro;

public class Pomodoro {
    private final static int WORK = 1;
    private final static int SHORT = 1;
    private final static int LONG = 1;

    private Pomodor pomodor;

    private int round = 1;

    public int getMinutes() {
        if (round % 2 == 0) {
            if (round == 4) {
                round = 1;
                pomodor = Pomodor.LONG;
                return LONG;
            }
            round++;
            pomodor = Pomodor.SHORT;
            return SHORT;
        } else {
            round++;
            pomodor = Pomodor.WORK;
            return WORK;
        }
    }

    public Pomodor getPomodor() {
        return pomodor;
    }

    public int getRound() {
        return round;
    }
}
