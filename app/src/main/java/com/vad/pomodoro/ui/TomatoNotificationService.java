package com.vad.pomodoro.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.vad.pomodoro.R;
import com.vad.pomodoro.ui.MainActivity;

public class TomatoNotificationService {
    public static final String ID_NOTIFICATION_CHANNEL = "notification_timer";
    private final Context context;
    private static final int REQUEST_CODE = 0x33234;

    public TomatoNotificationService(Context context) {
        this.context = context;
    }

    public NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public NotificationCompat.Builder showNotification() {

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_MUTABLE);

        return new NotificationCompat.Builder(context, ID_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.time))
                .setNotificationSilent()
                .setContentText("25:00")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    }

    public void notificationClear(int notificationId){
        getNotificationManager().cancel(notificationId);
    }
}
