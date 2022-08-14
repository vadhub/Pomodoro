package com.vad.pomodoro;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import androidx.core.app.NotificationCompat;

public class ManagerNotificatorTime {

    private Context context;
    private NotificationManager manager;

    public ManagerNotificatorTime(Context context){
        this.context=context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    //show notification
    public void outTime(String time){

        NotificationHelper notificationHelper;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotificationBuilder("Time",time);
            manager.notify(1, nb.build());
        }else {
            notificationHelper = new NotificationHelper(context);
            Notification.Builder nb = notificationHelper.getNotification("Time", time);
            manager.notify(1, nb.build());
        }
    }

    public void clear(){
        manager.cancel(1);
    }
}
