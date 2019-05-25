package nctu.cs.cgv.itour.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.Utility;
import nctu.cs.cgv.itour.custom.MyViewPager;
import nctu.cs.cgv.itour.fragment.CheckinDialogFragment;
import nctu.cs.cgv.itour.fragment.ListFragment;
import nctu.cs.cgv.itour.fragment.MapFragment;
import nctu.cs.cgv.itour.fragment.NewsFragment;
import nctu.cs.cgv.itour.fragment.PersonalFragment;
import nctu.cs.cgv.itour.fragment.SettingsFragment;
import nctu.cs.cgv.itour.fragment.TogoFragment;
import nctu.cs.cgv.itour.object.Checkin;
import nctu.cs.cgv.itour.object.CheckinNode;
import nctu.cs.cgv.itour.object.EdgeNode;
import nctu.cs.cgv.itour.object.Mesh;
import nctu.cs.cgv.itour.object.SpotCategory;
import nctu.cs.cgv.itour.object.SystemNotification;
import nctu.cs.cgv.itour.object.SpotList;
import nctu.cs.cgv.itour.object.SpotNode;
import nctu.cs.cgv.itour.object.UserData;
import nctu.cs.cgv.itour.service.AudioFeedbackService;
import nctu.cs.cgv.itour.service.CheckinNotificationService;
import nctu.cs.cgv.itour.service.CommentNotificationService;
import nctu.cs.cgv.itour.service.GpsLocationService;
import nctu.cs.cgv.itour.service.LikeNotificationService;
//import nctu.cs.cgv.itour.service.NotificationListener;
import nctu.cs.cgv.itour.service.NotificationListener;
import nctu.cs.cgv.itour.service.ScreenShotService;

import static nctu.cs.cgv.itour.MyApplication.audioFeedbackFlag;
import static nctu.cs.cgv.itour.MyApplication.dirPath;
import static nctu.cs.cgv.itour.MyApplication.edgeNode;
import static nctu.cs.cgv.itour.MyApplication.logFlag;
import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.MyApplication.realMesh;
import static nctu.cs.cgv.itour.MyApplication.screenCaptureFlag;
import static nctu.cs.cgv.itour.MyApplication.spotList;
import static nctu.cs.cgv.itour.MyApplication.warpMesh;
import static nctu.cs.cgv.itour.Utility.notifyCheckin;
import static nctu.cs.cgv.itour.Utility.pushNews;

