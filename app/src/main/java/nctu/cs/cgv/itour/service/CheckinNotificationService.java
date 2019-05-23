package nctu.cs.cgv.itour.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import nctu.cs.cgv.itour.object.SystemNotification;
import nctu.cs.cgv.itour.object.UserData;

import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.Utility.notifyCheckin;
import static nctu.cs.cgv.itour.Utility.pushNews;

public class CheckinNotificationService extends Service {
    private static final String TAG = "CheckinNotification";
    private NotificationManager notificationManager;
    private String channelId = "hot notification";
    private long currentTimestamp;
    private String uid;
    private UserData userData = null;
    private Query notiQuery;
    private ChildEventListener notiListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "熱門通知",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 300, 300, 300, 300});
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "service start");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            this.stopSelf();
            return START_NOT_STICKY;
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        currentTimestamp = System.currentTimeMillis() / 1000;
        notiQuery = FirebaseDatabase.getInstance().getReference().child("notification").child(mapTag);
        notiListener = notiQuery.orderByChild("timestamp").startAt(currentTimestamp).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Log.d(TAG, "DATA CHANGED!");
                    SystemNotification systemNotification =
                            dataSnapshot.getValue(SystemNotification.class);
                    if (systemNotification == null) return;
                    if (systemNotification.targetUid.equals("all") || systemNotification.targetUid.equals(uid)) {
                        notifyCheckin(getApplicationContext(), systemNotification, notificationManager, channelId);
                        pushNews(systemNotification, dataSnapshot.getKey());
                    }
                } catch (Exception ignore) {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (notiQuery != null && notiListener != null) notiQuery.removeEventListener(notiListener);
        } catch (Exception ignore) {

        }
    }
}
