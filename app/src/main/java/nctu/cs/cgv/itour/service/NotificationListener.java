package nctu.cs.cgv.itour.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import nctu.cs.cgv.itour.activity.MainActivity;

public class NotificationListener extends NotificationListenerService {

    public MainActivity mainActivity;
    @Override
    public void onCreate() {
        Log.d("MARVIN", "START");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn.getPackageName().equals("nctu.cs.cgv.itour") == false) return;
        Log.d("MARVIN", "TAG:" + sbn.getTag());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn,
                                      NotificationListenerService.RankingMap rankingMap,
                                      int reason) {
        if (sbn.getPackageName().equals("nctu.cs.cgv.itour") == false) return;
        if (reason == REASON_CLICK) {
            Log.d("NIVRAM", "謝天謝地 " + sbn.getTag());
            SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("data", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("tappedNotificationCheckinId", sbn.getTag()).apply();
            sharedPreferences.edit().putBoolean("launchedByTappingNotification", true).apply();

            Log.d("NIVRAM", "clicked notification" + sharedPreferences.getString("tappedNotificationCheckinId", "") + ", " +
                    sharedPreferences.getBoolean("launchedByTappingNotification", false));
        }
    }


}