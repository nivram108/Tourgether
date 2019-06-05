package nctu.cs.cgv.itour.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.roughike.bottombar.BottomBar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import nctu.cs.cgv.itour.activity.MainActivity;
import nctu.cs.cgv.itour.custom.ItemClickSupport;
import nctu.cs.cgv.itour.custom.NewsItemAdapter;
import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.object.CommentNotification;
import nctu.cs.cgv.itour.object.LikeNotification;
import nctu.cs.cgv.itour.object.NotificationType;
import nctu.cs.cgv.itour.object.SystemNotification;

import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.Utility.actionLog;
import static nctu.cs.cgv.itour.Utility.dpToPx;


/**
 * Query SystemNotification, CommentNotification and LikeNotification and store in hashMap of each notification type.
 * While obtaining new data, add to hashMap in respectively and update displaying arrayList in NewsItemAdapter
 * */
public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";
    private ActionBar actionBar;
    private NewsItemAdapter newsItemAdapter;
    public Map<String, SystemNotification> systemNotificationMap;
    public Map<String, CommentNotification> commentNotificationMap;
    public Map<String, LikeNotification> likeNotificationMap;
    public static NewsFragment newInstance() {
        Log.d("NEWS", "newInstance");
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("NEWS", "onCreate");
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        newsItemAdapter = new NewsItemAdapter(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.d("NEWS", "onCreateView");
        return inflater.inflate(R.layout.fragment_news, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("NEWS", "onViewCreated");

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;
        if (systemNotificationMap != null) systemNotificationMap.clear();
        else systemNotificationMap = new LinkedHashMap<String, SystemNotification>();
        if (commentNotificationMap != null) commentNotificationMap.clear();
        else commentNotificationMap = new LinkedHashMap<String, CommentNotification>();
        if (likeNotificationMap != null) likeNotificationMap.clear();
        else likeNotificationMap = new LinkedHashMap<String, LikeNotification>();
        RecyclerView newsList = view.findViewById(R.id.recycle_view);
        Context context = getContext();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        DividerItemDecoration itemDecor = new DividerItemDecoration(context, ((LinearLayoutManager)layoutManager).getOrientation());
        newsList.setAdapter(newsItemAdapter);
        newsList.setLayoutManager(layoutManager);
        newsList.addItemDecoration(itemDecor);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        querySystemNotification();
        queryCommentNotification();
        queryLikeNotification();

        ItemClickSupport.addTo(newsList).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        newsItemAdapter.getItem(position).setChecked();
                        newsItemAdapter.notifyDataSetChanged();
                        clearFocusNotificationIcon();
                        NotificationType notificationType= newsItemAdapter.getItem(position);
                        if (notificationType.type == NotificationType.TYPE_SYSTEM_NOTIFICATION) {
                            SystemNotification systemNotification = systemNotificationMap.get(notificationType.key);
                            if (systemNotification.uid.equals("")) {
                                ((MainActivity)getActivity()).onLocateClick(systemNotification.lat, systemNotification.lng);
                                SpotDescritionDialogFragment spotDescritionDialogFragment = SpotDescritionDialogFragment.newInstance(systemNotification.location);
                                spotDescritionDialogFragment.show(getFragmentManager(), "SpotDescritionDialogFragment");
                            } else {
                                showCheckinDialog(systemNotification.postId);
                            }
                            //TODO: make log
                        } else if (notificationType.type == NotificationType.TYPE_COMMENT_NOTIFICATION) {
                            showCheckinDialog(notificationType.key);
                            //TODO: make log
                        } else if (notificationType.type == NotificationType.TYPE_LIKE_NOTIFICATION) {
                            showCheckinDialog(notificationType.key);
                        }

//                        if (commentNotification.postId.equals("")) {
//                            if (notification.location.equals("")) {
//                                ((MainActivity) Objects.requireNonNull(getActivity())).onLocateClick(notification.lat, notification.lng);
//                            } else {
//                                ((MainActivity) Objects.requireNonNull(getActivity())).onLocateClick(notification.location);
//                            }
//                        } else {
//                            ((MainActivity) Objects.requireNonNull(getActivity())).onLocateCheckinClick(notification.postId);
//                        }
//
//                        actionLog("click news", notification.msg, notification.postId);
                    }
                }
        );
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("NEWS", "setUserVisibleHint");

        if (getUserVisibleHint()) {
            if (actionBar != null) {
                actionBar.setElevation(0);
                actionBar.setSubtitle(getString(R.string.subtitle_news));
            }
        } else {
            if (actionBar != null) {
                actionBar.setElevation(dpToPx(getContext(), 4));
            }
        }
    }
    private void querySystemNotification() {
        Query systemNotificationQuery = FirebaseDatabase.getInstance().getReference().child("notification").child(mapTag);
        systemNotificationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                nctu.cs.cgv.itour.object.SystemNotification systemNotification = dataSnapshot.getValue(nctu.cs.cgv.itour.object.SystemNotification.class);
                if (systemNotification == null) return;
                if (systemNotification.targetUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ||
                        systemNotification.targetUid.equals("all")) {
                    systemNotificationMap.put(dataSnapshot.getKey(), systemNotification);
                    newsItemAdapter.add(dataSnapshot.getKey());
                    requestFocusNotificationIcon();
//                    Log.d("NIVRAM", "q c noti key:" + systemNotification.likedCheckinKey);
                }
                // notification from own checkin while not liked by self

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
    }

    private void queryCommentNotification() {
        Query commentNotificationQuery = FirebaseDatabase.getInstance().getReference().child("comment_notification").child(mapTag);
        commentNotificationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                nctu.cs.cgv.itour.object.CommentNotification commentNotification = dataSnapshot.getValue(nctu.cs.cgv.itour.object.CommentNotification.class);
                if (commentNotification == null) return;
                if (commentNotification.commentedUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        && (commentNotification.commentUid.equals(commentNotification.commentedUid) == false)) {
                    commentNotificationMap.put(commentNotification.commentedCheckinKey, commentNotification);
                    newsItemAdapter.add(commentNotification);
                    requestFocusNotificationIcon();
                    Log.d("NIVRAM", "q c noti key:" + commentNotification.commentedCheckinKey);
                }
                // notification from own checkin while not commented by self

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
    }
    
    private void queryLikeNotification() {
        Query likeNotificationQuery = FirebaseDatabase.getInstance().getReference().child("like_notification").child(mapTag);
        likeNotificationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                nctu.cs.cgv.itour.object.LikeNotification likeNotification = dataSnapshot.getValue(nctu.cs.cgv.itour.object.LikeNotification.class);
                if (likeNotification == null) return;
                if (likeNotification.likedUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        && (likeNotification.likeUid.equals(likeNotification.likedUid) == false)) {
                    likeNotificationMap.put(likeNotification.likedCheckinKey, likeNotification);
                    newsItemAdapter.add(likeNotification);
                    requestFocusNotificationIcon();
                    Log.d("NIVRAM", "q c noti key:" + likeNotification.likedCheckinKey);
                }
                // notification from own checkin while not liked by self

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
    }
    
    void showCheckinDialog(String key) {
        CheckinDialogFragment checkinDialogFragment = CheckinDialogFragment.newInstance(key);
        checkinDialogFragment.show(getFragmentManager(), "fragment_checkin_dialog");
    }
    public void requestFocusNotificationIcon() {
        BottomBar bottomBar = getActivity().findViewById(R.id.bottom_bar);
        ((AppCompatImageView)bottomBar.findViewById(R.id.tab_news).findViewById(R.id.bb_bottom_bar_icon)).setImageResource(R.drawable.ic_notifications_notify_24dp);
//
//        if (notificationIcon.isFocused() == false) {
//            notificationIcon.setFocusable(true);
//            notificationIcon.setFocusableInTouchMode(true);///add this line
//            notificationIcon.requestFocus();
//        }
    }

    public void clearFocusNotificationIcon() {
        BottomBar bottomBar = getActivity().findViewById(R.id.bottom_bar);
        ((AppCompatImageView)bottomBar.findViewById(R.id.tab_news).findViewById(R.id.bb_bottom_bar_icon)).setImageResource(R.drawable.ic_notifications_white_24dp);

    }
}