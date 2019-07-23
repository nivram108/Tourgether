package nctu.cs.cgv.itour.object;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nctu.cs.cgv.itour.activity.MainActivity;

import static android.content.Context.MODE_PRIVATE;
import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_CHECKIN_ADD;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_CHECKIN_OPEN;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_REPORT_ANYWHERE;
import static nctu.cs.cgv.itour.object.FirebaseLogData.LOG_REPORT_CHECKIN;

public class FirebaseLogManager {

    public Context mContext;
    public FirebaseLogManager(Context context) {
        mContext = context;
        initUserName();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    }
    public FirebaseLogManager() {

    }
    public void log(String tag, String msg) {
        log(tag, msg, "");
    }

    public void log(String tag, String msg, String note) {
        initUserId();

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("data", MODE_PRIVATE);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String pushKey = databaseReference.child("log").child(mapTag).child(uid).child("collections").push().getKey();
        final String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String lat = Float.toString(sharedPreferences.getFloat("lat", 0));
        String lng = Float.toString(sharedPreferences.getFloat("lng", 0));
        Log.d("LOCATIONGETTTVVV", lat + ", " + lng);
        FirebaseLogData firebaseLogData = new FirebaseLogData(uid, tag, msg, note, name, lat, lng);
        Map<String, Object> logValue = firebaseLogData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/log/" + mapTag + "/" + uid + "/collections/" + pushKey, logValue);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                        //likeMsg.setText("");
                        Log.e("NIVRAM", "ERROR GG");
                    }
                });
        if (tag.equals(LOG_REPORT_ANYWHERE)) {
            // SET NOTE
            logPoi(firebaseLogData);
        }
        if (tag.equals(LOG_REPORT_ANYWHERE)){
            logPoi(firebaseLogData);
        } else if (tag.equals(LOG_REPORT_CHECKIN)){
            String location = ((MainActivity)mContext).checkinMap.get(msg).location;
            if (((MainActivity)mContext).personalFragment.togoFragment.togoItemAdapter.isTogo(location) == false) {
                logPoi(firebaseLogData);
            }
        }
        else if (tag.equals(LOG_CHECKIN_OPEN)) {
            addViewCheckinCount();
            initUserName();
        } else if (tag.equals(LOG_CHECKIN_ADD)) {
            addAddCheckinCount();
            isTogo(note);
        }

    }

    public void logPoi(FirebaseLogData firebaseLogData) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String pushKey = databaseReference.child("log_poi").child(mapTag).child(uid).child("collections").push().getKey();
        Map<String, Object> logValue = firebaseLogData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/log_poi/" + mapTag + "/" + uid + "/collections/" + pushKey, logValue);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                        //likeMsg.setText("");
                        Log.e("NIVRAM", "ERROR GG");
                    }
                });
        addPoiCount();
        initUserName();
    }

    public void queryLog() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query q = databaseReference.child("log").child(mapTag).child(uid).child("collections");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Log.d("FirebaseComplete", dataSnapshot.getKey().toString());

                    int counter = 0;
                    if (dataSnapshot.getKey().toString().equals("collections")) {
                        List<FirebaseLogData> firebaseLogDataList = new ArrayList<>();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for(DataSnapshot child : children) {

                            counter ++;
                            Log.d("FirebaseComplete", "count:" + counter + ", " + Long.toString(dataSnapshot.getChildrenCount()));
                            FirebaseLogData firebaseLogData = child.getValue(FirebaseLogData.class);
                            firebaseLogDataList.add(firebaseLogData);
                            if(counter >= dataSnapshot.getChildrenCount()) {
                                Log.d("FirebaseComplete", "COMPLETE");
                            }
                        }
                    }
                } catch (Exception ignored) {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        q.addListenerForSingleValueEvent(listener);
    }


    public void addPoiCount() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query q = databaseReference.child("log_summary").child(mapTag).child(uid).child("poi_count");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot.getValue() == null) {
                        databaseReference.child("log_summary").child(mapTag).child(uid).child("poi_count").setValue(1);
                    } else {
                        databaseReference.child("log_summary").child(mapTag).child(uid).child("poi_count").
                                setValue(Integer.valueOf(dataSnapshot.getValue().toString()) + 1);
                    }
                } catch (Exception ignored) {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        q.addListenerForSingleValueEvent(listener);
    }

    public void initUserName() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query q = databaseReference.child("log_summary").child(mapTag).child(uid).child("name");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot.getValue() == null) {
                        databaseReference.child("log_summary").child(mapTag).child(uid).child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    }
                } catch (Exception ignored) {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        q.addListenerForSingleValueEvent(listener);
    }
    public void initUserId() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query q = databaseReference.child("log_summary").child(mapTag).child(uid).child("uid");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot.getValue() == null) {
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("data", MODE_PRIVATE);
                        String userId = sharedPreferences.getString("givenUserId", "");
                        databaseReference.child("log_summary").child(mapTag).child(uid).child("uid").setValue(userId);
                    }
                } catch (Exception ignored) {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        q.addListenerForSingleValueEvent(listener);
    }
    public void addViewCheckinCount() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query q = databaseReference.child("log_summary").child(mapTag).child(uid).child("view_checkin_count");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot.getValue() == null) {
                        databaseReference.child("log_summary").child(mapTag).child(uid).child("view_checkin_count").setValue(1);
                    } else {
                        databaseReference.child("log_summary").child(mapTag).child(uid).child("view_checkin_count").
                                setValue(Integer.valueOf(dataSnapshot.getValue().toString()) + 1);
                    }
                } catch (Exception ignored) {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        q.addListenerForSingleValueEvent(listener);
    }

    public void addAddCheckinCount() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query q = databaseReference.child("log_summary").child(mapTag).child(uid).child("add_checkin_count");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot.getValue() == null) {
                        databaseReference.child("log_summary").child(mapTag).child(uid).child("add_checkin_count").setValue(1);
                    } else {
                        databaseReference.child("log_summary").child(mapTag).child(uid).child("add_checkin_count").
                                setValue(Integer.valueOf(dataSnapshot.getValue().toString()) + 1);
                    }
                } catch (Exception ignored) {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        q.addListenerForSingleValueEvent(listener);
    }

    public void isTogo(final String location) {

        Log.d("USERIDGET", "Start Querying");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query q = databaseReference.child("togo_list").child(mapTag).child(uid);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (true) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        boolean isPoi = true;
                        for(DataSnapshot child : children) {
                            TogoPlannedData togoPlannedData = child.getValue(TogoPlannedData.class);
                            if (togoPlannedData.locationName.equals(location)) {
                                isPoi = false;
                                break;
                            }
                        }
                        if (isPoi) addPoiCount();
                    }
                } catch (Exception ignored) {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        q.addListenerForSingleValueEvent(listener);
    }
}