public class MainActivity extends AppCompatActivity implements
//        SettingsFragment.OnFogListener,
//        SettingsFragment.OnDistanceIndicatorListener,
        SettingsFragment.OnCheckinIconListener,
        SettingsFragment.OnSpotIonListener {

    public static final int CHECKIN_NOTIFICATION_REQUEST = 321;
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    private static final int SCREEN_OVERLAY_PERMISSON_REQUEST = 456;
    private static final int SCREEN_CAPTURE_REQUEST = 789;
    // Checkins
    public static Map<String, Checkin> checkinMap;
    public static Map<String, Boolean> collectedCheckinKey;
    public static UserData userData;
    // view objects
    private MyViewPager viewPager;
    private BottomBar bottomBar;
    private List<Fragment> fragmentList;
    // MapFragment: communicate by calling fragment method
    private MapFragment mapFragment;
    private ListFragment listFragment;
    private PersonalFragment personalFragment;
    // use broadcast to receive gpsUpdate and fogUpdate
    private BroadcastReceiver messageReceiver;
    // device sensor manager
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gravity;
    private float[] geomagnetic;

    private Query checkinQuery;
    private ChildEventListener checkinListener;
    private Query commentNotificationQuery;
    private ChildEventListener commentNotificationListener;
    private Query savePostIdQuery;
    private ChildEventListener savePostIdListener;
    private Query ungoQuery;
    private ValueEventListener ungoListener;

    private boolean noticeCheckinFlag = false;
    private String notification_lat;
    private String notification_lng;
    private String notification_location;
    private String notification_key;
    private NotificationManager notificationManager, commentNotificationManager;
    private String notificationChannelId = "nearby notification";
    private String commentNotificationChannelId = "comment notification";
    private String likeNotificationChannelId = "like notification";
    private HandlerThread checkLaunchedByNotificationThread;
    private Handler checkLaunchedByNotificationThreadHandler;
    public static SpotCategory spotCategory;
    private static boolean activityIsVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotCategory = new SpotCategory();
        setContentView(R.layout.activity_main);
        checkPermission();
//        showCheckinDialog();

        if (checkLaunchedByNotificationThread == null)
            checkLaunchedByNotificationThread = new HandlerThread("checkLaunchedByNotificationThread");
        if (checkLaunchedByNotificationThread.isAlive() == false)
            checkLaunchedByNotificationThread.start();
        if (checkLaunchedByNotificationThreadHandler == null)
            checkLaunchedByNotificationThreadHandler = new Handler(checkLaunchedByNotificationThread.getLooper());
        checkLaunchedByNotificationThreadHandler.post(listenNotificationClicked);
    }

    private void init() {

        realMesh = new Mesh(new File(dirPath + "/" + mapTag + "_mesh.txt"));
        realMesh.readBoundingBox(new File(dirPath + "/" + mapTag + "_bound_box.txt"));
        warpMesh = new Mesh(new File(dirPath + "/" + mapTag + "_warpMesh.txt"));
        spotList = new SpotList(new File(dirPath + "/" + mapTag + "_spot_list.txt"));
        edgeNode = new EdgeNode(new File(dirPath + "/" + mapTag + "_edge_length.txt"));

        checkinMap = new LinkedHashMap<>();
        collectedCheckinKey = new LinkedHashMap<>();

        startService(new Intent(this, GpsLocationService.class));
        startService(new Intent(this, CommentNotificationService.class));
        startService(new Intent(this, LikeNotificationService.class));
        startService(new Intent(this, NotificationListener.class));
        setSensors();
        String string = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
//        if (!string.contains(NotificationListener.class.getName())) {
//            startActivity(new Intent(
//                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
//        }
        setBroadcastReceiver();
        setCheckinPreference();
        setView();
        setNotificationManager();
        setCommentNotificationChannel();
        setLikeNotificationChannel();

        if (logFlag && FirebaseAuth.getInstance().getCurrentUser() != null)
            queryUserUngo();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startService(new Intent(this, CheckinNotificationService.class));
            Log.d("NIVRAM", "EXCUSE ME WHAT THe FUCK");
//            startService(new Intent(this, CommentNotificationService.class));
        }
        if (logFlag && screenCaptureFlag && FirebaseAuth.getInstance().getCurrentUser() != null)
            requestScreenCapture();
    }

    private void setNotificationManager() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    notificationChannelId,
                    "熱門通知",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 300, 300, 300, 300});
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setCommentNotificationChannel() {
        commentNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    commentNotificationChannelId,
                    "新的留言",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 300, 300, 300, 300});
            commentNotificationManager.createNotificationChannel(channel);
        }
    }

    private void setLikeNotificationChannel() {
        commentNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    likeNotificationChannelId,
                    "新的讚",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 50, 100, 100});
            commentNotificationManager.createNotificationChannel(channel);
        }
    }

    private void setCheckinPreference() {
        // show checkin icon as default
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("checkin", true);
        editor.apply();
    }

    public void queryUserUngo() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ungoQuery = FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).child("data").child(mapTag);
        ungoListener = ungoQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    userData = dataSnapshot.getValue(UserData.class);
                } catch (Exception ignore) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void notifySpot(float lat, float lng) {
        if (!logFlag || FirebaseAuth.getInstance().getCurrentUser() == null) return;
        if (userData == null || userData.ungo == null) return;

        String spotName = "";
        float minDist = 101f;
        for (SpotNode spot : spotList.nodeMap.values()) {
            float dist = Utility.isNearBy(lat, lng, Float.valueOf(spot.lat), Float.valueOf(spot.lng));
            if (dist <= 100f && dist < minDist) {
                spotName = spot.name;
                minDist = dist;
            }
        }

        // 0: 沒去過, 1: 不確定, 2: 有去過
        if (!spotName.equals("") && MainActivity.userData.ungo.containsKey(spotName) && MainActivity.userData.ungo.get(spotName) == 0) {
            CheckinNode checkinNode = mapFragment.spotNodeMap.get(spotName).checkinNode;
            if (checkinNode != null && checkinNode.checkinList.size() > 1) {
                Checkin checkin = checkinNode.checkinList.get(checkinNode.checkinList.size() - 1);
                String title = "就在你附近！有人在" + spotName + "打卡了唷！";
                String msg = "距離" + String.valueOf((int) minDist) + "公尺";
                SystemNotification systemNotification = new SystemNotification(checkin.key, checkin.uid, "all",
                        title, msg, checkin.photo, checkin.location, checkin.lat, checkin.lng, System.currentTimeMillis() / 1000);
                pushNews(systemNotification, "");
                notifyCheckin(this, systemNotification, notificationManager, notificationChannelId);

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference()
                        .child("users").child(uid).child("data").child(mapTag).child("ungo").child(spotName)
                        .setValue(2);
            }
        }
    }

    public void queryCheckin() {
        checkinMap.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        checkinQuery = databaseReference.child("checkin").child(mapTag);

        checkinListener = checkinQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Checkin checkin = dataSnapshot.getValue(Checkin.class);
                    checkin.key = dataSnapshot.getKey();

                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if (!checkin.targetUid.equals("all") && !checkin.targetUid.equals(uid))
                            return;
                    }


                    checkinMap.put(dataSnapshot.getKey(), checkin);
                    mapFragment.addCheckin(checkin);
                    personalFragment.personalMapFragment.addCheckin(checkin);
                    Log.d("NIVRAM", "checkin add");
