package com.vad.pomodoro.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveConfiguration {
    private SharedPreferences prefer;
    private final Context context;

    public SaveConfiguration(Context context) {
        this.context = context;
    }

    public void saveShowNotification(boolean isShow) {
        prefer = context.getSharedPreferences("pomodoro_save_show_notif", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefer.edit();
        ed.putBoolean("is_show", isShow);
        ed.apply();
    }

    public void saveKeepScreen(boolean isOn) {
        prefer = context.getSharedPreferences("pomodoro_save_keep_screen", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefer.edit();
        ed.putBoolean("is_on", isOn);
        ed.apply();
    }

    public void saveSoundTikTak(boolean isOn) {
        prefer = context.getSharedPreferences("pomodoro_save_tiktak", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefer.edit();
        ed.putBoolean("is_on", isOn);
        ed.apply();
    }

    public boolean getShowNotification() {
        prefer = context.getSharedPreferences("pomodoro_save_show_notif", Context.MODE_PRIVATE);
        return prefer.getBoolean("is_show", true);
    }

    public boolean getKeepScreen() {
        prefer = context.getSharedPreferences("pomodoro_save_keep_screen", Context.MODE_PRIVATE);
        return prefer.getBoolean("is_on", false);
    }

    public boolean geSoundTikTak() {
        prefer = context.getSharedPreferences("pomodoro_save_tiktak", Context.MODE_PRIVATE);
        return prefer.getBoolean("is_on", false);
    }

    public void savePomodoro(int pomodoro) {
        prefer = context.getSharedPreferences("pomodoro_save_pomodoro", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefer.edit();
        ed.putInt("pomodoro", pomodoro);
        ed.apply();
    }

    public int getPomodoro() {
        prefer = context.getSharedPreferences("pomodoro_save_pomodoro", Context.MODE_PRIVATE);
        return prefer.getInt("pomodoro", 25);
    }

    public void saveShort(int mShort) {
        prefer = context.getSharedPreferences("pomodoro_save_short", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefer.edit();
        ed.putInt("m_short", mShort);
        ed.apply();
    }

    public int getShort() {
        prefer = context.getSharedPreferences("pomodoro_save_short", Context.MODE_PRIVATE);
        return prefer.getInt("m_short", 5);
    }


    public void saveLong(int mLong) {
        prefer = context.getSharedPreferences("pomodoro_save_long", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefer.edit();
        ed.putInt("m_long", mLong);
        ed.apply();
    }

    public int getLong() {
        prefer = context.getSharedPreferences("pomodoro_save_long", Context.MODE_PRIVATE);
        return prefer.getInt("m_long", 15);
    }

}
