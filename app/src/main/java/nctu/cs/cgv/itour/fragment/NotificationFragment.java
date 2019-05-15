package nctu.cs.cgv.itour.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.Objects;

import nctu.cs.cgv.itour.activity.MainActivity;
import nctu.cs.cgv.itour.custom.ItemClickSupport;
import nctu.cs.cgv.itour.R;
import nctu.cs.cgv.itour.custom.NotificationItemAdapter;
import nctu.cs.cgv.itour.object.SystemNotification;

import static nctu.cs.cgv.itour.MyApplication.mapTag;
import static nctu.cs.cgv.itour.Utility.actionLog;
import static nctu.cs.cgv.itour.Utility.dpToPx;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    private ActionBar actionBar;
    private NotificationItemAdapter notificationItemAdapter;

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        notificationItemAdapter = new NotificationItemAdapter(getContext(), new ArrayList<SystemNotification>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, parent, false);
    }

    /**
     * Query data
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        RecyclerView newsList = view.findViewById(R.id.recycle_view);
        Context context = getContext();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        DividerItemDecoration itemDecor = new DividerItemDecoration(context, ((LinearLayoutManager)layoutManager).getOrientation());
        newsList.setAdapter(notificationItemAdapter);
        newsList.setLayoutManager(layoutManager);
        newsList.addItemDecoration(itemDecor);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("news").child(mapTag);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SystemNotification systemNotification = dataSnapshot.getValue(SystemNotification.class);
                if (systemNotification == null) return;
                notificationItemAdapter.insert(systemNotification, 0);
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

        ItemClickSupport.addTo(newsList).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        SystemNotification systemNotification = notificationItemAdapter.getItem(position);
                        if (systemNotification.postId.equals("")) {
                            if (systemNotification.location.equals("")) {
                                ((MainActivity) Objects.requireNonNull(getActivity())).onLocateClick(systemNotification.lat, systemNotification.lng);
                            } else {
                                ((MainActivity) Objects.requireNonNull(getActivity())).onLocateClick(systemNotification.location);
                            }
                        } else {
                            ((MainActivity) Objects.requireNonNull(getActivity())).onLocateCheckinClick(systemNotification.postId);
                        }

                        actionLog("click news", systemNotification.msg, systemNotification.postId);
                    }
                }
        );
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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
}