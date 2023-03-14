package com.vad.pomodoro;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

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

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context, ID_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Time")
                .setNotificationSilent()
                .setContentText("25:00")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    }

    public void notificationClear(int notificationId){
        getNotificationManager().cancel(notificationId);
    }
}
