package nctu.cs.cgv.itour.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.activity.MainActivity;
import nctu.cs.cgv.itour.object.UserData;

import static nctu.cs.cgv.itour.MyApplication.fileDownloadURL;
import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.Utility.actionLog;
import static nctu.cs.cgv.itour.Utility.notifyCheckin;
import static nctu.cs.cgv.itour.Utility.notifyLike;
import static nctu.cs.cgv.itour.Utility.pushNews;
import static nctu.cs.cgv.itour.activity.MainActivity.CHECKIN_NOTIFICATION_REQUEST;

public class LikeNotificationService extends Service {
    private static final String TAG = "LikeNotificationService";
    private NotificationManager notificationManager;
    private String channelId = "like notification";
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
        Log.d("NIVRAM", "EXCUSE ME WHAT THe FUCK CREATE");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "新的讚",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 50, 100, 100});
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NIVRAM", "EXCUSE ME WHAT THe FUCK START");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            this.stopSelf();
            return START_NOT_STICKY;
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        currentTimestamp = System.currentTimeMillis() / 1000;
        notiQuery = FirebaseDatabase.getInstance().getReference().child("like_notification").child(mapTag);
        notiListener = notiQuery.orderByChild("timestamp").startAt(currentTimestamp).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Log.d("NIVRAM", "COMMENT DATA CHANGED!");
                    nctu.cs.cgv.itour.object.LikeNotification likeNotification =
                            dataSnapshot.getValue(nctu.cs.cgv.itour.object.LikeNotification.class);

                    if (likeNotification == null) return;

                    if (likeNotification.likedUid.equals(uid) && (likeNotification.likeUid.equals(uid) == false)) {
                        notifyLike(getApplicationContext(), likeNotification, notificationManager, channelId);
                        //pushNews(likeNotification, dataSnapshot.getKey());
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
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        sendBroadcast(new Intent("likeNotificationService"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Log.d("NIVRAM", "EXCUSE ME WHAT THe FUCK DESTROY???");
            //if (notiQuery != null && notiListener != null) notiQuery.removeEventListener(notiListener);
        } catch (Exception ignore) {

        }
    }
}
