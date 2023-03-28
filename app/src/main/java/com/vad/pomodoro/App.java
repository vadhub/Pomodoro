package com.vad.pomodoro;

import static com.vad.pomodoro.TomatoNotificationService.ID_NOTIFICATION_CHANNEL;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        creteChannel(manager);
    }

    private void creteChannel(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    ID_NOTIFICATION_CHANNEL,
                    "Tomato",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription(getResources().getString(R.string.notification_channel));
            manager.createNotificationChannel(channel);
        }
    }
}
