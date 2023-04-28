package com.vad.pomodoro;

import static com.vad.pomodoro.ui.TomatoNotificationService.ID_NOTIFICATION_CHANNEL;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.yandex.mobile.ads.common.MobileAds;

public class App extends Application {

    private static final String YANDEX_MOBILE_ADS_TAG = "YandexMobileAds";

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, () -> Log.d(YANDEX_MOBILE_ADS_TAG, "SDK initialized"));
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