//                        showCheckinDialog();


                } catch (Exception ignore) {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // like change
                // Something change
                try {
                    Checkin checkin = dataSnapshot.getValue(Checkin.class);
                    checkin.key = dataSnapshot.getKey();

                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if (!checkin.targetUid.equals("all") && !checkin.targetUid.equals(uid))
                            return;
                    }

                    checkinMap.put(dataSnapshot.getKey(), checkin);
                    listFragment.checkinItemAdapter.notifyDataSetChanged();
                    personalFragment.notifyCollectedCheckinChanged();
                    personalFragment.notifyPostedCheckinChanged();
                    mapFragment.changeCheckin(checkin);
                    personalFragment.personalMapFragment.changeCheckin(checkin);
                    Log.d("NIVRAM", "child change");
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try {
                    Checkin checkin = dataSnapshot.getValue(Checkin.class);
                    checkin.key = dataSnapshot.getKey();

                    checkinMap.remove(dataSnapshot.getKey());
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d("NIVRAM", "checkin:" + Integer.toString(checkinMap.size()));
    }

    public void querySavedPostId() {
        collectedCheckinKey.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        savePostIdQuery = databaseReference.child("users").child(uid).child("saved").child(mapTag);

        savePostIdListener = savePostIdQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    collectedCheckinKey.put(dataSnapshot.getKey(), (Boolean) dataSnapshot.getValue());
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    collectedCheckinKey.put(dataSnapshot.getKey(), (Boolean) dataSnapshot.getValue());
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try {
                    collectedCheckinKey.remove(dataSnapshot.getKey());
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setView() {
        mapFragment = MapFragment.newInstance();
        listFragment = ListFragment.newInstance();
        personalFragment = PersonalFragment.newInstance();
        fragmentList = new ArrayList<>();
        fragmentList.add(mapFragment);
        fragmentList.add(listFragment);
        fragmentList.add(personalFragment);
        fragmentList.add(NewsFragment.newInstance());
        fragmentList.add(TogoFragment.newInstance());
        fragmentList.add(SettingsFragment.newInstance());
//        fragmentList.add(NotificationFragment.newInstance());
        //TODO: add fragment here

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        // disable swipe
        viewPager.setPagingEnabled(false);

        // set keep all three pages alive
        // add one more page
        viewPager.setOffscreenPageLimit(5);

        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_map:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_list:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_person:
                        viewPager.setCurrentItem(2);
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_guest_function), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.tab_news:
                        viewPager.setCurrentItem(3);
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_guest_function), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.tab_togo:
                        viewPager.setCurrentItem(4);
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_guest_function), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.tab_settings:
                        viewPager.setCurrentItem(5);
                        break;
                }
            }
        });
    }

    private void setBroadcastReceiver() {
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                switch (intent.getAction()) {
                    case "gpsUpdate":
                        mapFragment.handleGpsUpdate(
                                intent.getFloatExtra("lat", 0),
                                intent.getFloatExtra("lng", 0));
                        personalFragment.personalMapFragment.handleGpsUpdate(
                                intent.getFloatExtra("lat", 0),
                                intent.getFloatExtra("lng", 0));
                        break;
                    case "fogUpdate":
                        notifySpot(intent.getFloatExtra("lat", 0), intent.getFloatExtra("lng", 0));
//                        mapFragment.handleFogUpdate(
//                                intent.getFloatExtra("lat", 0),
//                                intent.getFloatExtra("lng", 0));
                        break;
                }
            }
        };

        // Register onCreate; Un-register onDestroy
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("gpsUpdate");
        intentFilter.addAction("fogUpdate");
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);
    }

    private void setSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    gravity = event.values;

                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    geomagnetic = event.values;

                if (gravity != null && geomagnetic != null) {
                    float R[] = new float[9];
                    float I[] = new float[9];
                    boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
                    if (success) {
                        float orientation[] = new float[3];
                        SensorManager.getOrientation(R, orientation);
                        mapFragment.handleSensorChange(orientation[0]);
//                        personalFragment.personalMapFragment.handleSensorChange(orientation[0]);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        // Register onCreate; Un-register onDestroy
        if (accelerometer != null) {
            sensorManager.registerListener(
                    sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        if (magnetometer != null) {
            sensorManager.registerListener(
                    sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void requestSystemOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, SCREEN_OVERLAY_PERMISSON_REQUEST);
        } else {
            Intent service = new Intent(this, AudioFeedbackService.class);
            startService(service);
        }
    }

    private void requestScreenCapture() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), SCREEN_CAPTURE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCREEN_CAPTURE_REQUEST:
                if (resultCode == RESULT_OK && screenCaptureFlag) {
                    Intent service = new Intent(this, ScreenShotService.class);
                    service.putExtra("resultCode", resultCode);
                    service.putExtra("resultData", data);
                    startService(service);
                }
                break;
            case SCREEN_OVERLAY_PERMISSON_REQUEST:
                if (resultCode == RESULT_OK && audioFeedbackFlag) {
                    Intent service = new Intent(this, AudioFeedbackService.class);
                    startService(service);
                }
                break;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent.getBooleanExtra("checkinNotificationIntent", false)) {
            Utility.actionLog("notice checkin", intent.getStringExtra("title"), intent.getStringExtra("key"));

            notification_lat = intent.getStringExtra("lat");
            notification_lng = intent.getStringExtra("lng");
            notification_location = intent.getStringExtra("location");
            notification_key = intent.getStringExtra("key");
            noticeCheckinFlag = true;
        }


    }

    @Override
    protected void onResume() {

        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        Log.d("NIVRAM", "onResume, " + sharedPreferences.getBoolean("launchedByTappingNotification", false));
        if (checkLaunchedByNotificationThread == null)
            checkLaunchedByNotificationThread = new HandlerThread("checkLaunchedByNotificationThread");
        if (checkLaunchedByNotificationThread.isAlive() == false)
            checkLaunchedByNotificationThread.start();
        if (checkLaunchedByNotificationThreadHandler == null)
            checkLaunchedByNotificationThreadHandler = new Handler(checkLaunchedByNotificationThread.getLooper());
        checkLaunchedByNotificationThreadHandler.post(listenNotificationClicked);
        activityIsVisible = true;

        if (logFlag && audioFeedbackFlag && FirebaseAuth.getInstance().getCurrentUser() != null)
            requestSystemOverlayPermission();

        if (noticeCheckinFlag) {
            noticeCheckinFlag = false;
            try {
                if (notification_key.equals("")) {
                    if (notification_location.equals("")) {
                        onLocateClick(notification_lat, notification_lng);
                    } else {
                        onLocateClick(notification_location);
                    }
                } else {
                    onLocateCheckinClick(notification_key);
                }

            } catch (Exception ignore) {

            }
        }
    }

    @Override
    protected void onPause() {
        activityIsVisible = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);

        if (magnetometer != null || accelerometer != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }

        checkinQuery.removeEventListener(checkinListener);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            savePostIdQuery.removeEventListener(savePostIdListener);

        if (logFlag && FirebaseAuth.getInstance().getCurrentUser() != null)
            ungoQuery.removeEventListener(ungoListener);
    }

