package com.vad.pomodoro;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    private static final String CHANNEL_ID = "channel11ID";
    private static final String CHANNEL_NAME = "channel11";

    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        createChannel();
    }

    private void createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            getNotificationManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getNotificationManager(){
        if (notificationManager==null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder getChannelNotificationBuilder(String title, String text){

        Intent intent = new Intent(this, MainActivity.class);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    }

    public Notification.Builder getNotification(String title, String text){
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new Notification.Builder(this).setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setVisibility(Notification.VISIBILITY_PUBLIC);
    }

    //clear all notification
    public void notificationClear(int NOTIFICATION_ID){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
