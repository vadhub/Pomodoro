package com.vad.pomodoro;

import static com.vad.pomodoro.TomatoNotificationService.ID_NOTIFICATION_CHANNEL;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    private NotificationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        creteChannel(manager);
    }

    private void creteChannel(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    ID_NOTIFICATION_CHANNEL,
                    "Tomato",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("Used for showing time until finish of tomato timer");
            manager.createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager() {
        return manager;
    }
}
