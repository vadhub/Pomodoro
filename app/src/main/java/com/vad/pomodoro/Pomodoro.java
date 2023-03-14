package com.vad.pomodoro;

public class Pomodoro {
    private final static int WORK = 5;
    private final static int SHORT = 2;
    private final static int LONG = 3;

    private int round = 1;

    public int getMinutes() {
        System.out.println(round+"----------------------------");
        if (round % 2 == 0 && round != 8) {
            round++;
            return SHORT;
        } else if (round == 8) {
            round = 1;
            return LONG;
        } else {
            round++;
            return WORK;
        }
    }
}
