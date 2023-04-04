package com.vad.pomodoro.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SaveConfiguration {
    private SharedPreferences prefer;
    private Context context;

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

    public boolean getShowNotification() {
        prefer = context.getSharedPreferences("pomodoro_save_show_notif", Context.MODE_PRIVATE);
        return prefer.getBoolean("is_show", true);
    }

    public boolean getKeepScreen() {
        prefer = context.getSharedPreferences("pomodoro_save_keep_screen", Context.MODE_PRIVATE);
        return prefer.getBoolean("is_on", false);
    }
}
