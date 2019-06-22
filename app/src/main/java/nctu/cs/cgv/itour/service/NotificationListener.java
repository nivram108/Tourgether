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
import nctu.cs.cgv.itour.object.FirebaseLogManager;

import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_CLICKED_COMMENT;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_CLICKED_HOT_CHECKIN;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_CLICKED_HOT_SPOT;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_CLICKED_LIKE;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_COMMENT;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_HOT_CHECKIN;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_HOT_SPOT;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_LIKE;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_REMOVE_COMMENT;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_REMOVE_HOT_CHECKIN;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_REMOVE_HOT_SPOT;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_NOTIFICATION_REMOVE_LIKE;

public class NotificationListener extends NotificationListenerService {

    public MainActivity mainActivity;
    @Override
    public void onCreate() {
        Log.d("MARVIN", "NL START");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn.getPackageName().equals("nctu.cs.cgv.itour") == false) return;
        Log.d("NotificationListener", "TAG:" + sbn.getTag());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn,
                                      NotificationListenerService.RankingMap rankingMap,
                                      int reason) {
        Log.d("NotificationListener", "remove package : " + sbn.getPackageName() + ", " + sbn.getTag() + " reasion:" + reason);

        if (sbn.getPackageName().equals("nctu.cs.cgv.itour") == false) return;
        String[] log = sbn.getTag().split(",");
        String logTag = "";
        String logMsg = "";
        if (log.length != 2) {
            logTag = "ERROR_NOTIFICATION";
        } else {
            Log.d("NotificationListener", "get log :" + logTag + ", " + logMsg);
            logTag = getLogTag(reason, log[1]);
            logMsg = log[0];
        }
        FirebaseLogManager firebaseLogManager = new FirebaseLogManager(getBaseContext());
        firebaseLogManager.log(logTag, logMsg);
    }

    String getLogTag(int reason, String tag) {
        String logTag = "";
        if (reason == 1) {
            if (tag.equals(LOG_NOTIFICATION_HOT_CHECKIN)) logTag = LOG_NOTIFICATION_CLICKED_HOT_CHECKIN;
            else if (tag.equals(LOG_NOTIFICATION_HOT_SPOT)) logTag = LOG_NOTIFICATION_CLICKED_HOT_SPOT;
            else if (tag.equals(LOG_NOTIFICATION_LIKE)) logTag = LOG_NOTIFICATION_CLICKED_LIKE;
            else if (tag.equals(LOG_NOTIFICATION_COMMENT)) logTag = LOG_NOTIFICATION_CLICKED_COMMENT;
        } else {
            if (tag.equals(LOG_NOTIFICATION_HOT_CHECKIN)) logTag = LOG_NOTIFICATION_REMOVE_HOT_CHECKIN;
            else if (tag.equals(LOG_NOTIFICATION_HOT_SPOT)) logTag = LOG_NOTIFICATION_REMOVE_HOT_SPOT;
            else if (tag.equals(LOG_NOTIFICATION_LIKE)) logTag = LOG_NOTIFICATION_REMOVE_LIKE;
            else if (tag.equals(LOG_NOTIFICATION_COMMENT)) logTag = LOG_NOTIFICATION_REMOVE_COMMENT;
        }
        return logTag;
    }

}