//    @Override
//    public void onDistanceIndicatorSwitched(boolean flag) {
//        mapFragment.switchDistanceIndicator(flag);
//    }

//    @Override
//    public void onFogSwitched(boolean flag) {
//        mapFragment.switchFog(flag);
//    }

    @Override
    public void onCheckinIconSwitched(boolean flag) {
        mapFragment.switchCheckinIcon(flag);
        personalFragment.personalMapFragment.switchCheckinIcon(flag);
    }

    @Override
    public void onSpotIconSwitched(boolean flag) {
        mapFragment.switchSpotIcon(flag);
    }

    public void onLocateClick(String lat, String lng) {
        bottomBar.selectTabAtPosition(0);
        mapFragment.onLocateClick(lat, lng);
    }

    public void onLocateClick(String location) {
        bottomBar.selectTabAtPosition(0);
        mapFragment.onLocateClick(location);
    }

    public void onLocateCheckinClick(String postId) {
        bottomBar.selectTabAtPosition(0);
        mapFragment.onLocateCheckinClick(checkinMap.get(postId));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("NIVRAM", "onStart");
//        showCheckinDialog();

    }
    @Override
    protected void onStop() {
        super.onStop();
        activityIsVisible = false;
    }
    private void checkPermission() {
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int gpsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int micPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (gpsPermission + storagePermission + micPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_MULTIPLE_REQUEST);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean gpsPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean micPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    // ignore mic permission
                    if (logFlag == false || audioFeedbackFlag == false) micPermission = true;

                    if (storagePermission && gpsPermission && micPermission) {
                        init();
                    } else {
                        checkPermission();
                    }
                }
                break;
        }
    }

    private Runnable listenNotificationClicked = new Runnable() {
        @Override
        public void run() {
            while (activityIsVisible == true) {
                if (checkinMap.size()!= 0) {
                    SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                    if (sharedPreferences.getBoolean("launchedByTappingNotification", false) == true) {
                        showCheckinDialog();
                        return;
                    }
                }
            }

        }
    };
    public void showCheckinDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
//        Log.d("NIVRAM", "show: " + sharedPreferences.getString("tappedNotificationCheckinId", "") + ", " +
//                sharedPreferences.getBoolean("launchedByTappingNotification", false));
        if (true) {
            Log.d("NIVRAM" ,"LETS SHOW");
            sharedPreferences.edit().putBoolean("launchedByTappingNotification", false).apply();
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
//            for (Fragment fragment : fragments) {
//                if (fragment instanceof CheckinDialogFragment) {
//                    CheckinDialogFragment checkinDialogFragment = (CheckinDialogFragment) fragment;
////                    checkinDialogFragment.dismissAllowingStateLoss();
//                }
//            }
//            if (checkinMap.size() == 0) {
//                getCheckinFromFirebase(sharedPreferences.getString("tappedNotificationCheckinId", ""));
//                Checkin checkin = notificationCheckin;
////                queryCheckin();
//            }
            CheckinDialogFragment checkinDialogFragment = CheckinDialogFragment.
                    newInstance(sharedPreferences.getString("tappedNotificationCheckinId", ""));

            checkinDialogFragment.show(fragmentManager, "fragment_checkin_dialog");
        }
    }

}
